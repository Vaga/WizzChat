package vaga.io.wizzchat.api;

import android.os.StrictMode;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RestClient {

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncClient = new SyncHttpClient();
    private static final String URL = "http://wizz.byt.tl";

    public static void addDevice(String owner, String token, AsyncHttpResponseHandler responseHandler) {

        RequestParams params = new RequestParams();
        params.put("owner", md5(owner));
        params.put("token", token);

        syncClient.post(URL + "/devices", params, responseHandler);
    }

    public static void getThreads(String to, AsyncHttpResponseHandler responseHandler) {

        client.get(URL + "/threads?to=" + md5(to), null, responseHandler);
    }

    public static void getMessages(String from, String to, AsyncHttpResponseHandler responseHandler) {

        client.get(URL + "/messages?from=" + md5(from) + "&to=" + md5(to), null, responseHandler);
    }

    public static void postMessage(String from, String to, String data, AsyncHttpResponseHandler responseHandler) {

        RequestParams params = new RequestParams();
        params.put("from", md5(from));
        params.put("to", md5(to));
        params.put("data", data);

        client.post(URL + "/messages", params, responseHandler);
    }

    public static String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return null;
    }
}
