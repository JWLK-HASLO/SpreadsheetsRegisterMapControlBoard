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

import static co.haslo.spreadsheetsregistermapcontrolboard.SpreadSheetController.dataList;
import static co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager.DeviceDataTransfer.hexaArray;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertSheetType.convertDownloadString;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertSheetType.convertUploadString;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.showToast;

public class FullscreenClickAction {

    private AppCompatActivity appCompatActivity;
    DeviceHandler mDeviceHandler ;
    SpreadSheetController mSpreadSheetController;

    private boolean setTrigger = false;
    private boolean startTrigger = false;

    FullscreenClickAction(AppCompatActivity appCompatActivity, DeviceHandler deviceHandler) {
        this.appCompatActivity = appCompatActivity;
        mDeviceHandler = deviceHandler;
    }

    void initialize() {
        Dlog.d("Ready");
        setButton();
        mSpreadSheetController = new SpreadSheetController(appCompatActivity);
        mSpreadSheetController.initialize();

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
        loadMenuButtonUpload();
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
                showToast(appCompatActivity,"Clear Log Box");
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
                showToast(appCompatActivity,"Going to Top!");
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
                showToast(appCompatActivity,"Going to Bottom!");
                InterfaceUtil.scrollBottom(logBoxText);
            }
        });
    }

    private void loadMenuButtonResetCounter() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_resetCounter);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"Counter Reset");
                mDeviceHandler.resetCounter();
            }
        });
    }

    private void loadMenuButtonStartCounter() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_startCounter);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"Counter Start");
                mDeviceHandler.startCounter();
            }
        });
    }

    private void loadMenuButtonReset() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_reset);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"RESET Button Click");
                mDeviceHandler.resetCounter();
                mDeviceHandler.resetData();
            }
        });
    }

    private void loadMenuButtonSet() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_set);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"SET Button Click");
                mSpreadSheetController.getSheetData("A1:B4095");
                mSpreadSheetController.getResultsFormAPI();
                setTrigger = true;
            }
        });
    }


    private void loadMenuButtonStart() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_start);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setTrigger){
                    showToast(appCompatActivity,"START Button Click");
                    setTrigger = false;
                    startTrigger = true;
                    mDeviceHandler.sendData(convertDownloadString(dataList));
                } else {
                    showToast(appCompatActivity,"Please SET Data");
                }

            }
        });
    }

    private void loadMenuButtonUpload() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_upload);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startTrigger && hexaArray.length != 0){
                    showToast(appCompatActivity,"Upload Button Click");
                    startTrigger = false;
                    mSpreadSheetController.setSheetData("C1:C8200", convertUploadString(hexaArray));
                    mSpreadSheetController.setDataArraysFormAPI();
                } else {
                    showToast(appCompatActivity,"Please SET Data");
                }

            }
        });
    }


}
