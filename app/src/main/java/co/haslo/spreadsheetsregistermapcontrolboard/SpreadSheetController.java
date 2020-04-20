package co.haslo.spreadsheetsregistermapcontrolboard;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.haslo.spreadsheetsregistermapcontrolboard.auth.Config;
import co.haslo.spreadsheetsregistermapcontrolboard.util.CustomAnimationDialog;
import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.isDeviceOnline;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.networkError;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.showToast;

public class SpreadSheetController {

    /*Sheet API Value*/
    private static final String SHEET_ID = Config.sheet_id_private;

    /*Layout Element*/
    private AppCompatActivity appCompatActivity;
    private CustomAnimationDialog mProgress;
    private GoogleAccountCredential mCredential;
    public static List<String> dataList;
    private static String mGetRange = null;
    private static String mSetRange = null;
    private static String mString = null;
    /*get Value*/
    SpreadSheetController(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void initialize(){
        mProgress = new CustomAnimationDialog(appCompatActivity);
        mCredential = InitSplash.globalCredential;
    }

    public void getSheetData(String mGetRange) {
        SpreadSheetController.mGetRange = mGetRange;
    }

    public void setSheetData(String mSetRange, String mString){
        SpreadSheetController.mSetRange = mSetRange;
        SpreadSheetController.mString = mString;
    }

    /*GET Sheet Data*/
    public void getResultsFormAPI() {
        if(!isDeviceOnline(appCompatActivity)){
            networkError(appCompatActivity);
        } else {
            new getRequesetTask(mCredential).execute();
        }
    }


    /*SET Sheet Data*/
    public void setDataArraysFormAPI() {
        if(!isDeviceOnline(appCompatActivity)){
            networkError(appCompatActivity);
        } else {
            new setRequesetTask(mCredential).execute();
        }
    }


    /*get Sheet Data*/
    private class getRequesetTask extends AsyncTask<Void, Void, List<String>> {
        Sheets mSheetsService = null;
        Exception mLastError = null;
        String SHEET_GET_NAME = "LoadRegisterMap";
        String SHEET_GET_RANGE = mGetRange;
        String SHEET_GET_VALUE = SHEET_GET_NAME +"!"+SHEET_GET_RANGE;

        getRequesetTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mSheetsService =  new Sheets.Builder(transport, jsonFactory, credential)
                    .setApplicationName("SYSTEM_REGISTER_MAP")
                    .build();
        }
        @Override
        protected List<String> doInBackground(Void... voids) {
            try {
                return getDataFormAPI();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> getDataFormAPI() throws IOException {
            List<String> results = new ArrayList<String>();
            ValueRange response = mSheetsService.spreadsheets().values()
                    .get(SHEET_ID, SHEET_GET_VALUE)
                    .execute();

            List<List<Object>> values = response.getValues();
            if(values != null) {
                for(List row : values) {
                    for(int column = 0; column < row.size()/2; column+=2){
                        results.add(row.get(column).toString()+row.get(column+1).toString());
                    }
                }
            }
            Dlog.d("OnlyData = " + results);
            return results;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if(output == null || output.size() == 0) {
                showToast(appCompatActivity,"No Result, Check your");
            } else {
                dataList = output;
            }
        }
        @Override
        protected void onCancelled() {
            mProgress.hide();
            Dlog.e("API ERROR: "+ mLastError);
        }
    }


    /*set Sheet Data*/
    private class setRequesetTask extends AsyncTask<Void, Void, List<String>> {
        Sheets mSheetsService = null;
        Exception mLastError = null;
        String SHEET_SET_NAME = "LoadRegisterMap";
        String SHEET_SET_RANGE = mSetRange;
        String SHEET_SET_VALUE = SHEET_SET_NAME +"!"+SHEET_SET_RANGE;

        setRequesetTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mSheetsService =  new Sheets.Builder(transport, jsonFactory, credential)
                    .setApplicationName("SYSTEM_REGISTER_MAP")
                    .build();
        }
        @Override
        protected List<String> doInBackground(Void... voids) {
            try {
                return setDataFormAPI();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<String> setDataFormAPI() throws IOException {
            List<String> results = new ArrayList<String>();

            ObjectMapper mapper = new ObjectMapper();
            List<List<Object>> convertData= mapper.readValue(mString, new TypeReference<List<List<Object>>>(){});


            Dlog.d("convertData Value : "+ convertData);

            ValueRange body = new ValueRange().setValues(convertData);
            UpdateValuesResponse result = mSheetsService
                    .spreadsheets()
                    .values()
                    .update(SHEET_ID, SHEET_SET_VALUE, body)
                    .setValueInputOption("RAW")
                    .execute();
            Dlog.d(result.getUpdatedCells()+ " cells updated");

            if(convertData != null) {
                for(List row : convertData) {
                    for(int column = 0; column < row.size(); column++){
                        results.add(row.get(column).toString());
                    }
                }
            }
            return results;
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if(output == null || output.size() == 0) {
                showToast(appCompatActivity,"No Result, Check your");
            } else {
                //output.add(0, "Upload Data Retrieved Using the Google SpreadSheet API : ");
                //TextUtils.join("\n", output);
                dataList = output;
            }
        }
        @Override
        protected void onCancelled() {
            mProgress.hide();
            Dlog.e("API ERROR: "+ mLastError);
        }
    }





}
