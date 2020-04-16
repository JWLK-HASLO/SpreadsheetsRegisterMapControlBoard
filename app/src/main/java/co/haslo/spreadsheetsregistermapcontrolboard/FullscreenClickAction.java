package co.haslo.spreadsheetsregistermapcontrolboard;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager.DeviceHandler;
import co.haslo.spreadsheetsregistermapcontrolboard.util.CustomAnimationDialog;
import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;
import co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil;

public class FullscreenClickAction {

    private AppCompatActivity appCompatActivity;
    DeviceHandler mDeviceHandler ;

    FullscreenClickAction(AppCompatActivity appCompatActivity, DeviceHandler deviceHandler) {
        this.appCompatActivity = appCompatActivity;
        mDeviceHandler = deviceHandler;
    }

    void initialize() {
        Dlog.d("Ready");
        setButton();
    }

    /* Button Bundle */
    private void setButton() {
        loadHideButtonRegisterMap();
        loadMenuButtonClear();
        loadMenuButtonTop();
        loadMenuButtonBottom();
        loadMenuButtonResetCounter();
        loadMenuButtonStartCounter();
        loadMenuButtonSet();
        loadMenuButtonReset();
        loadMenuButtonStart();
    }

    /* Button Function */
    private void loadHideButtonRegisterMap() {
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

    private void loadMenuButtonClear() {
        final TextView logBoxText =  appCompatActivity.findViewById(R.id.log_box_text);
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_clear);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(appCompatActivity,"Clear Log Box", Toast.LENGTH_SHORT).show();
                FullscreenLogBox.clearLogcat();
                logBoxText.setText(null);
                FullscreenLogBox.lengthSaver = 0;
            }
        });
    }

    private void loadMenuButtonTop() {
        final TextView logBoxText =  appCompatActivity.findViewById(R.id.log_box_text);
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_top);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(appCompatActivity,"Going to Top!", Toast.LENGTH_SHORT).show();
                InterfaceUtil.scrollTop(logBoxText);
            }
        });
    }
    private void loadMenuButtonBottom() {
        final TextView logBoxText =  appCompatActivity.findViewById(R.id.log_box_text);
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_bottom);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(appCompatActivity,"Going to Bottom!", Toast.LENGTH_SHORT).show();
                InterfaceUtil.scrollBottom(logBoxText);
            }
        });
    }

    private void loadMenuButtonResetCounter() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_resetCounter);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(appCompatActivity,"Counter Reset", Toast.LENGTH_SHORT).show();
                mDeviceHandler.resetCounter();
            }
        });
    }

    private void loadMenuButtonStartCounter() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_startCounter);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(appCompatActivity,"Counter Start", Toast.LENGTH_SHORT).show();
                mDeviceHandler.startCounter();
            }
        });
    }

    private void loadMenuButtonSet() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_set);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(appCompatActivity,"SET Button Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMenuButtonReset() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_reset);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(appCompatActivity,"RESET Button Click", Toast.LENGTH_SHORT).show();
                mDeviceHandler.resetData();
            }
        });
    }

    private void loadMenuButtonStart() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_start);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(appCompatActivity,"START Button Click", Toast.LENGTH_SHORT).show();
            }
        });
    }




}
