package com.dao.asynchronous;

/**
 * Created in 26/11/18 15:25.
 *
 * @author Diogo Oliveira.
 */
public abstract class VarargsAsynchronous<Params, Result> extends BaseAsynchronous<Params, Result>
{
    @SafeVarargs
    public final Conclude execute(Params... params)
    {
        return super.run(params);
    }
}
