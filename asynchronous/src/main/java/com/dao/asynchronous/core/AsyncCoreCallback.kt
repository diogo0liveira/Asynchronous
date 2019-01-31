package com.dao.asynchronous.core

/**
 * Created in 31/01/19 19:37.
 *
 * @author Diogo Oliveira.
 */
interface AsyncCoreCallback<Params, Result>
{
    fun inBackground(vararg params: Params): Result
}