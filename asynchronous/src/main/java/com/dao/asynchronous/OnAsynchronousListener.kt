package com.dao.asynchronous

/**
 * Created in 30/01/19 19:36.
 *
 * @author Diogo Oliveira.
 */
interface OnAsynchronousListener<Result>
{
    fun onBegin(tag: String) {}

    fun onSuccess(tag: String, result: Result?)

    fun onProgress(tag: String, progress: Progress) {}

    fun onCancelled(tag: String, result: Result?) {}

    fun onError(tag: String, e: Exception) {}

    fun onFinish(task: AsyncFinished) {}
}