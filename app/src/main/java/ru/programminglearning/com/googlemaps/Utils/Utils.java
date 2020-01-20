package ru.programminglearning.com.googlemaps.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import ru.programminglearning.com.googlemaps.Utils.BottomSheet.BottomNavigationDrawerFragment;

public class Utils {

    /**
     * стиль для actionBar*/
    public void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * Проверка, работает ли GPS*/
    public static boolean isGpsTrue(Context context) {
        int locationMode = 0;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY) {
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * отображаем bottomsheet на 1.5сек*/
    public static void showBottomSheet(FragmentManager fragmentManager){
        final BottomNavigationDrawerFragment bottomNavigationView = new BottomNavigationDrawerFragment();
        assert fragmentManager != null;
        bottomNavigationView.show(fragmentManager,"Tag");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomNavigationView.dismiss();
            }
        },1500);
    }



}
