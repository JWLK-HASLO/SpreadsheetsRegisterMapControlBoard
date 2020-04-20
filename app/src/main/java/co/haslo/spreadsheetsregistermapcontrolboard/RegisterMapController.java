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

}
