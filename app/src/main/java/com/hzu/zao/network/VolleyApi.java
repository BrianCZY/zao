package com.hzu.zao.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * volley 封装
 * Created by Nearby Yang on 2015-11-16.
 */
public class VolleyApi {

    private static RequestQueue requestQueue;


    //请求类型
    public enum Method {
        GET, POST
    }

    public static RequestQueue getInstence(Context context) {
        if (requestQueue == null) {
            synchronized (VolleyApi.class) {
                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                    //使用默认的volley请求
//                    requestQueue = Volley.newRequestQueue(ApplicationConfig.getContext(), new SslHttpStack(true));

                    requestQueue.start();
                }
            }
        }

        return requestQueue;
    }

    /**
     * @param context
     * @param requestMethod
     * @param url
     * @param params
     * @param requestTag
     * @param tagClazz
     */
    public void bmobNetWorkRequest(Context context, Method requestMethod, String url, HashMap<String, String> params,
                                   String requestTag, Class tagClazz) {

        int method = Request.Method.GET;

        switch (requestMethod) {
            case GET:

                StringBuilder paramsStr = new StringBuilder("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    paramsStr = paramsStr.append(entry.getKey());
                    paramsStr = paramsStr.append("=");
                    paramsStr = paramsStr.append(entry.getValue());
                    paramsStr.append("&");
                }


                url = url + paramsStr.toString().trim();
                url = url.substring(0, url.length() - 1);//获取真的url地址

                method = Request.Method.GET;

                break;

            case POST:

                method = Request.Method.POST;

                break;

        }

        @SuppressWarnings("unchecked")
        BmobJSONRequest jr = new BmobJSONRequest(method, url, tagClazz, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        jr.setTag(requestTag);
        getInstence(context).add(jr);

    }



}
