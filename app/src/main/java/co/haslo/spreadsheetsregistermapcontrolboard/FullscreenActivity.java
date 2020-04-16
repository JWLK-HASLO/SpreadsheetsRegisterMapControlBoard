package co.haslo.spreadsheetsregistermapcontrolboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import co.haslo.spreadsheetsregistermapcontrolboard.util.CustomAnimationDialog;
import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    /**
     * Get Function Activity
     */
    private Dlog mDlog = new Dlog(this);
    private FullscreenClickAction mFullscreenClickAction = new FullscreenClickAction(this);
    private FullscreenController mFullscreenController = new FullscreenController(this);
    private FullscreenLogBox mFullscreenLogBox = new FullscreenLogBox(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        if(mDlog != null){
            boolean isDebuggable = Dlog.isDebuggable();
            Dlog.d("Debugging Status: "+isDebuggable);
        }

        mFullscreenClickAction.initialize();
        mFullscreenController.initialize();
        mFullscreenLogBox.initialize();

    }

}
