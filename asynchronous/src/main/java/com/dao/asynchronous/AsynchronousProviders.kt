@file:Suppress("UNCHECKED_CAST")

package com.dao.asynchronous

import androidx.fragment.app.FragmentManager

/**
 * Created in 30/01/19 20:39.
 *
 * @author Diogo Oliveira.
 */
object AsynchronousProviders
{
    fun <T : Asynchronous<*, *>> of(asyncClass: Class<T>, manager: FragmentManager): T
    {
        var asynchronous: T? = null

        try
        {
            asynchronous = asyncClass.newInstance()
            val running = manager.findFragmentByTag(asynchronous!!.tag()) as Asynchronous<*, *>?

            if(running == null)
            {
                val transaction = manager.beginTransaction()
                transaction.add(asynchronous.async, asynchronous.tag()).commit()
            }
            else
            {

                asynchronous = running as T
            }
        }
        catch(e: IllegalAccessException)
        {
            e.printStackTrace()
        }
        catch(e: InstantiationException)
        {
            e.printStackTrace()
        }

        return asynchronous!!
    }
}