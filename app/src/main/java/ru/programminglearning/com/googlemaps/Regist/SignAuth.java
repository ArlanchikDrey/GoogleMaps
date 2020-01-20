package ru.programminglearning.com.googlemaps.Regist;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.HashMap;
import java.util.Map;

import ru.programminglearning.com.googlemaps.MainContent.ActivityContent;
import ru.programminglearning.com.googlemaps.R;
import ru.programminglearning.com.googlemaps.Utils.Utils;

public class SignAuth extends AppCompatActivity {

    private GoogleSignInClient mGoogleClient;
    private FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);

        // Настройка Входа В Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //экземпляр класса GoogleSignInClient получает эти настройки
        mGoogleClient = GoogleSignIn.getClient(this, gso);

        //инициализация аунтефикации Firebase
        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT > 21){
            Utils style =  new Utils();
            style.setTranslucentStatus(this,true); // делаем тему шторки прозрачной
        }

        setStyleTitleText();

    }


    public void onClickAuth(View view) {
        if (getPhoneNumber() != null && getCityName() != null && getUserName() != null){
            signInIntent();
        }
    }

    /**
     * После интеграции Google входа в систему, вход в окно выбора аакаунта имеет код:
     */
    private void signInIntent() {
        Intent signInIntent = mGoogleClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private String getPhoneNumber(){
        CountryCodePicker ccp = findViewById(R.id.ccp);
        EditText text = findViewById(R.id.setNumberUserText);
        ccp.registerCarrierNumberEditText(text);

        if (text.getText().length() == 13 ){
            return ccp.getFullNumberWithPlus();
        }else{
            text.setError("Некорректно введен номер");
            return null;
        }

    }

    private String getUserName() {
        EditText editText = findViewById(R.id.getUserName);
        if (editText.getText().length() != 0){
            return editText.getText().toString();
        }else{
            editText.setError("Заполните поле");
            return null;
        }

    }

    private String getCityName() {
        EditText editText = findViewById(R.id.getUserCity);
        if (editText.getText().length() != 0){
            return editText.getText().toString();
        }else{
            editText.setError("Заполните поле");
            return null;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Результат, возвращенный при запуске Intent из GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Вход в Google был успешным, аутентификация с Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Ошибка входа в Google
                Log.w("GoogleSign", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                         onStartActivity();
                         setValueToFirebase();

                        } else {
                            String error = "Ошибка входа:(" + "\n"
                                    + "Проверьте наличие интернета";

                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, error, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Запуск активности происхлодит при успешном входе*/
    private void onStartActivity(){
        startActivity( new Intent(this, ActivityContent.class));
        finish();
    }

    private void setValueToFirebase(){
        final FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.hasChild(userId))){ //если юзера нет в базе

                    Map<String,Object> map = new HashMap<>();
                    map.put("userName",getUserName());
                    map.put("userPhoneNumber",getPhoneNumber());
                    map.put("userFromCity",getCityName());
                    map.put("userEmail","");

                    databaseReference.child(userId).setValue(map);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setStyleTitleText(){
        TextView textView = findViewById(R.id.titleName);
        textView.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/TotShriftBold.otf"));

    }


}
