package co.haslo.spreadsheetsregistermapcontrolboard;

import android.Manifest;
import android.content.Context;
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

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private static final String SHEET_ID = Config.spreadsheet_id;
    private static final String API_KEY = Config.google_api_key;
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS_READONLY};

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
        getResultsFormAPI();

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

    /*GET API Permission*/
    private void getResultsFormAPI() {
        if(!isDeviceOnline()){
            mDataBox.setText("Check your Network Environment State");
            Dlog.d("Check Network State");
        } else {
            new MaketRequesetTask().execute();
        }
    }

    /*EasyPermissions Granted*/
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if(EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString("JEWELS", null);
            if(accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFormAPI();
            } else {
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
    private static final String SHEET_NAME = "LoadRegisterMap";
    private static final String SHEET_RANGE ="A1:B32";
    private static final String SHEET_VALUE = SHEET_NAME +"!"+SHEET_RANGE;
    private class MaketRequesetTask extends AsyncTask<Void, Void, List<String>> {
        Sheets mSheetsService = null;
        Exception mLastError = null;

        MaketRequesetTask() {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mSheetsService =  new Sheets.Builder(transport, jsonFactory, null)
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
                    .get(SHEET_ID, SHEET_VALUE)
                    .setKey(API_KEY)
                    .execute();

            List<List<Object>> values = response.getValues();
            if(values != null) {
                results.add("NAME, ADDRESS");
                for(List row : values) {
                    results.add(row.get(0) + ", " + row.get(1));
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
            }
        }
    }




}
