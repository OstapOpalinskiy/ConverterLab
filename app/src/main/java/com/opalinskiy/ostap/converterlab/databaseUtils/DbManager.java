package com.opalinskiy.ostap.converterlab.databaseUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.opalinskiy.ostap.converterlab.constants.Constants;
import com.opalinskiy.ostap.converterlab.constants.dbConstants;
import com.opalinskiy.ostap.converterlab.model.Currency;
import com.opalinskiy.ostap.converterlab.model.DataResponse;
import com.opalinskiy.ostap.converterlab.model.Organisation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Evronot on 22.04.2016.
 */
public class DbManager {
    private DbHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;

    public DbManager(Context context) {
        this.context = context;
    }

    public void open() {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null) {
            database.close();
        }
    }

    public void writeListOfOrganisationsToDb(List<Organisation> list) {
        database.delete(dbConstants.TABLE_ORGANIZATIONS, null, null);
        for (int i = 0; i < list.size(); i++) {
            writeOrganisationToDb(list.get(i));
        }
    }

    public void writeOrganisationToDb(Organisation org) {
        ContentValues cv = new ContentValues();
        cv.put(dbConstants.COLUMN_ID, org.getId());
        cv.put(dbConstants.COLUMN_OLD_ID, org.getOldId());
        cv.put(dbConstants.COLUMN_ORG_TYPE, org.getOrgType());
        cv.put(dbConstants.COLUMN_TITLE, org.getTitle());
        cv.put(dbConstants.COLUMN_REGION_ID, org.getRegionId());
        cv.put(dbConstants.COLUMN_CITY_ID, org.getCityId());
        cv.put(dbConstants.COLUMN_PHONE, org.getPhone());
        cv.put(dbConstants.COLUMN_ADDRESS, org.getAddress());
        cv.put(dbConstants.COLUMN_LINK, org.getLink());
        cv.put(dbConstants.COLUMN_DATE, org.getDate());
        database.insert(dbConstants.TABLE_ORGANIZATIONS, null, cv);
    }

    public void writeDataToDb(DataResponse response) {
        writeListOfOrganisationsToDb(response.getOrganisations());
        writeMapToDb(response.getCurrencies(), dbConstants.TABLE_CURRENCIES);
        writeMapToDb(response.getCities(), dbConstants.TABLE_CITIES);
        writeMapToDb(response.getRegions(), dbConstants.TABLE_REGIONS);
        writeMapToDb(response.getOrgTypes(), dbConstants.TABLE_ORG_TYPES);
        writeAllCoursesToDb(response.getOrganisations());
    }

    public void writeMapToDb(Map<String, String> map, String tableName) {
        ContentValues cv = new ContentValues();
        database.delete(tableName, null, null);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            cv.put(dbConstants.PRIMARY_KEY_ID, entry.getKey());
            cv.put(dbConstants.COLUMN_VALUE, entry.getValue());
            database.insert(tableName, null, cv);
        }
    }

    public Cursor readOrganisationsFromDb() {
        String query = "SELECT " +
                "organizations.id, " +
                "organizations.oldId, " +
                "organizations.orgType, " +
                "orgTypes.value, " +
                "organizations.title, " +
                "organizations.regionId, " +
                "regions.value, " +
                "organizations.cityId, " +
                "cities.value, " +
                "organizations.phone, " +
                "organizations.address, " +
                "organizations.link, " +
                "organizations.date " +
                "FROM   organizations, regions, cities, orgTypes " +
                "WHERE  regions._id=organizations.regionId AND " +
                "cities._id=organizations.cityId AND " +
                "orgTypes._id=organizations.orgType";
        return database.rawQuery(query, null);
    }

    public List<Organisation> readListOfOrganisationsFromDB() {
        Cursor cursor = null;
        List<Organisation> list = new ArrayList();
        try {
            cursor = readOrganisationsFromDb();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Organisation organisation = new Organisation();
                    organisation.setId(cursor.getString(0));
                    organisation.setOldId(Integer.parseInt(cursor.getString(1)));
                    organisation.setOrgType(Integer.parseInt(cursor.getString(2)));
                    organisation.setTitle(cursor.getString(4));
                    organisation.setRegionId(cursor.getString(5));
                    organisation.setRegion(cursor.getString(6));
                    organisation.setCityId(cursor.getString(7));
                    organisation.setCity(cursor.getString(8));
                    organisation.setPhone(cursor.getString(9));
                    organisation.setAddress(cursor.getString(10));
                    organisation.setLink(cursor.getString(11));
                    organisation.setDate(cursor.getString(12));
                    list.add(organisation);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "exception caught in onLogin()", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public void writeAllCoursesToDb(List<Organisation> list) {
        database.delete(dbConstants.TABLE_COURSES, null, null);
        for (int i = 0; i < list.size(); i++) {
            writeCourseToDb(list.get(i));
        }
    }

    public void writeCourseToDb(Organisation organisation) {
        List<Currency> listCurrencies = organisation.getCurrencies().getCurrencyList();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < listCurrencies.size(); i++) {
            cv.put(dbConstants.COLUMN_ID_ORGANIZATIONS, organisation.getId());
            cv.put(dbConstants.COLUMN_ID_CURRENCY, listCurrencies.get(i).getIdCurrency());
            cv.put(dbConstants.COLUMN_NAME_CURRENCY, listCurrencies.get(i).getNameCurrency());
            cv.put(dbConstants.COLUMN_ASK_CURRENCY, listCurrencies.get(i).getAsk());
            cv.put(dbConstants.COLUMN_CHANGE_ASK, listCurrencies.get(i).getChangeAsk());
            cv.put(dbConstants.COLUMN_BID_CURRENCY, listCurrencies.get(i).getBid());
            cv.put(dbConstants.COLUMN_CHANGE_BID, listCurrencies.get(i).getChangeBid());
            database.insert(dbConstants.TABLE_COURSES, null, cv);
        }

    }

    public void fillOrganisationWithCourses(Organisation organisation) {
        List<Currency> list = new ArrayList();
        Cursor cursor = null;
        try {
            cursor = getCourses();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (cursor.getString(1).equals(organisation.getId())) {
                        Currency currency = new Currency();
                        currency.setIdCurrency(cursor.getString(2));
                        currency.setNameCurrency(cursor.getString(3));
                        currency.setAsk(cursor.getString(4));
                        currency.setChangeAsk(cursor.getString(5));
                        currency.setBid(cursor.getString(6));
                        currency.setChangeBid(cursor.getString(7));
                        list.add(currency);
                    }
                } while (cursor.moveToNext());
                organisation.getCurrencies().setCurrencyList(list);
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "exception caught in onLogin()", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Cursor getCourses() {
        return database.rawQuery("SELECT * FROM " + dbConstants.TABLE_COURSES, null);
    }


    public void recreateDb() {
        dbHelper.onUpgrade(database, 1, 1);
    }

    public void setCurrencyVariationForList(List<Organisation> list) {
        for (int i = 0; i < list.size(); i++) {
            List<Currency> currencyList = list.get(i).getCurrencies().getCurrencyList();
            for (int j = 0; j < currencyList.size(); j++) {
                setCurrencyVariation(list.get(i), currencyList.get(j));
            }
        }
    }


    public void setCurrencyVariation(Organisation organisation, Currency currency) {
        Log.d(Constants.LOG_TAG, "org id in setCurrencyVariation: " + organisation.getId());
        Log.d(Constants.LOG_TAG, "currency Name in setCurrencyVariation: " + currency.getNameCurrency());
        Currency currencyFromDB = findCurrencyInDb(organisation.getId(), currency.getIdCurrency());
        Log.d(Constants.LOG_TAG, "currency from db is null" + (currencyFromDB == null));
        compareAskBid(currency, currencyFromDB);
    }

    private void compareAskBid(Currency currency, Currency currencyFromDB) {
        double ask = 0;
        double bid = 0;
        double askDb = 0;
        double bidDb = 0;

        if (currencyFromDB != null) {
            if (!TextUtils.isEmpty(currency.getAsk())) {
                ask = Double.parseDouble(currency.getAsk());
            }

            if (!TextUtils.isEmpty(currency.getBid())) {
                bid = Double.parseDouble(currency.getBid());
            }

            if (!TextUtils.isEmpty(currencyFromDB.getAsk())) {
                askDb = Double.parseDouble(currency.getBid());
            }

            if (!TextUtils.isEmpty(currencyFromDB.getBid())) {
                bidDb = Double.parseDouble(currency.getBid());
            }

            if (ask >= askDb) {
                currency.setChangeAsk(Constants.INCREASE_KEY);
            } else {
                currency.setChangeAsk(Constants.DECREASE_KEY);
            }
            if (bid >= bidDb) {
                currency.setChangeBid(Constants.INCREASE_KEY);
            } else {
                currency.setChangeBid(Constants.DECREASE_KEY);
            }
        }
    }

    public Currency findCurrencyInDb(String organisationId, String currencyId) {
        Cursor cursor = null;
        Currency currency = null;
        try {
            cursor = getCourses();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (cursor.getString(1).equals(organisationId)
                            && currencyId.equals(cursor.getString(2))) {
                        currency = new Currency();
                        currency.setAsk(cursor.getString(4));
                        currency.setBid(cursor.getString(6));
                        return currency;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "exception caught in onLogin()", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}