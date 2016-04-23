package com.opalinskiy.ostap.converterlab.utils.dbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
//            Log.d(Constants.LOG_TAG, "id: " + entry.getKey());
//            Log.d(Constants.LOG_TAG, "value: " + entry.getValue());
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
                "orgTypes.newValue, " +
                "organizations.title, " +
                "organizations.regionId, " +
                "regions.newValue, " +
                "organizations.cityId, " +
                "cities.newValue, " +
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
            Log.d(Constants.LOG_TAG, "write curencies for " + organisation.getTitle());
            cv.put(dbConstants.COLUMN_ID_ORGANIZATIONS, organisation.getId());
            cv.put(dbConstants.COLUMN_ID_CURRENCY, listCurrencies.get(i).getIdCurrency());
            cv.put(dbConstants.COLUMN_NAME_CURRENCY, listCurrencies.get(i).getNameCurrency());
            cv.put(dbConstants.COLUMN_ASK_CURRENCY, listCurrencies.get(i).getAsk());
            cv.put(dbConstants.COLUMN_CHANGE_ASK, "1");
            cv.put(dbConstants.COLUMN_BID_CURRENCY, listCurrencies.get(i).getBid());
            cv.put(dbConstants.COLUMN_CHANGE_BID, "1");
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
                    Log.d(Constants.LOG_TAG, "id from courses" + cursor.getString(1) );
                    Log.d(Constants.LOG_TAG, "id from org" +organisation.getId());
                    Log.d(Constants.LOG_TAG, "cursor count: " + cursor.getCount());
                    Log.d(Constants.LOG_TAG, "=============================================================");

                    if (cursor.getString(1).equals(organisation.getId())) {
                        Currency currency = new Currency();
                        currency.setIdCurrency(cursor.getString(1));
                        currency.setNameCurrency(cursor.getString(2));
                        currency.setAsk(cursor.getString(3));
                        currency.setChangeAsk(cursor.getString(4));
                        currency.setBid(cursor.getString(5));
                        currency.setChangeBid(cursor.getString(6));
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
}