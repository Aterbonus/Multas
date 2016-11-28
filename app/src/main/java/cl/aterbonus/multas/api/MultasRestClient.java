package cl.aterbonus.multas.api;

import android.content.Context;

import com.google.gson.JsonObject;
import com.loopj.android.http.*;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.text.SimpleDateFormat;

import cz.msebera.android.httpclient.entity.StringEntity;

public class MultasRestClient {

    private static final String BASE_URL = "https://multas.aterbonus.cl/api/";
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static AsyncHttpClient client;

    static {

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client = new AsyncHttpClient();
            client.setTimeout(30*1000);
            client.setSSLSocketFactory(socketFactory);
        } catch (Exception e) {
            e.printStackTrace();
            client = new AsyncHttpClient();
        }
        client.setMaxConnections(1);
        client.addHeader("Accept", "application/json");
    }

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {

        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context , String url, String params, JsonHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(params);
        client.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void setToken(String token){
        client.addHeader("Authorization", "Bearer " + token);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
