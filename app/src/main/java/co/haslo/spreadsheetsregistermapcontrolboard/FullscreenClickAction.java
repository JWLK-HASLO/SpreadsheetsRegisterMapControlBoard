package co.haslo.spreadsheetsregistermapcontrolboard;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager.DeviceDataTransfer;
import co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager.DeviceHandler;
import co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertDataType;
import co.haslo.spreadsheetsregistermapcontrolboard.util.CustomAnimationDialog;
import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;
import co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil;

import static co.haslo.spreadsheetsregistermapcontrolboard.SpreadSheetController.dataList;
import static co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager.DeviceDataTransfer.BULK_COUNT;
import static co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager.DeviceDataTransfer.SEQUENCE_DATA_SIZE;
import static co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager.DeviceDataTransfer.bufferArray;
import static co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager.DeviceDataTransfer.hexaArray;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertSheetType.convertDownloadString;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertSheetType.convertUploadString;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.showToast;

public class FullscreenClickAction {

    private AppCompatActivity appCompatActivity;
    DeviceHandler mDeviceHandler ;
    SpreadSheetController mSpreadSheetController;
    String convertString;

    private boolean setTrigger = false;
    private boolean startTrigger = false;
    private boolean uploadTrigger = false;

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
        loadMenuButtonReset();
        loadMenuButtonSet();
        loadMenuButtonLoad();
        loadMenuButtonStart();
        loadMenuButtonConvert();
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

    private void loadMenuButtonReset() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_reset);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"Reset");
                DeviceDataTransfer.defaultBulkCounter = -1;
                hexaArray = new String[SEQUENCE_DATA_SIZE*BULK_COUNT];
                mDeviceHandler.resetData();
                uploadTrigger = true;
            }
        });
    }

    private void loadMenuButtonLoad() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_load);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"LOAD Button Click");
                mSpreadSheetController.getSheetData("C1:D4095");
                mSpreadSheetController.getResultsFormAPI();
                setTrigger = true;
            }
        });
    }

    private void loadMenuButtonSet() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_set);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(setTrigger) {
                    showToast(appCompatActivity,"SET Button Click");
                    mDeviceHandler.sendData(convertDownloadString(dataList));
                    DeviceDataTransfer.defaultBulkCounter = -1;
                    hexaArray = new String[SEQUENCE_DATA_SIZE*BULK_COUNT];
                    setTrigger = false;
                    startTrigger = true;
                } else {
                    showToast(appCompatActivity,"Please LOAD Data");
                }
            }
        });
    }


    private void loadMenuButtonStart() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_start);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(startTrigger){
                    showToast(appCompatActivity,"START Button Click");
                    DeviceDataTransfer.defaultBulkCounter = -1;
                    hexaArray = new String[SEQUENCE_DATA_SIZE*BULK_COUNT];
                    mDeviceHandler.run();
                    startTrigger = false;
                    uploadTrigger = true;
//                } else {
//                    showToast(appCompatActivity,"Please SET Data");
//                }

            }
        });
    }

    private void loadMenuButtonConvert() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_convert);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast(appCompatActivity,"Convert Button Click");
                Dlog.d("Convert Complete");
                for(int i = 0, counter = 0; i < 147456; i+=4, counter++) {
                    byte Data03 = bufferArray[i + 3];
                    byte Data02 = bufferArray[i + 2];
                    byte Data01 = bufferArray[i + 1];
                    byte Data00 = bufferArray[i + 0];
                    byte[] DataArray = {Data03,Data02,Data01,Data00};
                    convertString = ConvertDataType.byteArrayToHexString(DataArray);
                    hexaArray[counter] = convertString;
                }

            }
        });
    }

    private void loadMenuButtonUpload() {
        Button mButton = (Button) appCompatActivity.findViewById(R.id.side_button_upload);
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(uploadTrigger && hexaArray.length != 0){
                    showToast(appCompatActivity,"UPLOAD Button Click");
                    uploadTrigger = false;
//                    mSpreadSheetController.setSheetData("B1:B32800", convertUploadString(hexaArray));
                    mSpreadSheetController.setSheetData("B1:B"+hexaArray.length, convertUploadString(hexaArray));
                    mSpreadSheetController.setDataArraysFormAPI();
//                } else {
//                    showToast(appCompatActivity,"Please SET Data");
//                }

            }
        });
    }


}
