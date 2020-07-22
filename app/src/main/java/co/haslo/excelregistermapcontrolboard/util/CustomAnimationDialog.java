package co.haslo.excelregistermapcontrolboard.util;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import co.haslo.excelregistermapcontrolboard.R;

public class CustomAnimationDialog extends ProgressDialog {

    private AppCompatActivity appCompatActivity;

    public CustomAnimationDialog(AppCompatActivity appCompatActivity) {
        super(appCompatActivity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);
        ImageView imageView = (ImageView) findViewById(R.id.img_android);
        Animation anim = AnimationUtils.loadAnimation(appCompatActivity, R.anim.loading);
        imageView.setAnimation(anim);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}