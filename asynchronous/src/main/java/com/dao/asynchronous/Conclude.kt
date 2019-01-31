package com.dao.asynchronous

/**
 * Created in 31/01/19 19:59.
 *
 * @author Diogo Oliveira.
 */
interface Conclude : AsyncFinished
{
    fun isAttach(): Boolean

    fun cancel(interruptIfRunning: Boolean = true): Boolean
}