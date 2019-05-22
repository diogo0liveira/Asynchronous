package com.dao.asynchronous.app

import android.os.SystemClock
import com.dao.asynchronous.SingleAsynchronous

/**
 * Created in 30/01/19 19:05.
 *
 * @author Diogo Oliveira.
 */
class ExecuteTask : SingleAsynchronous<String, Boolean>()
{
    override fun doInBackground(param: String): Boolean
    {
        progress.message = getString(R.string.app_name)
        progress.total = 550

        for(i in 0..550)
        {
            progress.increment = 1
            publishProgress(progress)
            SystemClock.sleep(120)
        }

        return (param.contains("test"))
    }
}