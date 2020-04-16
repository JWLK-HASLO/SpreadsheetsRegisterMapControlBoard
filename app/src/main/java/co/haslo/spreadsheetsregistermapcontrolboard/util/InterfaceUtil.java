package co.haslo.spreadsheetsregistermapcontrolboard.util;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class InterfaceUtil {

    public static void scrollBottom(TextView textView) {
        int lineTop =  textView.getLayout().getLineTop(textView.getLineCount()) ;
        int scrollY = lineTop - textView.getHeight();
        if (scrollY > 0) {
            textView.scrollTo(0, scrollY);
        } else {
            textView.scrollTo(0, 0);
        }
    }

    public static void scrollTop(TextView textView) {
        int lineTop =  textView.getLayout().getLineTop(textView.getLineCount()) ;
        int scrollY = textView.getHeight()-lineTop;
        if (scrollY > 0) {
            textView.scrollTo(0, 0);
        } else {
            textView.scrollBy(0, scrollY);
        }
    }

    public static void showToast(Context context, String stringValue) {
        final Toast toast = Toast.makeText(context, stringValue, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }



}
