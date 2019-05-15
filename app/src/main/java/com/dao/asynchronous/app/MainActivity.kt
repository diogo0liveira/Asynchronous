package com.dao.asynchronous.app

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dao.asynchronous.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, OnCompleteAsynchronousListener<Boolean>
{
    private val asyncConclude = AsyncConclude()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeView()
    }

    override fun onDestroy()
    {
        asyncConclude.clear(supportFragmentManager)
        super.onDestroy()
    }

    private fun initializeView()
    {
        buttonRun.setOnClickListener(this)
    }

    override fun onClick(view: View)
    {
        when(view.id)
        {
            R.id.buttonRun ->
            {
                val executeTask = AsynchronousProviders.of(ExecuteTask::class.java, supportFragmentManager)
                asyncConclude.add(executeTask.execute("test"))
            }
        }
    }

    override fun onBegin(tag: String)
    {
        //To change body of created functions
    }

    override fun onSuccess(tag: String, result: Boolean)
    {
        Log.d("TAG", "$tag [$result]")
    }

    override fun onProgress(tag: String, progress: Progress)
    {
        Log.d("TAG", "$tag [$progress]")
    }

    override fun onCancelled(tag: String, result: Boolean)
    {
        Log.d("TAG", "$tag [$result]")
    }

    override fun onError(tag: String, e: Exception)
    {
        Log.d("TAG", "$tag [$e]")
    }

    override fun onFinish(task: Asynchronous)
    {
        Log.d("TAG", "$task")
    }
}
