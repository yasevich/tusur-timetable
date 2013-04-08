package com.synergy.android.timetable.web;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebPageUtils {
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;
    
    private static final String DEFAULT_CHARSET = "UTF-8";
    
    public static String readPage(String url) throws IOException {
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
        
        HttpClient client = new DefaultHttpClient(params);
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        return responseToString(response);
    }
    
    public static String responseToString(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        Header contentEncoding = entity.getContentEncoding();
        String charset = DEFAULT_CHARSET;
        if (contentEncoding != null) {
            charset = contentEncoding.getValue();
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),
                charset));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }
}
