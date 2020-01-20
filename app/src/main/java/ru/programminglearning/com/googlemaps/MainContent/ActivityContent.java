package ru.programminglearning.com.googlemaps.MainContent;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ru.programminglearning.com.googlemaps.MainContent.ListAllCarWash.ListAllCarWashFragment;
import ru.programminglearning.com.googlemaps.MainContent.PersonalAccount.PersonalAccountFragment;
import ru.programminglearning.com.googlemaps.MainContent.RecordsUser.RecordsUsersFragment;
import ru.programminglearning.com.googlemaps.MainContent.Sales.SalesFragment;
import ru.programminglearning.com.googlemaps.Maps.Maps;
import ru.programminglearning.com.googlemaps.R;
import ru.programminglearning.com.googlemaps.Utils.Utils;

public class ActivityContent extends AppCompatActivity   {
    private TextView mTextMessage;
    private Switch aSwitch;
    private RelativeLayout container_switch;
    private Fragment selectedFragment;
    private BottomNavigationView navigation;

    // слушатель на нижнюю панель навигации
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_personal_account:

                    setVisibilityandTextTitle(View.GONE,R.string.personal_account);

                    selectedFragment = new PersonalAccountFragment();
                    break;
                case R.id.navigation_record:

                    setVisibilityandTextTitle(View.VISIBLE,R.string.title_activity_maps);

                    selectedFragment = new Maps();
                    aSwitch.setChecked(false);
                    aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked){
                                selectedFragment = new ListAllCarWashFragment();
                            }else{
                                selectedFragment = new Maps();
                            }
                            setFragmentTransaction();
                        }
                    });
                    break;
                case R.id.navigation_my_records:

                    setVisibilityandTextTitle(View.GONE,R.string.record);

                    selectedFragment = new RecordsUsersFragment();
                    break;
                case R.id.navigation_discount:

                    setVisibilityandTextTitle(View.GONE,R.string.discount);

                    selectedFragment = new SalesFragment();
                    break;
            }

            setFragmentTransaction();

            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        setThemeWindowActionBar();

        initViews();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // позиция нижней навигации
        if (savedInstanceState != null){
            navigation.setSelectedItemId(savedInstanceState.getInt("selectedItemId"));
        }else{
            navigation.setSelectedItemId(R.id.navigation_record);
        }


    }

    /**
     * Сохраняем выбранную позицию навигации*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItemId",navigation.getSelectedItemId());
    }

    /**
     * Транзация по фрагментам, используется в нижней навиции и в слушателе switch'а*/
    private void setFragmentTransaction() {
        getSupportFragmentManager()
                .beginTransaction().
                replace(R.id.container_fragments_bottom, selectedFragment).commit();
    }


    private void setVisibilityandTextTitle(int visibil, int title){
        container_switch.setVisibility(visibil);
        mTextMessage.setText(title);
    }

    /**
     *  задаем actionbar стиль от тулбара
     */
    private void setThemeWindowActionBar (){
        if (Build.VERSION.SDK_INT > 21) {
            Utils style = new Utils();
            style.setTranslucentStatus(this, true);
        }
    }

    private void initViews(){
        mTextMessage = findViewById(R.id.message);
        container_switch = findViewById(R.id.container_switch);
        aSwitch = findViewById(R.id.switch_choice);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
    }
}
