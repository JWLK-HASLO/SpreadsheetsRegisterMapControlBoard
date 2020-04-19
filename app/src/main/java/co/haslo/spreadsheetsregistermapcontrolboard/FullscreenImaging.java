package co.haslo.spreadsheetsregistermapcontrolboard;

import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import co.haslo.spreadsheetsregistermapcontrolboard.util.Dlog;

public class FullscreenImaging {
    private AppCompatActivity appCompatActivity;

    FullscreenImaging(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    void initialize() {
        Dlog.d("Ready");
        loadDataImaging();
    }

    private void loadDataImaging() {
        ImageView mImage = appCompatActivity.findViewById(R.id.data_image);
        Glide.with(appCompatActivity)
                .load(R.raw.echo01)
                .fitCenter()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(mImage);
    }

}
