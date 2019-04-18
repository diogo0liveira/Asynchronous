package com.dao.asynchronous;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

/**
 * Created in 12/11/18 13:47.
 *
 * @author Diogo Oliveira.
 */
public final class AsyncConclude
{
    private Set<Conclude> concludes;

    public AsyncConclude(Conclude... concludes)
    {
        if(concludes != null)
        {
            this.concludes = new HashSet<>(concludes.length * 4 / 3 + 1);
            this.concludes.addAll(Arrays.asList(concludes));
        }
    }

    public AsyncConclude(Iterable<? extends Conclude> concludes)
    {
        if(concludes != null)
        {
            this.concludes = new HashSet<>();

            for(Conclude conclude : concludes)
            {
                this.concludes.add(conclude);
            }
        }
    }

    public void add(Conclude conclude)
    {
        if(conclude.isAttach())
        {
            return;
        }

        synchronized(this)
        {
            if(!conclude.isAttach())
            {
                Set<Conclude> rs = concludes;

                if(rs == null)
                {
                    rs = new HashSet<>(4);
                    concludes = rs;
                }

                rs.add(conclude);
                return;
            }

            conclude.cancel();
        }
    }

    public void remove(Conclude conclude)
    {
        synchronized(this)
        {
            if(concludes == null || !concludes.remove(conclude))
            {
                return;
            }
        }

        conclude.cancel();
    }

    public void clear(@NonNull FragmentManager manager)
    {
        for(Conclude conclude : concludes)
        {
            conclude.finish(manager);
        }
    }
}
