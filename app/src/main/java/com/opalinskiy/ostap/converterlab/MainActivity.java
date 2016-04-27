package com.opalinskiy.ostap.converterlab;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.opalinskiy.ostap.converterlab.abstractActivities.AbstractActionActivity;
import com.opalinskiy.ostap.converterlab.adapters.OrganisationsAdapter;
import com.opalinskiy.ostap.converterlab.api.Api;
import com.opalinskiy.ostap.converterlab.constants.Constants;
import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.model.Organisation;
import com.opalinskiy.ostap.converterlab.model.DataResponse;
import com.opalinskiy.ostap.converterlab.utils.databaseUtils.DbManager;
import com.opalinskiy.ostap.converterlab.receivers.AlarmReceiver;
import com.opalinskiy.ostap.converterlab.services.LoaderService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AbstractActionActivity implements SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener {

    private List<Organisation> organisations;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DbManager dbManager;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_MA);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        prepareNotification();
        dbManager = new DbManager(this);
        dbManager.open();

        // startAlarmReceiver();

        final Snackbar snackbar = Snackbar
                .make(swipeRefreshLayout, "", Snackbar.LENGTH_INDEFINITE);

        Api.getDataResponse(new ConnectCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d(Constants.LOG_TAG, "On success");
                DataResponse dataResponse = (DataResponse) object;
                organisations = dataResponse.getOrganisations();
                updateNotification("Loading successful");
                startLoaderService();

                // dbManager.smartWriteIntoDbList(organisations); dbManager.setRatesVariationForList(organisations);
//                dbManager.writeAllDataToDb(dataResponse);
                showList(organisations);
                snackbar.dismiss();
            }

            @Override
            public void onFailure() {
                Log.d(Constants.LOG_TAG, "onFailure=");
                organisations = dbManager.readListOfOrganisationsFromDB();
                dbManager.setRatesForList(organisations);
                Log.d(Constants.LOG_TAG, "ask from first of DB: " + organisations.get(0));
                updateNotification("Cant load data from internet");
                showList(organisations);
            }

            @Override
            public void onProgress(long percentage) {
                String message = "Progress:" + percentage + "%";
                updateNotification(message);
                snackbar.setText(message);
                snackbar.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //  Log.d("TAG", "text changed..." + query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(Constants.LOG_TAG, "onTextChange");
        String criteria = newText.toLowerCase();
        List<Organisation> filteredList = new ArrayList();
        //  if (!TextUtils.isEmpty(newText)) {
        for (int i = 0; i < organisations.size(); i++) {
            if (organisations.get(i).getCity().toLowerCase().contains(criteria)
                    || organisations.get(i).getRegion().toLowerCase().contains(criteria)
                    || organisations.get(i).getTitle().toLowerCase().contains(criteria)) {
                filteredList.add(organisations.get(i));
            }
        }
        showList(filteredList);
//        } else {
//            showList(organisations);
//        }
        return false;
    }

    private void startLoaderService() {
        Intent intent = new Intent(this, LoaderService.class);
        startService(intent);
    }

    private void refreshData() {
        final Snackbar snackbar = Snackbar
                .make(swipeRefreshLayout, "", Snackbar.LENGTH_INDEFINITE);
        Api.getDataResponse(new ConnectCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d(Constants.LOG_TAG, "on success");
                DataResponse dataResponse = (DataResponse) object;
                organisations = dataResponse.getOrganisations();
                Log.d(Constants.LOG_TAG, "on success");
                showExample();
                showList(organisations);
                snackbar.dismiss();
            }

            @Override
            public void onFailure() {
                Log.d(Constants.LOG_TAG, "onFailure=");
            }

            @Override
            public void onProgress(long percentage) {
                String message = "Progress:" + percentage + "%";
                Log.d("TAG", "Progress in activity" + percentage + "%");
                snackbar.setText(message);
                snackbar.show();

            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showList(List<Organisation> list) {
        OrganisationsAdapter adapter = new OrganisationsAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void showExample() {
        if (organisations != null) {
            Log.d(Constants.LOG_TAG, "" + organisations.get(0));
            Log.d(Constants.LOG_TAG, "==================================================================================");
        }
    }

    private void startAlarmReceiver() {
        Intent alarm = new Intent(this, AlarmReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmRunning == false) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(), Constants.THIRTY_MINUTES, pendingIntent);
        }
    }

    private void prepareNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this)
                .setContentText("")
                .setContentTitle("Loading...")
                .setSmallIcon(R.drawable.ic_link)
                .setAutoCancel(false);
    }

    private void updateNotification(String text) {
        builder.setContentText(text);
        notificationManager.notify(0, builder.build());
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}

