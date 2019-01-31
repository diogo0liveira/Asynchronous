package com.dao.asynchronous

import androidx.fragment.app.FragmentManager
import com.dao.asynchronous.core.AsyncCore
import com.dao.asynchronous.core.AsyncCoreCallback

/**
 * Created in 31/01/19 18:28.
 *
 * @author Diogo Oliveira.
 */
abstract class Asynchronous<Params, Result>: AsyncFinished
{
    internal val async: AsyncCore<Params, Result>
        get() = AsyncCore(asyncCoreCallback, tag())

    abstract fun doInBackground(vararg params: Params): Result

    /**
     * Iniciar a execução da task.
     *
     * @param params parametros para execução da task.
     */
    @SafeVarargs
    fun execute(vararg params: Params): Conclude
    {
        return async.run(*params)
    }

    /**
     * Registre um callback para retorno de chamadas do "Asynchronous".
     */
    fun attachListener(listener: OnAsynchronousListener<Result>)
    {
        async.setListener(listener)
    }

    /**
     * Finaliza o "Asynchronous", após isso será removido deu seu hospedeiro (Activity/Fragment).
     */
    override fun finish(manager: FragmentManager)
    {
        async.finish(manager)
    }

    /**
     * Se o Asynchronous deve respeitar o ciclo de vida de que esta "attached" (Activity/Fragment).
     * Com essa função o Asynchronous esperará ate que o hospedeiro esteja "isResumed" para enviar
     * os resultado pelo ouvinte adicionado por [.attachListener].
     *
     * @param value true para respeitar o clico de vida do hospedeiro.
     */
    fun setLifecycleOwner(value: Boolean)
    {
        async.isLifecycleOwner = value
    }

    fun publishProgress(progress: Progress)
    {
        async.publishProgress(progress)
    }

    fun isCancelled(): Boolean
    {
        return async.isCancelled()
    }

    fun isRunning(): Boolean
    {
        return async.isRunning()
    }

    open fun tag(): String
    {
        return this.javaClass.simpleName
    }

    fun cancel()
    {
        async.cancel()
    }

    private val asyncCoreCallback = object: AsyncCoreCallback<Params, Result>
    {
        override fun inBackground(vararg params: Params): Result
        {
            return doInBackground(*params)
        }
    }
}