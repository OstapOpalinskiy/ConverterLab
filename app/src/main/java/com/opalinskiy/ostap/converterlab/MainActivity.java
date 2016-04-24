package com.opalinskiy.ostap.converterlab;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.opalinskiy.ostap.converterlab.adapters.OrganisationsAdapter;
import com.opalinskiy.ostap.converterlab.constants.Constants;
import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.interfaces.EventHandler;
import com.opalinskiy.ostap.converterlab.model.Organisation;
import com.opalinskiy.ostap.converterlab.model.DataResponse;
import com.opalinskiy.ostap.converterlab.databaseUtils.DbManager;
import com.opalinskiy.ostap.converterlab.utils.DateParser;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EventHandler, SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener {

    private List<Organisation> organisations;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DbManager dbManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_MA);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        dbManager = new DbManager(this);
        dbManager.open();

        final Snackbar snackbar = Snackbar
                .make(swipeRefreshLayout, "", Snackbar.LENGTH_INDEFINITE);
        Calendar calendar = null;
        Calendar calendarYounger = null;

        try {
           calendar = DateParser.toCalendar("1971-04-24T12:01:37+03:00");
           calendarYounger = DateParser.toCalendar("2016-04-24T12:01:36+03:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "time" +  (calendar.getTimeInMillis() < calendarYounger.getTimeInMillis()));
        Api.getDataResponse(new ConnectCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d(Constants.LOG_TAG, "On success");
                DataResponse dataResponse = (DataResponse) object;
                organisations = dataResponse.getOrganisations();
                dbManager.setRatesVariationForList(organisations);
                dbManager.writeAllDataToDb(dataResponse);
               // dbManager.smartWriteIntoDbList(organisations);
                showList(organisations);
                snackbar.dismiss();
            }

            @Override
            public void onFailure() {
                Log.d(Constants.LOG_TAG, "onFailure=");
                organisations = dbManager.readListOfOrganisationsFromDB();
                dbManager.setRatesForList(organisations);
                Log.d(Constants.LOG_TAG, "ask from first of DB: " + organisations.get(0));
                showList(organisations);
            }

            @Override
            public void onProgress(long percentage) {
                String message = "Progress:" + percentage + "%";
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

    @Override
    public void onOpenLink(Organisation org) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(org.getLink()));
        startActivity(browserIntent);
    }

    @Override
    public void onShowMap(Organisation org) {
        Intent intent = new Intent(this, MapActivity.class);
        // String address = org.getCity() + ", " + org.getAddress();
        //Log.d("TAG", address);
        //intent.putExtra("address", address);
        intent.putExtra("city", org.getCity());
        intent.putExtra("address", org.getAddress());
        startActivity(intent);
    }

    @Override
    public void onShowDetails(Organisation org) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.ORG_SERIALISE, (Serializable) org);
        startActivity(intent);
    }

    @Override
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

