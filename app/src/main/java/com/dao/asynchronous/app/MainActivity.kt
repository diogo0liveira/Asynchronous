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
    private lateinit var progressDialog: ProgressDialog

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

    private fun showProgressDialog()
    {
        progressDialog = ProgressDialog.newInstance()
                .title("Assistente Venda")
                .message("gerando pedido sugestão ...")
                .progressStyle()
                .maxValue(100)

        progressDialog.show(supportFragmentManager)
    }

    private fun hideProgressDialog()
    {
        progressDialog.dismiss()
    }

    override fun onClick(view: View)
    {
        when(view.id)
        {
            R.id.buttonRun ->
            {
//                val executeTask = AsynchronousProviders.of(ExecuteTask::class.java, supportFragmentManager)
//                asyncConclude.add(executeTask.execute("test"))
            }
        }
    }

    override fun onBegin(tag: String)
    {
        showProgressDialog()
    }

    override fun onSuccess(tag: String, result: Boolean)
    {
        Log.d("TAG", "$tag [$result]")
    }

    override fun onProgress(tag: String, progress: Progress)
    {
        progressDialog.setMaxValue(progress.total)
        progressDialog.incrementBy(progress.increment)
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
        hideProgressDialog()
        Log.d("TAG", "$task")
    }
}
