package com.dao.asynchronous.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dao.asynchronous.AsyncConclude
import com.dao.asynchronous.AsynchronousProviders
import com.dao.asynchronous.OnAsynchronousListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, OnAsynchronousListener<Boolean>
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

    override fun onSuccess(tag: String, result: Boolean?)
    {
        if(result != null)
        {

        }
    }
}
