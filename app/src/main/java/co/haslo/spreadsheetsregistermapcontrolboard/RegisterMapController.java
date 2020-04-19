package co.haslo.spreadsheetsregistermapcontrolboard;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import static android.app.Activity.RESULT_OK;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.showToast;

public class RegisterMapController {

    /*Sheet Value*/;
    private static final String SHEET_ID = Config.sheet_id_private;
    private static final String SHEET_API_KEY = Config.google_api_key;
    private static final String SHEET_NAME = "LoadRegisterMap";
    private static final String SHEET_RANGE ="A1:B32";
    private static final String SHEET_VALUE = SHEET_NAME +"!"+SHEET_RANGE;

    /*Layout Element*/
    private AppCompatActivity appCompatActivity;
    public static CustomAnimationDialog mProgress;
    private GridView mDataGrid;
    public static TextView mDataBox;

    /*Grid Value*/
    static List<String> dataGridList = null;
    static ArrayList<String>  dataArrayList = new ArrayList<>();

    RegisterMapController(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    void initialize() {
        Dlog.d("Ready");
        setActionBarControl();
        setLayoutElement();
    }


    private void setActionBarControl() {
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(Html.fromHtml("<small>REGISTER MAP</small>", Html.FROM_HTML_MODE_LEGACY));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setLayoutElement() {
        mProgress = new CustomAnimationDialog(appCompatActivity);
        mDataGrid = appCompatActivity.findViewById(R.id.data_grid);
        mDataBox = appCompatActivity.findViewById(R.id.data_box);
        mDataBox.setMovementMethod((new ScrollingMovementMethod()));
    }

    public void setDataGridView() {
        mDataGrid.setAdapter(new ArrayAdapter<String>(appCompatActivity, android.R.layout.simple_list_item_1, dataGridList){ public View getView(int position, View convertView, ViewGroup parent) {

                // Return the GridView current item as a View
                View view = super.getView(position,convertView,parent);

                // Convert the view as a TextView widget
                TextView tv = (TextView) view;

                // set the TextView text color (GridView item color)
                tv.setTextColor(Color.WHITE);

                // Set the layout parameters for TextView widget
                RelativeLayout.LayoutParams lp =  new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
                );
                tv.setLayoutParams(lp);

                // Get the TextView LayoutParams
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tv.getLayoutParams();

                // Set the width of TextView widget (item of GridView)
                //params.width = getPixelsFromDPs(appCompatActivity,168);

                // Set the TextView layout parameters
                tv.setLayoutParams(params);

                // Display TextView text in center position
                tv.setGravity(Gravity.CENTER);

                // Set the TextView text font family and text size
                tv.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

                // Set the TextView text (GridView item text)
                tv.setText(dataGridList.get(position));

                // Set the TextView background color
                tv.setBackgroundColor(Color.parseColor("#FFFF4F25"));

                // Return the TextView widget as GridView item
                return tv;
            }
        });
    }

    /*GET API Permission*/
    public void getResultsFormAPI() {
        if(!isDeviceOnline()){
            mDataBox.setText("Check your Network Environment State");
            Dlog.d("Check Network State");
        } else {
            new sheetReceiveTask().execute();
        }
    }


    /*Check Network State*/
    private boolean isDeviceOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) appCompatActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        Network networkConnection = connectivityManager.getActiveNetwork();
        return (networkConnection != null);
    }

        /*get Sheet Data*/
    private class sheetReceiveTask extends AsyncTask<Void, Void, List<String>> {
        Sheets mSheetsService = null;
        Exception mLastError = null;

        sheetReceiveTask() {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mSheetsService =  new Sheets.Builder(transport, jsonFactory, InitSplash.globalCredential)
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
            List<String> results = new ArrayList<>();
            ValueRange response = mSheetsService.spreadsheets().values()
                    .get(SHEET_ID, SHEET_VALUE)
                    //.setKey(SHEET_API_KEY)
                    .execute();

            List<List<Object>> values = response.getValues();
            if(values != null) {
                results.add("NAME, ADDRESS");
                for(List row : values) {
                    dataArrayList.add(row.get(0).toString());
                    dataArrayList.add(row.get(1).toString());
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
                dataGridList = new ArrayList<>(dataArrayList);
                output.add(0, "Data Retrieved Using the Google SpreadSheet API : ");
                mDataBox.setText(TextUtils.join("\n", output));
                showToast(appCompatActivity,"Complete Reload");
            }
        }
        @Override
        protected void onCancelled() {
            mProgress.hide();
            Dlog.e("API ERROR: "+ mLastError);

        }
    }





}
