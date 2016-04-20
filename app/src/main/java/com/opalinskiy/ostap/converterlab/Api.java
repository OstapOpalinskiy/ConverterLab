package com.opalinskiy.ostap.converterlab;

import android.util.Log;

import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.response.OrganisationResponse;


public class Api {
    //returns array of all posts
    public static void getOrgs(ConnectCallback callback) {
        Log.d("TAG", "getOrgs");
        Connect.getInstance().getRequestWithParam(null, new OrganisationResponse(), callback);
    }
}
