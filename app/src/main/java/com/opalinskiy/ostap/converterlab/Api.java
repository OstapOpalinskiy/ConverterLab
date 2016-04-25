package com.opalinskiy.ostap.converterlab;

import android.content.Context;
import android.util.Log;

import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.model.DataResponse;

import java.io.IOException;


public class Api {
    public static void getDataResponse(ConnectCallback callback) {
        Connect.getInstance().getRequestWithParam(null, new DataResponse(), callback);
    }

    public static void getDataResponseSynchronous(Context context, ConnectCallback callback) {
        try {
            Connect.getInstance().getRequestSynchronous(context, new DataResponse(), callback);
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }
}
