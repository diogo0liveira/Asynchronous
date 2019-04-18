package com.dao.asynchronous;

/**
 * Created in 06/11/18 12:42.
 *
 * @author Diogo Oliveira.
 */
interface AsyncCallback<Params, Result>
{
    void onPreExecute();

    @SuppressWarnings("unchecked")
    Result doInBackground(Params... params);

    void onProgressUpdate(Progress progress);

    void onPostExecute(Result result);

    void onCancelled(Result result);
}
