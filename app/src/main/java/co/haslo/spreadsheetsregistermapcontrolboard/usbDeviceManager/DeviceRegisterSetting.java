package co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager;


import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;

import static co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertDataType.hexStringToShort16bit4HexArray;
import static co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertDataType.hexStringArrayToInt32bit8HexArray;


public class DeviceRegisterSetting {

    public static void writeCommandHexData(DeviceCommunicator device, String hexString)
    {
        short[] dataShort = hexStringToShort16bit4HexArray(hexString);
        for(short shorts : dataShort){
            Dlog.d(String.format("0x%04X", shorts));
        }

        try {
            device.DataTransferSingleWrite(dataShort[0], dataShort[1]);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void writeBulkHexData(DeviceCommunicator device, String[] mHexStringArray)
    {
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(mHexStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(mHexStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void run(DeviceCommunicator device)
    {
        String[] sendStringArray =  {"98000003"};
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(sendStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void reset(DeviceCommunicator device)
    {
        String[] sendStringArray =  {"98000000"};
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(sendStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void counterTest(DeviceCommunicator device)
    {
        String[] sendStringArray =  {"98000000","980103FF", "9807001F","98080FFF","980900FF"};
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(sendStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

    public static void counterReset(DeviceCommunicator device)
    {
        String[] sendStringArray =  {"98000000", "98010000", "98000003"};
        int[] sendIntArray = hexStringArrayToInt32bit8HexArray(sendStringArray);
        for(int ints : sendIntArray){
            Dlog.d(String.format("0x%04X", ints));
        }
        try {
            device.DataTransferBulkWrite(sendStringArray);
        } catch (Exception e) {
            e.printStackTrace();
            Dlog.e(e.toString());
        }

    }

}
