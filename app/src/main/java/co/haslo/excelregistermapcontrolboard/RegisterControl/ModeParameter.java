package co.haslo.excelregistermapcontrolboard.RegisterControl;


import co.haslo.excelregistermapcontrolboard.usbDeviceManager.DeviceCommunicator;


public class ModeParameter {

    /**
     * Center Debug Button
     */
    static int loopSwitchLED = 0;

    public static void toggleLED(DeviceCommunicator device) {
        if(loopSwitchLED%2 == 0) {
            //LED ON

        } else if(loopSwitchLED%2 == 1) {

        }

    }


}
