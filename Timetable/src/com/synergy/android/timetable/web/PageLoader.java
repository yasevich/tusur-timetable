package com.synergy.android.timetable.web;

import android.os.AsyncTask;

import java.io.IOException;

public class PageLoader extends AsyncTask<String, Void, String> {
    private OnPageLoaderListener listener;
    
    public PageLoader(OnPageLoaderListener listener) {
        this.listener = listener;
    }
    
    @Override
    protected void onPreExecute() {
        if (listener != null) {
            listener.onPreExecute();
        }
    }
    
    @Override
    protected String doInBackground(String... params) {
        try {
            return WebPageUtils.readPage(params[0]);
        } catch (IOException e) {
            return null;
        }
    }
    
    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onPostExecute(result);
        }
    }
}
