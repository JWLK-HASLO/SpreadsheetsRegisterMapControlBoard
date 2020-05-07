package co.haslo.spreadsheetsregistermapcontrolboard.usbDeviceManager;


import android.util.Log;

import java.util.ArrayList;

import co.haslo.spreadsheetsregistermapcontrolboard.util.ConvertDataType;
import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;

public class DeviceDataTransfer {

    public static final int SEQUENCE_DATA_SIZE = 4096;
//    public static final int BYTE = 4*7;
    public static final int BYTE = 4;
    public static final int BULK_COUNT = 9;

    private static final int defaultByeDataSize = SEQUENCE_DATA_SIZE * BYTE;
    public static int defaultBulkCounter = 0;


    private static DeviceDataTransfer mDataTransferInstance = null;
    private static final Object mSyncBlock = new Object();

    private Thread mDataTransferThread;
    private DeviceCommunicator mDeviceCommunicator;
    private final Object mDataTransferBlock;

    public static String[] hexaArray = new String[SEQUENCE_DATA_SIZE*BULK_COUNT];
    public static byte[] bufferArray = new byte[defaultByeDataSize*BULK_COUNT];


    private class DeviceDataTransferThread extends Thread {

        public DeviceDataTransferThread() {
            super();
        }

        public void run(){

            final byte[] readBuffer = new byte[defaultByeDataSize];
            int readSize;
            String convertString;
            //String[] convertHexaArray = new String[SEQUENCE_SIZE];
            while (!isInterrupted()) {
                try
                {
                    readSize = mDeviceCommunicator.ReadBulkTransfer(readBuffer, 0, defaultByeDataSize);
                    if(isInterrupted()) {
                        Dlog.i("Thread is interrupted");
                        break;
                    }

                } catch (Exception e) {
                    Dlog.e("Thread Read Exception : " + e);
                    readSize = -1;
                }

                if(readSize <= -1) {
                    continue;
                } else {
                    defaultBulkCounter++;
                    Dlog.i("DeviceDataTransferThread readSize : "+ readSize+ "/" + defaultBulkCounter);
                    System.arraycopy(readBuffer,0, bufferArray, (16384*defaultBulkCounter), defaultByeDataSize);
                }


//                for(int i = 0, counter = 0; i < defaultByeDataSize; i+=4, counter++) {
//                    //Dlog.d("defaultBulkCounter : " + defaultBulkCounter);
//                    byte Data03 = readBuffer[i + 3];
//                    byte Data02 = readBuffer[i + 2];
//                    byte Data01 = readBuffer[i + 1];
//                    byte Data00 = readBuffer[i + 0];
//                    byte[] DataArray = {Data03,Data02,Data01,Data00};
//                    convertString = ConvertDataType.byteArrayToHexString(DataArray);
//                    //Dlog.i("RX["+ i +"] - "+ convertString);
//                    hexaArray[4096*(defaultBulkCounter)+counter] = convertString;
////                    hexaArray[counter] = convertString;
//                }
                for (int bulk = 0; bulk < BULK_COUNT; bulk++) {
//                    System.arraycopy(readBuffer,0, bufferArray, (4096*bulk), defaultByeDataSize);
                }


            }

        }

    }

    private DeviceDataTransfer() {
        mDataTransferThread = null;
        mDataTransferBlock = new Object();
        mDeviceCommunicator = null;

    }

    public static DeviceDataTransfer getInstance() {
        if(mDataTransferInstance != null) {
            return mDataTransferInstance;
        }
        synchronized (mSyncBlock) {
            if(mDataTransferInstance == null) {
                mDataTransferInstance = new DeviceDataTransfer();
            }
        }
        return mDataTransferInstance;
    }


    //Communicator Set && Thread Start
    public void registerDeviceCommunicator(DeviceCommunicator communicator) {
        Dlog.i("Device Communicator Setting...");
        synchronized (mDataTransferBlock) {
            _interruptThreadAndReleaseUSB();
            mDeviceCommunicator = communicator;
            mDataTransferThread = new DeviceDataTransferThread();
            mDataTransferThread.start();
        }
        Dlog.i("Device Communicator Setting Complete!");
    }

    private void _interruptThreadAndReleaseUSB() {
        Dlog.i("Interrupt Thread Connection trying...");

        if(mDataTransferThread != null){
            mDataTransferThread.interrupt();
            try {
                mDataTransferThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mDataTransferThread = null;
        }
        if(mDeviceCommunicator != null) {
            mDeviceCommunicator.Clear();
            mDeviceCommunicator = null;
        }
        Dlog.i("Interrupt Thread Connection Complete!");
    }

    public void deregisterDeivceCommunicator() {
        Dlog.i("Device Communicator Resetting`...");
        synchronized (mDataTransferBlock)
        {
            _interruptThreadAndReleaseUSB();
        }
        Dlog.i("Device Communicator Reset Complete!");
    }

}
