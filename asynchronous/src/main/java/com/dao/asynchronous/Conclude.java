package com.dao.asynchronous;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

/**
 * Created in 12/11/18 14:14.
 *
 * @author Diogo Oliveira.
 */
public interface Conclude
{
    void cancel();

    boolean isAttach();

    void finish(@NonNull FragmentManager manager);
}
