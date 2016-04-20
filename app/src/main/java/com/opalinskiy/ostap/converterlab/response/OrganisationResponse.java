package com.opalinskiy.ostap.converterlab.response;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opalinskiy.ostap.converterlab.interfaces.ModelResponse;
import com.opalinskiy.ostap.converterlab.models.organisation.Organisation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class OrganisationResponse implements ModelResponse {
    private ArrayList<Organisation> orgs;
    private HashMap<String, String> regions;
    private HashMap<String, String>cities;

    @Override
    public void configure(Object object) throws JSONException, ParseException {
        JSONObject generalObject = (JSONObject) object;
        JSONArray orgsArray = generalObject.getJSONArray("organizations");
        Gson gson = new Gson();
        orgs = gson.fromJson(orgsArray.toString(), new TypeToken<ArrayList<Organisation>>() {
        }.getType());

        JSONObject regionsObject = generalObject.getJSONObject("regions");
        regions = gson.fromJson(regionsObject.toString(), new TypeToken<HashMap<String, String>>() {
        }.getType());

        JSONObject citiesObject = generalObject.getJSONObject("cities");
        cities = gson.fromJson(citiesObject.toString(), new TypeToken<HashMap<String, String>>() {
        }.getType());

        for(int i = 0; i < orgs.size(); i++){
            orgs.get(i).setRegion(regions.get( orgs.get(i).getRegionId()));
            orgs.get(i).setCity(cities.get(orgs.get(i).getCityId()));
        }
    }

    public ArrayList<Organisation> getOrgs() {
        return orgs;
    }

}
