package co.haslo.excelregistermapcontrolboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.List;

import co.haslo.excelregistermapcontrolboard.util.Dlog;
import pub.devrel.easypermissions.EasyPermissions;

public class InitSplash extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    /*EasyPermissions Request Value*/
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICE = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;


    public Dlog mDlog = new Dlog(this);
    public static GoogleAccountCredential globalCredential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};

    Handler hd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_splash);

        if(mDlog != null){
            boolean isDebuggable = Dlog.isDebuggable();
            Dlog.d("Debugging Status: "+isDebuggable);
        }

//        globalCredential =  GoogleAccountCredential.usingOAuth2(
//                getApplicationContext(), Arrays.asList(SCOPES))
//                .setBackOff((new ExponentialBackOff()));
        getResultsFormAPI();
    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), FullscreenActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            InitSplash.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }


    /*GET API Permission*/
    private void getResultsFormAPI() {
            hd = new Handler();
            hd.postDelayed(new splashhandler(), 5000); // 1초 후에 hd handler 실행  3000ms = 3초

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}
