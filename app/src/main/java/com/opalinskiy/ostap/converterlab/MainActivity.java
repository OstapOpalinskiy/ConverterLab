package com.opalinskiy.ostap.converterlab;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.opalinskiy.ostap.converterlab.adapters.OrganisationsAdapter;
import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.interfaces.EventHandler;
import com.opalinskiy.ostap.converterlab.models.organisation.Organisation;
import com.opalinskiy.ostap.converterlab.response.OrganisationResponse;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements EventHandler {

    private static final String LOG_TAG = "TAG";
    private ArrayList<Organisation> orgsList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_MA);
        Api.getOrgs(new ConnectCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d(LOG_TAG, "on sucsess");
                OrganisationResponse organisationResponse = (OrganisationResponse) object;
                orgsList = organisationResponse.getOrgs();
                showExample();
                showList();
            }

            @Override
            public void onFailure(Throwable throwable, String errorMessage) {
                Log.d(LOG_TAG, "onFailure=" + errorMessage);
            }
        });
    }

    private void showList() {
        OrganisationsAdapter adapter = new OrganisationsAdapter(this, orgsList);
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
}
