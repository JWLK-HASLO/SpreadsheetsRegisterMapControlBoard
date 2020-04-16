package co.haslo.spreadsheetsregistermapcontrolboard;

import android.icu.text.SimpleDateFormat;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;
import co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil;

public class FullscreenLogBox {

    private StringBuilder log = null;
    private int lengthSaver = 0;
    private SimpleDateFormat formatPrint = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss");

    /*Layout Element*/
    private TextView logBoxText;
    private TextView logBoxSelected;

    /*Get Parent Element*/
    private AppCompatActivity appCompatActivity;

    FullscreenLogBox(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    void initialize() {
        Dlog.d("Ready");
        logBoxText =  appCompatActivity.findViewById(R.id.log_box_text);
        logBoxText.setMovementMethod(new ScrollingMovementMethod());
        logBoxSelected =  appCompatActivity.findViewById(R.id.log_box_selected);
        logBoxSelected.setMovementMethod(new ScrollingMovementMethod());
        mUSBRealTimeController.start();
    }

    public void setLogcat(){
        logBoxText.setText(getLogcat());
        logBoxSelected.setText(getLogcat());
    }

    public String getLogcat() {
        try {
            log = new StringBuilder();
            /*System Element*/
            Process logcat = Runtime.getRuntime().exec("logcat -d -v tag");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(logcat.getInputStream()),4*1024);
            String lineString;
            String separator = System.lineSeparator();
            while ((lineString = bufferedReader.readLine()) != null) {
                if(lineString.contains("JWLK")){
                    log.append(lineString);
                    log.append(separator);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return log.toString();
    }

    public void clearLogcatLog() {
        try {
            @SuppressWarnings("unused")
            Process process = Runtime.getRuntime().exec("logcat -b all -c");
            logBoxText.append("Please Click Load");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSelectedLogcat() {
        String retunrString = null;

        return retunrString;
    }

    private Thread mUSBRealTimeController = new Thread(){
        @Override
        public void run() {
            while (!isInterrupted()) {

                Date currentTime = new Date();
                String printString = formatPrint.format(currentTime);
                Dlog.i(printString); // Timer

                try
                {
                    Thread.sleep(1000);
                    if(getLogcat().length() > lengthSaver){
                        lengthSaver = getLogcat().length();
                        setLogcat();
                        InterfaceUtil.scrollBottom(logBoxText);
                        InterfaceUtil.scrollBottom(logBoxSelected);
                    }
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
}
