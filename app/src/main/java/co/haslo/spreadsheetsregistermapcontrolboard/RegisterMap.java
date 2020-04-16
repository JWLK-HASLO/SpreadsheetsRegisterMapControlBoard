package co.haslo.spreadsheetsregistermapcontrolboard;

import android.app.Dialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.Date;
import java.util.List;

import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;

import static co.haslo.spreadsheetsregistermapcontrolboard.util.InterfaceUtil.showToast;

public class RegisterMap extends AppCompatActivity {

    RegisterMapController mRegisterMapController = new RegisterMapController(this);
    List<List<Object>> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_register_map);
        Dlog.d("Start : RegisterMap onCreate");
        mRegisterMapController.initialize();
        readThread.start();
        loadCloudData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                this.finish();
                return true;
            }
            case R.id.button_reload:{
                showToast(this,"Reload");
                loadCloudData();
                return true;
            }
            case R.id.button_save:{
                showToast(this,"Save");
                return true;
            }
            case R.id.button_setting:{
                showToast(this,"Setting");
                return true;
            }


        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_fullscreen, menu);
        return true;
    }


    /**
     * Load Data
     */
    public void loadCloudData() {

        try {
            readThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (values == null || values.isEmpty()) {
            Dlog.d("No data found.");
        } else {
            Dlog.d("ADDRESS, REGISTER");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                Dlog.d(String.format("%s, %s", row.get(0), row.get(1)));
            }
        }

    }

    /**
     *  Read Sheet
     */

    HttpTransport transport = AndroidHttp.newCompatibleTransport();
    JsonFactory factory = JacksonFactory.getDefaultInstance();
    final Sheets sheetsService = new Sheets.Builder(transport, factory, null)
            .setApplicationName("My Awesome App")
            .build();
    final String spreadsheetId = Config.spreadsheet_id;

    private SimpleDateFormat formatPrint = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss");

    Thread readThread = new Thread() {
        @Override
        public void run() {
            Date currentTime = new Date();
            String printString = formatPrint.format(currentTime);
            Dlog.i(printString); // Timer
            try {
                Thread.sleep(1000);
                String range = "Sheet1!A1:B32";
                ValueRange response = sheetsService.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .setKey(Config.google_api_key)
                        .execute();
                int numRows = response.getValues() != null ? response.getValues().size() : 0;
                values = response.getValues();

                Dlog.d("Rows Total " + numRows);
            } catch (InterruptedException e) {
                Dlog.e("Thread Error : " + e );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
