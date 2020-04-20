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
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.ObjectArrays;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import co.haslo.spreadsheetsregistermapcontrolboard.auth.Config;
import co.haslo.spreadsheetsregistermapcontrolboard.util.CustomAnimationDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;

import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.showToast;

public class SpreadSheetLoad extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    /*EasyPermissions Request Value*/
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICE = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    /*Sheet API Value*/
    private static final String SHEET_ID = Config.sheet_id_private;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};

    /*Layout Element*/
    private TextView mDataBox;
    private Button mButtonUpload;
    private Button mButtonDownload;
    CustomAnimationDialog mProgress;
    GoogleAccountCredential mCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_spread_sheet);
        setActionBarControl();

        mDataBox = findViewById(R.id.data_box);
        mButtonUpload = findViewById(R.id.upload);
        mButtonDownload = findViewById(R.id.download);
        mProgress = new CustomAnimationDialog(this);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff((new ExponentialBackOff()));
        setDatabox();
        onClickButton();

    }

    /*Action & OnClick Method*/
    private void setDatabox() {
        mDataBox.setMovementMethod((new ScrollingMovementMethod()));
    }
    private void onClickButton() {
        mButtonUpload.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dlog.d("Upload");
                showToast(getApplicationContext(),"Upload");
                mButtonUpload.setEnabled(false);
                setDataArraysFormAPI();
                mButtonUpload.setEnabled(true);
            }
        });

        mButtonDownload.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Dlog.d("Download");
                //showToast(getApplicationContext(),"Download");
                mButtonDownload.setEnabled(false);
                getResultsFormAPI();
                mButtonDownload.setEnabled(true);
            }
        });
    }

    /*Action Control*/
    private void setActionBarControl() {
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(Html.fromHtml("<small>EDIT :: REGISTER MAP</small>", Html.FROM_HTML_MODE_LEGACY));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                this.finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*SET API Permission*/
    private void setDataArraysFormAPI() {
        if(!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if(mCredential.getSelectedAccountName() == null){
            chooseAccount();
        } else if(!isDeviceOnline()){
            mDataBox.setText("Check your Network Environment State");
            Dlog.d("Check Network State");
        } else {
            new EditRequesetTask(mCredential).execute();
        }
    }

    /*GET API Permission*/
    private void getResultsFormAPI() {
        if(!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if(mCredential.getSelectedAccountName() == null){
            chooseAccount();
        } else if(!isDeviceOnline()){
            mDataBox.setText("Check your Network Environment State");
            Dlog.d("Check Network State");
        } else {
            new MaketRequesetTask(mCredential).execute();
        }
    }


    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICE);
        dialog.show();
    }


    /*EasyPermissions Granted*/
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if(EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if(accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFormAPI();
            } else {
                startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This App needs to access your Google account (Via Contacts)",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICE:
                if (resultCode != RESULT_OK) {
                    mDataBox.setText("This App Require Google Play Service. Please install"+
                                            "Google Play Service on your device and relaunch this app.");
                } else {
                    getResultsFormAPI();
                }
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null){
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if(accountName != null) {
                        Dlog.d("accountName: " + accountName);
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFormAPI();

                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFormAPI();
                }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    /*Device Status*/
    private boolean isDeviceOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        Network networkConnection = connectivityManager.getActiveNetwork();
        return (networkConnection != null);
    }

    /*get Sheet Data*/
    static ArrayList<String> dataArrayList = new ArrayList<>();
    private static final String SHEET_GET_NAME = "LoadRegisterMap";
    private static final String SHEET_GET_RANGE ="A1:B10";
    private static final String SHEET_GET_VALUE = SHEET_GET_NAME +"!"+SHEET_GET_RANGE;
    private class MaketRequesetTask extends AsyncTask<Void, Void, List<String>> {
        Sheets mSheetsService = null;
        Exception mLastError = null;

        MaketRequesetTask(GoogleAccountCredential credential) {
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
                results.add("GET DATA");
                for(List row : values) {
                    dataArrayList.add(row.get(0).toString());
                    dataArrayList.add(row.get(1).toString());
                    results.add(row.get(0).toString() + "," + row.get(1).toString());
                }
            }
            return results;
        }

        @Override
        protected void onPreExecute() {
            mDataBox.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if(output == null || output.size() == 0) {
                mDataBox.setText("No Result, Check your");
            } else {
                output.add(0, "Data Retrieved Using the Google SpreadSheet API : ");
                mDataBox.setText(TextUtils.join("\n", output));
            }
        }
        @Override
        protected void onCancelled() {
            mProgress.hide();
            if(mLastError != null) {
                Dlog.e("API ERROR: "+ mLastError);
                if(mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
                } else if(mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(((UserRecoverableAuthIOException)mLastError).getIntent(), SpreadSheetLoad.REQUEST_AUTHORIZATION);
                } else {
                    mDataBox.setText("The following error occured:\n"+ mLastError.getMessage());
                }
            }
        }
    }


    /*set Sheet Data*/
    private static final String SHEET_SET_NAME = "LoadRegisterMap";
    private static final String SHEET_SET_RANGE ="C1:D10";
    private static final String SHEET_SET_VALUE = SHEET_SET_NAME +"!"+SHEET_SET_RANGE;
    private class EditRequesetTask extends AsyncTask<Void, Void, List<String>> {
        Sheets mSheetsService = null;
        Exception mLastError = null;

        EditRequesetTask(GoogleAccountCredential credential) {
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
            ValueRange response = mSheetsService
                    .spreadsheets()
                    .values()
                    .get(SHEET_ID, SHEET_GET_VALUE)
                    .execute();


            List<List<Object>> values = response.getValues();

            ObjectMapper mapper = new ObjectMapper();
            String contentsString = "[[1, 2], [10, 23], [2, 2]]";
            List<List<Object>> convertWebSiteModels = mapper.readValue(contentsString, new TypeReference<List<List<Object>>>(){});


            Dlog.d("convertWebSiteModels Value : "+ convertWebSiteModels);

            ValueRange body = new ValueRange().setValues(convertWebSiteModels);
            UpdateValuesResponse result = mSheetsService
                    .spreadsheets()
                    .values()
                    .update(SHEET_ID, SHEET_SET_VALUE, body)
                    .setValueInputOption("RAW")
                    .execute();
            Dlog.d(result.getUpdatedCells()+ " cells updated");
            //
            if(values != null) {
                results.add("UPDATE DATA");
                for(List row : values) {
                    results.add(row.get(0).toString() + "," + row.get(1).toString());
                }
            }
            return results;
        }

        @Override
        protected void onPreExecute() {
            mDataBox.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if(output == null || output.size() == 0) {
                mDataBox.setText("No Result, Check your");
            } else {
                output.add(0, "Data Retrieved Using the Google SpreadSheet API : ");
                mDataBox.setText(TextUtils.join("\n", output));
            }
        }
        @Override
        protected void onCancelled() {
            mProgress.hide();
            if(mLastError != null) {
                Dlog.e("API ERROR: "+ mLastError);
                if(mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) mLastError).getConnectionStatusCode());
                } else if(mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(((UserRecoverableAuthIOException)mLastError).getIntent(), SpreadSheetLoad.REQUEST_AUTHORIZATION);
                } else {
                    mDataBox.setText("The following error occured:\n"+ mLastError.getMessage());
                }
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mProgress.dismiss();
    }



}
