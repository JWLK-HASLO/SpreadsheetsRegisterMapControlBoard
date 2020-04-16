package co.haslo.spreadsheetsregistermapcontrolboard;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import co.haslo.spreadsheetsregistermapcontrolboard.util.CustomAnimationDialog;
import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;

public class FullscreenClickAction {

    private AppCompatActivity appCompatActivity;
    private CustomAnimationDialog mCustomAnimationDialog;

    FullscreenClickAction(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    void initialize() {
        Dlog.d("Ready");
        setButton();
        this.mCustomAnimationDialog = new CustomAnimationDialog(appCompatActivity);
    }

    /* Button Bundle */
    private void setButton() {
        Dlog.d("Set");
        loadHideButtonRegisterMap();
        loadMenuButtonStart();
    }

    /* Button Function */
    private void loadHideButtonRegisterMap() {
        Dlog.d("Load");
        Button mButton = (Button) appCompatActivity.findViewById(R.id.hide_button_register_map);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dlog.d("Ready");
                Intent screen = new Intent(appCompatActivity.getApplicationContext(),RegisterMap.class);
                appCompatActivity.startActivityForResult(screen,1001);
            }
        });
    }

    private void loadMenuButtonStart() {
        Dlog.d("Load");
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_start);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dlog.d("Ready");
            }
        });
    }




}
