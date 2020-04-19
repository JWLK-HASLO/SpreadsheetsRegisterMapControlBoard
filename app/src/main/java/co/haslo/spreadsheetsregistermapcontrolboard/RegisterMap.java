package co.haslo.spreadsheetsregistermapcontrolboard;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;
import pub.devrel.easypermissions.EasyPermissions;

import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.showToast;

public class RegisterMap extends AppCompatActivity {


    RegisterMapController mRegisterMapController = new RegisterMapController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_register_map);
        Dlog.d("Start : RegisterMap onCreate");
        mRegisterMapController.initialize();
        mRegisterMapController.getResultsFormAPI();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                this.finish();
                return true;
            }
            case R.id.button_reload:{
                mRegisterMapController.getResultsFormAPI();
                //mRegisterMapController.setDataGridView();
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
