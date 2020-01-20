package ru.programminglearning.com.googlemaps.MainContent.PersonalAccount;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ru.programminglearning.com.googlemaps.Maps.ViewWindowMarker.Model;
import ru.programminglearning.com.googlemaps.R;
import ru.programminglearning.com.googlemaps.Utils.BottomSheet.BottomNavigationDrawerFragment;
import ru.programminglearning.com.googlemaps.Utils.Utils;

/**
 *   Поля
 *   databaseReferencere - ссылка на базу данных
 *   user - ссылка на пользователя
 *   */

public class PersonalAccountFragment extends Fragment {

    private DatabaseReference databaseReferencere;
    private FirebaseUser user;
    private Button button;



    /**
     * Инициализируем ссылки на базу данных и пользователя*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReferencere = FirebaseDatabase.getInstance().getReference("Users");
        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        connectGetDatabase();

        return inflater.inflate(R.layout.fragment_personal_account, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null){
            nextActivityAboutApp(view);
            onClickLogOut(view);
        }

    }

    /**
     *получаем данные из базы */
    private void connectGetDatabase(){
        databaseReferencere
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String userName= dataSnapshot.child("userName").getValue(String.class);
                        String userFromCity= dataSnapshot.child("userFromCity").getValue(String.class);
                        String userPhoneNumber= dataSnapshot.child("userPhoneNumber").getValue(String.class);
                        String userEmail= dataSnapshot.child("userEmail").getValue(String.class);

                        if ((userName != null) && (userFromCity != null) && (userPhoneNumber != null)){

                            CustomerModel model = new CustomerModel(userName,userFromCity,userPhoneNumber,userEmail);

                            // передаем данные в элементы
                            setValuesInViews(model.getUserName(),
                                    model.getUserFromCity(),
                                    model.getUserPhoneNumber(),model.getUserEmail());
                        }


                    }

                    // инициализируем и передаем значения во view-элементы
                    private void setValuesInViews(final String name, String city, final String phone, String email){
                        View view = getView();

                        if (view != null) {

                            final EditText nameUserPersonalAccount
                                    = view.findViewById(R.id.nameUserPersonalAccount);
                            final EditText cityUserPersonalAccount
                                    = view.findViewById(R.id.cityUserPersonalAccount);
                            final EditText phoneUserPersonalAccount
                                    = view.findViewById(R.id.phoneUserPersonalAccount);
                            final EditText emailUserPersonalAccount
                                    = view.findViewById(R.id.emailUserPersonalAccount);
                            button =
                                    view.findViewById(R.id.btnSave);

                            nameUserPersonalAccount.setText(name);
                            cityUserPersonalAccount.setText(city);
                            phoneUserPersonalAccount.setText(phone);

                            if (!email.equals("")){
                                emailUserPersonalAccount.setText(email);
                            }
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String names = nameUserPersonalAccount.getText().toString();
                                    String cities = cityUserPersonalAccount.getText().toString();
                                    String phones = phoneUserPersonalAccount.getText().toString();
                                    String emails = emailUserPersonalAccount.getText().toString();

                                    //если поля не пустые
                                    if (!names.equals("") &&
                                            !cities.equals("") &&
                                            (!phones.equals("")) && phones.length() >= 6){
                                        //передаем значения в параметры и затем в базу
                                        connectPostDatabase(names,cities,phones,emails);

                                        //отображаем bottomsheet на 1.5сек
                                        Utils.showBottomSheet(getFragmentManager());

                                    }else {
                                        if (names.equals("")){
                                            nameUserPersonalAccount.setError("Поле не должно быть пустым");
                                        }
                                        if (cities.equals("")){
                                            cityUserPersonalAccount.setError("Поле не должно быть пустым");
                                        }
                                        if (phones.equals("")){
                                            phoneUserPersonalAccount.setError("Поле не должно быть пустым");
                                        }else if (phones.length() < 6){
                                            phoneUserPersonalAccount.setError("Пожалуйста, введите корректный номер");
                                        }

                                    }
                                }
                            });

                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     *отправлям данные в базы */
    private void connectPostDatabase(String name,String city,String phone,String email){
        Map<String,Object> map = new HashMap<>();
        map.put("userName",name);
        map.put("userPhoneNumber",phone);
        map.put("userFromCity",city);
        map.put("userEmail",email);

        databaseReferencere.child(user.getUid()).setValue(map);
    }

    /**
     * Открытие активности с инфой об приложении*/
    private void nextActivityAboutApp(View view){

            Button button = view.findViewById(R.id.btnAboutAppNext);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),AboutApp.class));

                }
            });


    }

    /**
     * Метод содержит кнопку со слушателем, который вызывает диалог окно*/
    private void onClickLogOut(View view){
        Button button = view.findViewById(R.id.btnSignOut);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog();
            }
        });
    }

    /**
     * Диалог окно для выбора: выйти из приложения*/
    private void createAlertDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case DialogInterface.BUTTON_POSITIVE:
                        //YES
                        signOut();
                        getActivity().finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //NO
                        builder.create().dismiss();
                        break;
                }
            }
        };

        builder.setMessage("Выйти из аккаунта?")
                .setPositiveButton("Да", dialogClickListener)
                .setNegativeButton("Нет", dialogClickListener).show();
    }

    /**Выходим из Firebase*/
    private void signOut(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
    }

}
