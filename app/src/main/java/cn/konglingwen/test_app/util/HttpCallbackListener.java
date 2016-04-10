package cn.konglingwen.test_app.util;

/**
 * Created by Administrator on 2016/4/10.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
