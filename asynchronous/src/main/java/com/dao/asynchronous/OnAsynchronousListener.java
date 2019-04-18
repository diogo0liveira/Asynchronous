package com.dao.asynchronous;

/**
 * Created in 08/11/18 11:00.
 *
 * @author Diogo Oliveira.
 */
public abstract class OnAsynchronousListener<Result> implements OnCompleteAsynchronousListener<Result>
{
    @Override
    public void onBegin(String tag) { }

    @Override
    public void onSuccess(String tag, Result result) { }

    @Override
    public void onProgress(String tag, Progress progress) { }

    @Override
    public void onCancelled(String tag, Result result) { }

    @Override
    public void onError(String tag, Exception e) { }

    @Override
    public void onFinish(Asynchronous task) { }
}
