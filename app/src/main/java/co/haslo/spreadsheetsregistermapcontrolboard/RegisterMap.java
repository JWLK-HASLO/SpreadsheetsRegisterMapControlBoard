package co.haslo.spreadsheetsregistermapcontrolboard;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;
import pub.devrel.easypermissions.EasyPermissions;

import static co.haslo.spreadsheetsregistermapcontrolboard.SpreadSheetController.dataList;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertSheetType.convertDownloadString;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertSheetType.convertUploadString;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.showToast;

public class RegisterMap extends AppCompatActivity {


    RegisterMapController mRegisterMapController = new RegisterMapController(this);
    SpreadSheetController mSpreadSheetController = new SpreadSheetController(this);
    TextView mDatabox;

    static String[] stringGetArrayData;
    //String[] stringSendArrayData = {"98000000","980100EE","98000003","98000000","98010000","98000003"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_register_map);
        Dlog.d("Start : RegisterMap onCreate");
        mRegisterMapController.initialize();

        mDatabox = findViewById(R.id.data_box);
        mSpreadSheetController.initialize();
        mSpreadSheetController.getSheetData("A1:B16384");
        mSpreadSheetController.getResultsFormAPI();
        Dlog.d("data: " + dataList);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                this.finish();
                return true;
            }
            case R.id.button_download:{
                //mRegisterMapController.getResultsFormAPI();
                //mRegisterMapController.setDataGridView();
                mSpreadSheetController.getSheetData("A1:B16384");
                mSpreadSheetController.getResultsFormAPI();
                mDatabox.setText("Click RELOAD");
                return true;
            }
            case R.id.button_upload:{
                stringGetArrayData = convertDownloadString(dataList);
                mSpreadSheetController.setSheetData("C1:C16384", convertUploadString(stringGetArrayData));
                mSpreadSheetController.setDataArraysFormAPI();
                return true;
            }
            case R.id.button_reload:{
                stringGetArrayData = convertDownloadString(dataList);
                mDatabox.setText(dataList.toString());
                return true;
            }
            case R.id.button_edit:{
                showToast(this,"Edit");
                Intent screen = new Intent(getApplicationContext(), SpreadSheetLoad.class);
                startActivity(screen);
                return true;
            }
            case R.id.button_setting:{
                showToast(this,"Setting");
                return true;
            }

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return true;
    }

}
