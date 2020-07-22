package co.haslo.excelregistermapcontrolboard.RegisterControl;

import java.util.ArrayList;

import co.haslo.excelregistermapcontrolboard.usbDeviceManager.DeviceCommunicator;

public class SendRegister {

    private DeviceCommunicator deviceCommunicator;
    private String[] sendStringArray;

    public void set(DeviceCommunicator device, ArrayList<String> array) {

        deviceCommunicator = device;
        sendStringArray =  array.toArray(new String[array.size()]);
    }



}
