package com.example.jianhong.note.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.jianhong.note.entity.HttpCallbackListener;

public class HttpUtils {

    private static final String TAG = HttpUtils.class.getSimpleName();

    public static String doJsonPost(String urlPath, String Json) {
        StringBuilder result = new StringBuilder();
        BufferedReader reader = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");

            LogUtils.d(TAG, "hello");
            OutputStream wr = conn.getOutputStream();
            LogUtils.d(TAG, "hello1");
            wr.write(Json.getBytes());
            wr.flush();

            LogUtils.d(TAG, "responseCode:"+conn.getResponseCode());
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            }
        } catch (Exception e) {
            LogUtils.d(TAG, "Exception");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }


	public static void sendHttpRequest(final String address, 
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {			
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
                    LogUtils.d(TAG, "hello");
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);

                    LogUtils.d(TAG, "before getInputStream");
					InputStream in = connection.getInputStream();
                    LogUtils.d(TAG, "after getInputStream");
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					
					if (listener != null) {
						listener.onFinish(response.toString());
					}
					
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	
}
