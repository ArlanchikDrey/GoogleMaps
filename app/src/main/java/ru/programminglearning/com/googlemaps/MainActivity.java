package ru.programminglearning.com.googlemaps;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.programminglearning.com.googlemaps.MainContent.ActivityContent;
import ru.programminglearning.com.googlemaps.Regist.SignAuth;

public class MainActivity extends AppCompatActivity {

    //получаем данные о пользователе
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (currentUser != null){
            startActivity(new Intent(this, ActivityContent.class));
            finish();
        }else{
            startActivity(new Intent(this, SignAuth.class));
            finish();

        }
    }


}
