package com.dao.asynchronous;

/**
 * Created in 26/11/18 14:33.
 *
 * @author Diogo Oliveira.
 */
public abstract class SingleAsynchronous<Param, Result> extends BaseAsynchronous<Param, Result> {

    @Override
    @SafeVarargs
    public final Result doInBackground(Param... params) {
        return doInBackground(params[0]);
    }

    public final Conclude execute(Param param) {
        return super.run(param);
    }

    public final Conclude execute() {
        return super.run();
    }

    public abstract Result doInBackground(Param param);
}
