package com.opalinskiy.ostap.converterlab.interfaces;


import com.opalinskiy.ostap.converterlab.model.Organisation;

public interface EventHandler {
    void onOpenLink(Organisation org);

    void onShowMap(Organisation org);

    void onShowDetails(Organisation org);

    void onCallNumber(Organisation org);
}
