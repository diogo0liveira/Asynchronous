package com.dao.asynchronous.app;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class ProgressDialog extends DialogFragment
{
    private final static String TAG = ProgressDialog.class.getSimpleName();
    private android.app.ProgressDialog progress;

    private String title;
    private String message;

    private boolean isProgressStyle;
    private int maxValue = 100;

    public static ProgressDialog newInstance()
    {
//        Bundle args = new Bundle();
//        ProgressDialog fragment = new ProgressDialog();
//        fragment.setArguments(args);
//        return fragment;

        return new ProgressDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        progress = new android.app.ProgressDialog(getContext());

        progress.setTitle(title);
        progress.setMessage(message);

        if(isProgressStyle)
        {
            progress.setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(false);
            progress.setMax(maxValue);
        }

        setCancelable(false);
        return progress;
    }

    public void show(FragmentManager manager)
    {
        DialogFragment dialog = (DialogFragment)manager.findFragmentByTag(TAG);

        if(dialog != null)
        {
            dialog.dismissAllowingStateLoss();
        }

        show(manager, TAG);
    }

    public ProgressDialog title(String title)
    {
        this.title = title;
        return this;
    }

    public ProgressDialog message(String message)
    {
        this.message = message;
        return this;
    }

    public ProgressDialog progressStyle()
    {
        isProgressStyle = true;
        return this;
    }

    public ProgressDialog indeterminateStyle()
    {
        isProgressStyle = true;
        return this;
    }

    public ProgressDialog maxValue(int max)
    {
        maxValue = max;
        return this;
    }

    public void setProgress(int value)
    {
        progress.setProgress(value);
    }

    public void incrementBy(int value)
    {
        progress.incrementProgressBy(value);
    }

    public void setMaxValue(int max)
    {
        progress.setMax(max);
    }
}
