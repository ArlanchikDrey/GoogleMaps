package ru.programminglearning.com.googlemaps.MainContent.PersonalAccount;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import ru.programminglearning.com.googlemaps.R;
import ru.programminglearning.com.googlemaps.Utils.Utils;

public class AboutApp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        if (Build.VERSION.SDK_INT > 21){
            Utils style =  new Utils();
            style.setTranslucentStatus(this,true); // делаем тему шторки прозрачной
        }
        back();
    }

    public void back() {
        ImageButton button = findViewById(R.id.btnBackDevelop);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }
}
