package com.opalinskiy.ostap.converterlab.abstractActivities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.opalinskiy.ostap.converterlab.DetailActivity;
import com.opalinskiy.ostap.converterlab.MapActivity;
import com.opalinskiy.ostap.converterlab.R;
import com.opalinskiy.ostap.converterlab.constants.Constants;
import com.opalinskiy.ostap.converterlab.model.DataResponse;
import com.opalinskiy.ostap.converterlab.model.Organisation;
import com.opalinskiy.ostap.converterlab.services.LoaderService;

import java.io.Serializable;

/**
 * Created by Evronot on 26.04.2016.
 */
public class AbstractActionActivity extends AppCompatActivity {
    protected NotificationCompat.Builder builder;
    protected NotificationManager notificationManager;
    protected Snackbar snackbar;
    private boolean isCalledAllowed = false;
    private Organisation organisation;
    private SharedPreferences sPref;

    protected void startLoaderService() {
        Intent intent = new Intent(this, LoaderService.class);
        startService(intent);
    }

    protected void prepareNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this)
                .setContentText("")
                .setContentTitle("Loading...")
                .setSmallIcon(R.drawable.ic_link)
                .setOngoing(true);
    }

    protected void updateNotification(String text) {
        builder.setContentText(text);
        notificationManager.notify(0, builder.build());
    }

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
        organisation = org;
        sPref = getPreferences(MODE_PRIVATE);
        isCalledAllowed = sPref.getBoolean(Constants.CALL_ALLOWED, false);
        if (isCalledAllowed) {
            performCall(org);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }

    private void performCall(Organisation org) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + org.getPhone()));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d(Constants.LOG_TAG, "no phone app in device");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // checks whether user gave a permission
        if (!(grantResults.length > 0
                && !(grantResults[0] == PackageManager.PERMISSION_GRANTED))) {
            isCalledAllowed = true;
            SharedPreferences.Editor ed = sPref.edit();
            ed.putBoolean(Constants.CALL_ALLOWED, isCalledAllowed);
            ed.commit();
            performCall(organisation);
        }
    }

}
