package com.dao.asynchronous

import androidx.fragment.app.FragmentManager
import java.util.*

/**
 * Created in 30/01/19 20:34.
 *
 * @author Diogo Oliveira.
 */
class AsyncConclude(vararg concludes: Conclude)
{
    private var concludes: MutableSet<Conclude> = mutableSetOf()

    init
    {
        if(concludes.isNotEmpty())
        {
            this.concludes.addAll(concludes)
        }
    }

    fun add(conclude: Conclude)
    {
        if(conclude.isAttach())
        {
            return
        }

        synchronized(this) {
            if(!conclude.isAttach())
            {
                var rs: MutableSet<Conclude>? = concludes
                if(rs == null)
                {
                    rs = HashSet(4)
                    concludes = rs
                }
                rs.add(conclude)
                return
            }

            conclude.cancel()
        }
    }

    fun remove(conclude: Conclude)
    {
        synchronized(this) {
            if(concludes.isEmpty() || !concludes.remove(conclude))
            {
                return
            }
        }

        conclude.cancel()
    }

    fun clear(manager: FragmentManager)
    {
        for(conclude in concludes)
        {
            conclude.finish(manager)
        }
    }
}