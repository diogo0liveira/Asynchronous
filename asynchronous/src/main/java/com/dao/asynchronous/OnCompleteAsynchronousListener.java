package com.dao.asynchronous;

/**
 * Created in 06/06/18 08:49.
 *
 * @author Diogo Oliveira.
 */
public interface OnCompleteAsynchronousListener<Result>
{
    void onBegin(String tag);

    void onSuccess(String tag, Result result);

    void onProgress(String tag, Progress progress);

    void onCancelled(String tag, Result result);

    void onError(String tag, Exception e);

    void onFinish(Asynchronous task);
}
