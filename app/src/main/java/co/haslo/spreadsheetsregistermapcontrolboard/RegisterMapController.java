package co.haslo.spreadsheetsregistermapcontrolboard;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;

public class RegisterMapController {

    private AppCompatActivity appCompatActivity;

    RegisterMapController(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    void initialize() {
        Dlog.d("Ready");
        setActionBarControl();
    }


    private void setActionBarControl() {
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(Html.fromHtml("<small>REGISTER MAP</small>", Html.FROM_HTML_MODE_LEGACY));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                appCompatActivity.finish();
                return true;
            }
            case R.id.button_reload:{
                Toast.makeText(appCompatActivity,"Reload", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.button_save:{
                Toast.makeText(appCompatActivity,"Save", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.button_setting:{
                Toast.makeText(appCompatActivity,"Setting", Toast.LENGTH_SHORT).show();
                return true;
            }


        }
        return appCompatActivity.onOptionsItemSelected(item);
    }

    /* Button Action: Back Press(home) */
    public boolean onCreateOptionsMenu(Menu menu) {
        appCompatActivity.getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return true;
    }


}
