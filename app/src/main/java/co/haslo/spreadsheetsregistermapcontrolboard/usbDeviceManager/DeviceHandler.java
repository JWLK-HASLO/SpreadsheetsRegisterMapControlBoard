package co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager;

import android.hardware.usb.UsbDevice;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;
import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;

public class DeviceHandler extends Handler {

    private AppCompatActivity appCompatActivity;
    private DeviceManager mDeviceManager;
    private DeviceCommunicator mDeviceCommunicator;
    private DeviceDataTransfer mDeviceDataTransfer;
    //private PropertyManager mPropertyManager;

    private SimpleDateFormat formatPrint = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss");

    private Thread mUSBRealTimeController = new Thread(){
        @Override
        public void run() {
            while (!isInterrupted()) {

                Date currentTime = new Date();
                String printString = formatPrint.format(currentTime);
                //Dlog.i(printString); // Timer

                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    Dlog.e("mUSBRealTimeController Thread Error : " + e );
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };

    public DeviceHandler(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void initialize() {
        mDeviceDataTransfer = DeviceDataTransfer.getInstance();
        //mUSBRealTimeController.start();
    }

    public void handleMessage(Message msg) {
        UsbDevice device = (UsbDevice) msg.obj;

        if(msg.what == DeviceManager.MSG_USB_CONNECTION) {
            Dlog.i("Device Manager Monitoring Service Send Message : On USB Connected");
            try {
                mDeviceCommunicator = mDeviceManager.CreateDeviceCommunicator(appCompatActivity.getApplicationContext(), device);
            } catch (Exception e) {
                Dlog.e("handleMessage Error "+ e );
            }

            if(mDeviceCommunicator != null) {
                mDeviceDataTransfer.registerDeviceCommunicator(mDeviceCommunicator);
            }
        } else {
            Dlog.i("Device Manager Monitoring Service Send Message : On USB DisConnected");
            handlingClear();
        }
    }

    public void handlingStart() {
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.DeviceManagerMonitoringStart(appCompatActivity.getApplicationContext(), this);
    }

    public void handlingStop() {
        if(mDeviceCommunicator != null)
        {
            try {
                Dlog.i("Set freeze");
                //DeviceSetting.sendFreeze(mDeviceCommunicator);
                Dlog.i("Complete freeze");
            } catch(Exception e) {
                Dlog.e("Send device freeze error : " + e.getMessage());
            }
        }
        mDeviceManager.DeviceManagerMonitoringClear();
    }

    public void handlingClear() {
        mDeviceDataTransfer.deregisterDeivceCommunicator();
        mDeviceCommunicator = null;

    }

    public void sendData(String[] hexStringArray) {
        if(mDeviceCommunicator != null) {
            DeviceRegisterSetting.writeBulkHexData(mDeviceCommunicator, hexStringArray);
        } else {
            deviceConnectionError();
        }
    }

    public void run() {
        Dlog.i("Run Data");
        if(mDeviceCommunicator != null) {
            DeviceRegisterSetting.run(mDeviceCommunicator);
        } else {
            deviceConnectionError();
        }
    }

    public void resetData() {
        Dlog.i("Reset Data");
        if(mDeviceCommunicator != null) {
            //mDeviceCommunicator.DataTransferReset();
            DeviceRegisterSetting.reset(mDeviceCommunicator);
        } else {
            deviceConnectionError();
        }
    }

    public void startCounter() {
        Dlog.i("Start Counter");
        if(mDeviceCommunicator != null) {
            DeviceRegisterSetting.counterTest(mDeviceCommunicator);
        } else {
            deviceConnectionError();
        }
    }

    public void resetCounter() {
        Dlog.i("Reset Counter");
        if(mDeviceCommunicator != null) {
            DeviceRegisterSetting.counterReset(mDeviceCommunicator);
        } else {
            deviceConnectionError();
        }
    }

    public void deviceConnectionError() {
        Toast warningMessage = Toast.makeText(appCompatActivity.getApplicationContext(),"Please Connect USB Device: "+mDeviceCommunicator, Toast.LENGTH_SHORT);
        warningMessage.show();
    }




}
