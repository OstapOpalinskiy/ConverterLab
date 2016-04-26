package com.opalinskiy.ostap.converterlab.abstractActivities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import com.opalinskiy.ostap.converterlab.DetailActivity;
import com.opalinskiy.ostap.converterlab.MapActivity;
import com.opalinskiy.ostap.converterlab.constants.Constants;
import com.opalinskiy.ostap.converterlab.model.Organisation;

import java.io.Serializable;

/**
 * Created by Evronot on 26.04.2016.
 */
public class AbstractActionActivity extends AppCompatActivity {

    public void onOpenLink(Organisation org) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(org.getLink()));
        startActivity(browserIntent);
    }


    public void onShowMap(Organisation org) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("city", org.getCity());
        intent.putExtra("address", org.getAddress());
        startActivity(intent);
    }


    public void onShowDetails(Organisation org) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.ORG_SERIALISE, (Serializable) org);
        startActivity(intent);
    }


    public void onCallNumber(Organisation org) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + org.getPhone()));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // shows alert dialog, if there is no phone app on device
            // showAlertDialog(R.string.application_not_found, R.string.no_phone, R.string.buttonText_ok);
        }
    }

}
