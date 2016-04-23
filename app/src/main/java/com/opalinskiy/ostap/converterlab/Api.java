package com.opalinskiy.ostap.converterlab;

import android.util.Log;

import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.model.DataResponse;


public class Api {
    public static void getDataResponse(ConnectCallback callback) {
        Connect.getInstance().getRequestWithParam(null, new DataResponse(), callback);
    }
}
