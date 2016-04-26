package com.opalinskiy.ostap.converterlab.services;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.opalinskiy.ostap.converterlab.api.Api;
import com.opalinskiy.ostap.converterlab.constants.Constants;
import com.opalinskiy.ostap.converterlab.utils.databaseUtils.DbManager;
import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.model.DataResponse;
import com.opalinskiy.ostap.converterlab.model.Organisation;

import java.util.List;

public class LoaderService extends IntentService {
    DbManager dbManager;
    List<Organisation> organisations;
    public LoaderService() {
        super("LoaderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(Constants.LOG_TAG, "handle intent service");
        dbManager = new DbManager(getApplicationContext());
        dbManager.open();

        Api.getDataResponseSynchronous(getApplicationContext(), new ConnectCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d(Constants.LOG_TAG, "On success in service");
                DataResponse dataResponse = (DataResponse) object;
                organisations = dataResponse.getOrganisations();
                dbManager.setRatesVariationForList(organisations);
                dbManager.writeAllDataToDb(dataResponse);
            }

            @Override
            public void onFailure() {
                Log.d(Constants.LOG_TAG, "On failure in service");

            }

            @Override
            public void onProgress(long percentage) {

            }
        });

    }
}
