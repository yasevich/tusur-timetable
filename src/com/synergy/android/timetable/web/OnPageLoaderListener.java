package com.synergy.android.timetable.web;

public interface OnPageLoaderListener {
    public void onPreExecute();
    public void onPostExecute(String pageData);
}
