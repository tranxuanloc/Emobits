package com.tranxuanloc.emobits.retrofit;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.tranxuanloc.emobits.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * Created by Trần Xuân Lộc on 1/3/2016.
 */
public class RetrofitError {
    private static Snackbar snackbar;

    public static void errorNoAction(Context context, Throwable error, String TAG, View view) {
        if (error instanceof NoInternet) {
            snackbar = Snackbar.make(view, context.getString(R.string.no_internet), Snackbar.LENGTH_LONG);
        } else if (error instanceof SocketTimeoutException || error instanceof ConnectException) {
            snackbar = Snackbar.make(view, context.getString(R.string.timeout_conn), Snackbar.LENGTH_LONG);
        } else {
            snackbar = Snackbar.make(view, context.getString(R.string.error_system), Snackbar.LENGTH_LONG);
        }
        Log.e(TAG, error.getClass().getName());
        snackbar.show();
    }

    public static void errorWithAction(Context context, Throwable error, String TAG, View view, View.OnClickListener action) {
        if (error instanceof NoInternet) {
            snackbar = Snackbar.make(view, context.getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, action);
        } else if (error instanceof SocketTimeoutException || error instanceof ConnectException) {
            snackbar = Snackbar.make(view, context.getString(R.string.timeout_conn), Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, action);
        } else {
            snackbar = Snackbar.make(view, context.getString(R.string.error_system), Snackbar.LENGTH_INDEFINITE).setAction(R.string.try_again, action);
        }
        Log.e(TAG, error.getClass().getName());
        snackbar.show();
    }

    public static String getErrorMessage(Context context, Throwable error) {
        if (error instanceof NoInternet)
            return context.getString(R.string.no_internet);
        else if (error instanceof SocketTimeoutException || error instanceof ConnectException)
            return context.getString(R.string.timeout_conn);
        else
            return context.getString(R.string.error_system);
    }

    public static Snackbar getSnackbar() {
        return snackbar;
    }
}
