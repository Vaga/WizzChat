package vaga.io.wizzchat.api;

import android.os.StrictMode;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import vaga.io.wizzchat.utils.Md5;

public class RestClient {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncClient = new SyncHttpClient();
    private static final String URL = "http://wizz.byt.tl";

    public static void addDevice(String owner, String token, AsyncHttpResponseHandler responseHandler) {

        RequestParams params = new RequestParams();
        params.put("owner", owner);
        params.put("token", token);

        syncClient.post(URL + "/devices", params, responseHandler);
    }

    public static void getThreads(String to, AsyncHttpResponseHandler responseHandler) {

        client.get(URL + "/threads?to=" + to, null, responseHandler);
    }

    public static void getMessages(String from, String to, AsyncHttpResponseHandler responseHandler) {

        client.get(URL + "/messages?from=" + from + "&to=" + to, null, responseHandler);
    }

    public static void postMessage(String from, String to, String data, AsyncHttpResponseHandler responseHandler) {

        RequestParams params = new RequestParams();
        params.put("from", from);
        params.put("to", to);
        params.put("data", data);

        client.post(URL + "/messages", params, responseHandler);
    }
}
