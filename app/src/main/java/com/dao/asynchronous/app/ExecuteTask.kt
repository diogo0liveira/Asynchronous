package com.dao.asynchronous.app

import com.dao.asynchronous.Asynchronous

/**
 * Created in 30/01/19 19:05.
 *
 * @author Diogo Oliveira.
 */
class ExecuteTask: Asynchronous<String, Boolean>()
{
    override fun doInBackground(vararg params: String): Boolean
    {
        return (params.contains("test"))
    }

}