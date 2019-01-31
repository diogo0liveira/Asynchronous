@file:Suppress("UNCHECKED_CAST")

package com.dao.asynchronous.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dao.asynchronous.Asynchronous
import com.dao.asynchronous.Conclude
import com.dao.asynchronous.OnAsynchronousListener
import com.dao.asynchronous.Progress
import java.lang.ref.WeakReference

/**
 * Created in 31/01/19 18:29.
 *
 * @author Diogo Oliveira.
 */
internal class AsyncCore<Params, Result>(
        private val asyncCoreCallback: AsyncCoreCallback<Params, Result>,
        private val taget: String) : Fragment(), Conclude
{
    private var listener: WeakReference<OnAsynchronousListener<Result>>? = null
    private var progress: Progress? = null
    private var error: Exception? = null

    private var state = State.NONE
    private var hasToReturn = false
    private var isFinalized = false
    internal var isLifecycleOwner = true

    private var task: TaskCore? = null
    private var result: Result? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)

        if(context is OnAsynchronousListener<*>)
        {
            if(listener == null)
            {
                listener = WeakReference(context as OnAsynchronousListener<Result>)
            }
        }
    }

    override fun isAttach(): Boolean
    {
        return this.isAdded
    }

    override fun onResume()
    {
        super.onResume()

        listener?.get()?.let {
            if(isLifecycleOwner)
            {
                when(state)
                {
                    State.BEGIN ->
                    {
                        preExecute()

                        progress?.let {
                            publishProgress(it)
                        }
                    }
                    State.SUCCESS, State.CANCELLED ->
                    {
                        if(hasToReturn)
                        {
                            if(isCancelled())
                            {
                                cancelled(result)
                            }
                            else
                            {
                                postExecute(result)
                            }
                        }
                    }
                    else ->
                    {
                    }
                }
            }
        }
    }

    override fun onDetach()
    {
        listener?.clear()
        super.onDetach()
    }

    override fun cancel(interruptIfRunning: Boolean): Boolean
    {
      return task?.run {
            interrupted = interruptIfRunning
            interrupted = cancel(interruptIfRunning)

            if(interruptIfRunning)
            {
                postExecute(null)
            }

          interrupted
        }!!
    }

    override fun finish(manager: FragmentManager)
    {
        finish(manager, false)
    }

    fun preExecute()
    {
        listener?.get()?.let {
            if(isLifecycleOwner)
            {
                state = State.BEGIN

                if(isResumed)
                {
                    it.onBegin(taget)
                }
            }
            else
            {
                it.onBegin(taget)
            }
        }
    }

    fun inBackground(vararg params: Params): Result
    {
        return asyncCoreCallback.inBackground(*params)
    }

    fun publishProgress(progress: Progress)
    {
        task?.let {
            this.progress = progress
            it.publishProgress(progress)
        }
    }

    fun progressUpdate(progress: Progress)
    {
        listener?.get()?.let {
            if(!isLifecycleOwner || isResumed)
            {
                it.onProgress(taget, progress)
            }
        }
    }

    fun postExecute(result: Result?)
    {
        listener?.get()?.let {
            if(isAdded)
            {
                if(isLifecycleOwner && !isResumed)
                {
                    hasToReturn = true
                    this.result = result
                    state = State.SUCCESS
                }
                else
                {
                    if(error == null)
                    {
                        it.onSuccess(taget, result)
                    }
                    else
                    {
                        it.onError(taget, error!!)
                    }

                    it.onFinish(this)
                    this.state = State.NONE
                    hasToReturn = false
                    this.result = null
                }
            }
        }
    }

    fun cancelled(result: Result?)
    {
        listener?.get()?.let {
            if(isLifecycleOwner && !isResumed)
            {
                hasToReturn = true
                this.result = result
                state = State.CANCELLED
            }
            else
            {
                it.onCancelled(taget, result)
                it.onFinish(this)

                this.state = State.NONE
                hasToReturn = false
                this.result = null
            }
        }
    }

    fun isCancelled(): Boolean
    {
        return task?.takeIf { it.isCancelled } == null
    }

    fun isRunning(): Boolean
    {
        return task?.takeIf { !it.isCancelled /*&& !it.interrupted*/ && it.status == AsyncTask.Status.RUNNING } != null
    }

    @SafeVarargs
    fun run(vararg params: Params): Conclude
    {
        if(isFinalized)
        {
            throw IllegalStateException("Asynchronous: $taget não pode ser executado após a chamada de finish.")
        }

        if(isRunning())
        {
            cancel()
        }

        task = TaskCore().apply {
            if(params.isNotEmpty())
            {
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, *params)
            }
            else
            {
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null as Params?)
            }
        }

        return this
    }

    fun setListener(listener: OnAsynchronousListener<Result>)
    {
        this.listener = WeakReference(listener)
    }

    private fun finish(manager: FragmentManager, restart: Boolean)
    {
        if(!restart)
        {
            listener = null
            isFinalized = true
        }

        result = null
        hasToReturn = false
        state = State.NONE

        arguments?.clear()
        val running = manager.findFragmentByTag(taget) as Asynchronous<*, *>?

        running?.let {
            val transaction = manager.beginTransaction()
            it.cancel()
            transaction.remove(it.async).commit()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class TaskCore : AsyncTask<Params, Progress, Result>()
    {
        internal var interrupted = false

        override fun onPreExecute()
        {
            preExecute()
        }

        internal fun publishProgress(progress: Progress)
        {
            super.publishProgress(progress)
        }

        override fun doInBackground(vararg params: Params): Result
        {
            return inBackground(*params)
        }

        override fun onProgressUpdate(vararg values: Progress)
        {
            if(values.isNotEmpty())
            {
                progressUpdate(values[0])
            }
        }

        override fun onPostExecute(result: Result)
        {
            postExecute(result)
        }

        override fun onCancelled(result: Result)
        {
            cancelled(result)
        }
    }

    internal enum class State
    {
        NONE, BEGIN, SUCCESS, CANCELLED
    }
}