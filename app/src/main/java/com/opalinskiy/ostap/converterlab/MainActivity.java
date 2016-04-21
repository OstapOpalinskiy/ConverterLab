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
import android.widget.Toast;

import com.opalinskiy.ostap.converterlab.adapters.OrganisationsAdapter;
import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.interfaces.EventHandler;
import com.opalinskiy.ostap.converterlab.models.organisation.Organisation;
import com.opalinskiy.ostap.converterlab.response.OrganisationResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EventHandler, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private static final String LOG_TAG = "TAG";
    private ArrayList<Organisation> orgsList;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_MA);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        final Snackbar snackbar = Snackbar
                .make(swipeRefreshLayout, "", Snackbar.LENGTH_INDEFINITE);


        Api.getOrgs(new ConnectCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d(LOG_TAG, "on sucsess");
                OrganisationResponse organisationResponse = (OrganisationResponse) object;
                orgsList = organisationResponse.getOrgs();
                showExample();
                showList(orgsList);
                snackbar.dismiss();
            }

            @Override
            public void onFailure(Throwable throwable, String errorMessage) {
                Log.d(LOG_TAG, "onFailure=" + errorMessage);
            }

            @Override
            public void onProgress(long percentage) {
                String message =  "Progress:" + percentage +"%";
                Log.d("TAG", "Progress in activity" + percentage + "%");
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
        Log.d("TAG", "onTextChange");
        String criteria = newText.toLowerCase();
        List<Organisation> filteredList = new ArrayList();
        //  if (!TextUtils.isEmpty(newText)) {
        for (int i = 0; i < orgsList.size(); i++) {
            if (orgsList.get(i).getCity().toLowerCase().contains(criteria)
                    || orgsList.get(i).getRegion().toLowerCase().contains(criteria)
                    || orgsList.get(i).getTitle().toLowerCase().contains(criteria)) {
                filteredList.add(orgsList.get(i));
            }
        }
        showList(filteredList);
//        } else {
//            showList(orgsList);
//        }
        return false;
    }

    private void refreshData() {
        final Snackbar snackbar = Snackbar
                .make(swipeRefreshLayout, "", Snackbar.LENGTH_INDEFINITE);


        Api.getOrgs(new ConnectCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d(LOG_TAG, "on sucsess");
                OrganisationResponse organisationResponse = (OrganisationResponse) object;
                orgsList = organisationResponse.getOrgs();
                showExample();
                showList(orgsList);
                snackbar.dismiss();
            }

            @Override
            public void onFailure(Throwable throwable, String errorMessage) {
                Log.d(LOG_TAG, "onFailure=" + errorMessage);
            }

            @Override
            public void onProgress(long percentage) {
                String message =  "Progress:" + percentage +"%";
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
        if (orgsList != null) {
            Log.d(LOG_TAG, "orgsList.size:" + orgsList.size());
            Log.d(LOG_TAG, "orgsList.first  address:" + orgsList.get(0).getAddress());
            Log.d(LOG_TAG, "orgsList.first: title" + orgsList.get(0).getTitle());
            Log.d(LOG_TAG, "orgsList.first: title" + orgsList.get(0).getLink());
            Log.d(LOG_TAG, "orgsList.first: region" + orgsList.get(0).getRegion());
            Log.d(LOG_TAG, "orgsList.first: Euro ask" + orgsList.get(0).getCurrencies().getEur().getBid());
            Log.d(LOG_TAG, "orgsList.first: Euro bid" + orgsList.get(0).getCurrencies().getEur().getAsk());
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
        Toast.makeText(MainActivity.this, "onShow map", Toast.LENGTH_SHORT).show();
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
}

