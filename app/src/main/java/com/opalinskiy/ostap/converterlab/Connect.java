package com.opalinskiy.ostap.converterlab;


import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.interfaces.ModelResponse;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class Connect {
    public static final int PARSER_GSON = 10;
    public static final int PARSER_JSON = 11;
    private static final String LOG_TAG = Connect.class.getName();

    private static Connect _instance;
    private AsyncHttpClient client;
    private int mParser = PARSER_JSON;

    private Connect(){}

    public static Connect getInstance() {
        if (_instance == null) {
            _instance = new Connect();
            _instance.client = new AsyncHttpClient();
        }

        return _instance;
    }

    public void getRequestWithParam(RequestParams requestParams,final ModelResponse modelResponse , final ConnectCallback callback) {
        Log.d("TAG", "getRequestWithParams");
        client.get("http://resources.finance.ua/ru/public/currency-cash.json",requestParams,new JsonHttpResponseHandler()
        {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("TAG", "succuess");
                        parseData(response, modelResponse, callback);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d("TAG", "succuessArray");
                        parseData(response, modelResponse, callback);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.d("TAG", "failure");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.d("TAG", "failureArray");
                    }
                }
        );
    }

    private void parseData(Object jsonObject, ModelResponse modelObject, ConnectCallback callback) {
        if (null != modelObject) {
            try {

                modelObject.configure(jsonObject);
                callback.onSuccess(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public int getParser() {
        return mParser;
    }
}
