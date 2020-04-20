package co.haslo.spreadsheetsregistermapcontrolboard.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ConvertSheetType {

    public static String[] convertDownloadString(List<String> mDtaList) {
        String[] resultStringArray = mDtaList.toArray(new String[0]);
        return resultStringArray;
    }

    public static String convertUploadString(String[] mStringArray) {
        String resultString = null;
        int dataSize = mStringArray.length;
        List<String> mListString = new ArrayList<String>();;

        for(int i = 0; i < dataSize; i++){
            mListString.add("[\""+mStringArray[i]+"\"]") ;
        }

        TextUtils.join(",", mListString);
        resultString = mListString.toString();

        return resultString;
    }

}
