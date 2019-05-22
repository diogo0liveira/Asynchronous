package com.dao.asynchronous;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created in 26/11/18 15:41.
 *
 * @author Diogo Oliveira.
 */
@SuppressWarnings({"unchecked", "WeakerAccess"})
abstract class BaseAsynchronous<Params, Result> extends Asynchronous implements AsyncCallback<Params, Result>, AsyncContextCallback
{
    protected OnCompleteAsynchronousListener<Result> listener;
    protected Progress progress;
    protected Exception error;

    private Task<Params, Result> task;
    private Result result = null;

    private State state = State.NONE;
    private boolean isLifecycleOwner = true;
    private boolean hasToReturn;
    private boolean isFinalized;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(@NotNull Context context)
    {
        super.onAttach(context);

        if(context instanceof OnCompleteAsynchronousListener)
        {
            if(listener == null)
            {
                //noinspection unchecked
                listener = (OnCompleteAsynchronousListener<Result>)context;
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(isLifecycleOwner && hasListener())
        {
            switch(state)
            {
                case BEGIN:
                {
                    onPreExecute();

                    if(progress != null)
                    {
                        publishProgress(progress);
                    }
                    break;
                }
                case SUCCESS:
                case CANCELLED:
                {
                    if(hasToReturn)
                    {
                        if(isCancelled())
                        {
                            onCancelled(result);
                        }
                        else
                        {
                            onPostExecute(result);
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onDetach()
    {
        listener = null;
        super.onDetach();
    }

    @Override
    public void cancel()
    {
        cancel(true);
    }

    @Override
    public boolean isAttach()
    {
        return this.isAdded();
    }

    @Override
    public void onPreExecute()
    {
        if(hasListener())
        {
            if(isLifecycleOwner)
            {
                state = State.BEGIN;

                if(isResumed())
                {
                    listener.onBegin(getTag());
                }
            }
            else
            {
                listener.onBegin(getTag());
            }
        }
    }

    public abstract Result doInBackground(Params... params);

    protected void publishProgress(Progress progress)
    {
        if(task != null)
        {
            this.progress = progress;
            task.publishProgress(progress);
        }
    }

    @Override
    public void onProgressUpdate(Progress progress)
    {
        if(hasListener())
        {
            if(!isLifecycleOwner || isResumed())
            {
                listener.onProgress(tag(), progress);
            }
        }
    }

    @Override
    public void onPostExecute(Result result)
    {
        if(hasListener())
        {
            if(isAttach())
            {
                if(isLifecycleOwner && !isResumed())
                {
                    hasToReturn = true;
                    this.result = result;
                    state = State.SUCCESS;
                }
                else
                {
                    if(error == null)
                    {
                        if(isCancelled())
                        {
                            listener.onCancelled(getTag(), result);
                        }
                        else
                        {
                            listener.onSuccess(getTag(), result);
                        }
                    }
                    else
                    {
                        listener.onError(getTag(), error);
                    }

                    listener.onFinish(this);

                    this.state = State.NONE;
                    hasToReturn = false;
                    this.result = null;
                }
            }
        }
    }

    @Override
    public void onCancelled(Result result)
    {
        if(hasListener())
        {
            if(isLifecycleOwner && !isResumed())
            {
                hasToReturn = true;
                this.result = result;
                state = State.CANCELLED;
            }
            else
            {
                listener.onCancelled(tag(), result);
                listener.onFinish(this);

                this.state = State.NONE;
                hasToReturn = false;
                this.result = null;
            }
        }
    }

    /**
     * Iniciar a execução da task.
     *
     * @param params parametros para execução da task.
     */
    @SafeVarargs
    protected final Conclude run(Params... params)
    {
        if(isFinalized)
        {
            throw new IllegalStateException("Asynchronous: " + tag() + " não pode ser executado após a chamada de finish.");
        }

        if(isRunning())
        {
            cancel();
        }

        reset();
        task = new Task<>(this);
        progress = new Progress();

        if(params.length > 0)
        {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
        else
        {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Params)null);
        }
        return this;
    }

    @Override
    public void finish(@NonNull FragmentManager manager)
    {
        finish(manager, false);
    }

    private void finish(@NonNull FragmentManager manager, boolean restart)
    {
        if(!restart)
        {
            listener = null;
            isFinalized = true;
        }

        reset();

        if(getArguments() != null)
        {
            getArguments().clear();
        }

        FragmentTransaction transaction = manager.beginTransaction();
        Asynchronous running = (Asynchronous)manager.findFragmentByTag(tag());

        if(running != null)
        {
            running.cancel();
            transaction.remove(running).commitNowAllowingStateLoss();
        }
    }

    public void attachListener(OnCompleteAsynchronousListener<Result> listener)
    {
        this.listener = listener;
    }

    /**
     * Adiciona o Asynchronous para ser executado. Quando adicionado manualmente
     * será necessário chamar o método {@link #finish(FragmentManager)}
     *
     * @param manager que será adicionado o Asynchronous.
     */
    public void start(@NonNull FragmentManager manager, final Params... params)
    {
        finish(manager, true);
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(this, tag())
                .runOnCommit(() -> BaseAsynchronous.this.run(params))
                .commitNowAllowingStateLoss();
    }

    public final boolean cancel(boolean interruptIfRunning)
    {
        boolean result = false;

        if(task != null)
        {
            task.interrupted = interruptIfRunning;
            result = task.cancel(interruptIfRunning);

            if(interruptIfRunning)
            {
                onPostExecute(null);
            }
        }

        return result;
    }

    /**
     * Se o Asynchronous deve respeitar o ciclo de vida de que esta "attached" (Activity/Fragment).
     * Com essa função o Asynchronous esperará ate que o hospedeiro esteja "isResumed" para enviar
     * os resultado pelo ouvinte adicionado por {@link #attachListener(OnCompleteAsynchronousListener)}.
     *
     * @param value true para respeitar o clico de vida do hospedeiro.
     */
    public final void setLifecycleOwner(boolean value)
    {
        this.isLifecycleOwner = value;
    }

    public final boolean isCancelled()
    {
        return ((task == null) || task.isCancelled());
    }

    public final boolean isRunning()
    {
        return ((task != null) && ((!task.isCancelled() && !task.interrupted) &&
                (task.getStatus() == AsyncTask.Status.RUNNING)));
    }

    public String tag()
    {
        return getClass().getSimpleName();
    }

    boolean hasListener()
    {
        return (listener != null);
    }

    private void reset()
    {
        error = null;
        result = null;
        progress = null;
        hasToReturn = false;
        state = State.NONE;
    }

    protected static class Task<Params, Result> extends AsyncTask<Params, Progress, Result>
    {
        boolean interrupted = false;
        private AsyncCallback<Params, Result> callback;

        protected Task(AsyncCallback<Params, Result> callback)
        {
            this.callback = callback;
        }

        void publishProgress(Progress progress)
        {
            super.publishProgress(progress);
        }

        @Override
        protected void onPreExecute()
        {
            callback.onPreExecute();
        }

        @Override
        @SafeVarargs
        protected final Result doInBackground(Params... params)
        {
            return callback.doInBackground(params);
        }

        @Override
        protected final void onProgressUpdate(Progress... values)
        {
            if((values != null) && values.length > 0)
            {
                callback.onProgressUpdate(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Result result)
        {
            callback.onPostExecute(result);
            finish();
        }

        @Override
        protected void onCancelled(Result result)
        {
            if(!interrupted)
            {
                callback.onCancelled(result);
            }

            finish();
        }

        private void finish()
        {
            callback = null;
            interrupted = false;
        }
    }

    public enum State
    {
        NONE, BEGIN, SUCCESS, CANCELLED
    }
}
