package com.opalinskiy.ostap.converterlab.interfaces;

public interface ConnectCallback {
    void onSuccess(Object object);

    void onFailure(Throwable throwable, String errorMessage);

}
