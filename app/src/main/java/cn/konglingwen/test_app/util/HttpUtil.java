package cn.konglingwen.test_app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/4/10.
 */
public class HttpUtil {
    public static final String UTF8_BOM = "\uFEFF";

    private static String removeUTF8BOM(String s){
        if(s.startsWith(UTF8_BOM)){
            s = s.substring(1);
        }
        return s;
    }

    public static void sendHttpRequest(final String address,
            final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }

                    if(listener != null){
                        listener.onFinish(removeUTF8BOM(response.toString()));
                    }
                }catch (Exception e){
                    if (listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
