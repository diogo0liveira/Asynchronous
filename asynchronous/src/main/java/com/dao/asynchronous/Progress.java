package com.dao.asynchronous;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * Created in 08/11/18 11:16.
 *
 * @author Diogo Oliveira.
 */
public class Progress
{
    private int percentage;
    private int increment;
    private int total;
    private int value;

    private String message;
    private boolean isFailed;

    public Progress()
    {
        this(0, null);
    }

    public Progress(int percentage, String message)
    {
        this.percentage = percentage;
        this.message = message;
        this.increment = 1;
        this.value = 0;
    }

    public int getIncrement()
    {
        return increment;
    }

    public void setIncrement(int increment)
    {
        this.value += increment;
        this.increment = increment;
        this.percentage = (int)(((float)value * 100) / total);
    }

    public float getPercentage()
    {
        return percentage;
    }

    public void setPercentage(int percentage)
    {
        this.percentage = percentage;
        this.value = (int)(((float)percentage / 100) * total);
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal(int total)
    {
        this.total = total;
        this.value = (int)(((float)percentage / 100) * total);
        this.percentage = (int)(((float)value * 100) / total);
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean isFailed()
    {
        return isFailed;
    }

    public void setFailed(boolean failed)
    {
        isFailed = failed;
    }

    @NonNull
    @Override
    public String toString()
    {
        return (String.format("%1$s - %2$s",
                String.format(Locale.getDefault(), "%1$d%%", percentage), message));
    }
}
