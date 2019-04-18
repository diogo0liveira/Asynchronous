package com.dao.asynchronous.app

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
        return (param.contains("test"))
    }
}