package group24.oplevelserbekaemperensomhed.view.profile.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import group24.oplevelserbekaemperensomhed.R;
import group24.oplevelserbekaemperensomhed.data.LocalData;
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack;
import group24.oplevelserbekaemperensomhed.view.login.ActivityLogin;

public class Settings extends AppCompatActivity {

    Button btn_terms;
    Button btn_contact;
    Button btn_log_ud;
    Button btn_delete_acc;
    ImageView btn_back;

    LocalData localData = LocalData.INSTANCE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btn_back = (ImageView) findViewById(R.id.back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            finish();
            }

        });



        btn_terms = (Button) findViewById(R.id.button1);
        btn_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PopActivity.class);
                startActivity(i);
            }
        });

        btn_contact = (Button) findViewById(R.id.button2);
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PopActivityKont.class);
                startActivity(intent);
            }
        });


        //Log out
        btn_log_ud = (Button) findViewById(R.id.button3);
        btn_log_ud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
                finishAffinity();


            }
        });

        // Delete an account
        btn_delete_acc = (Button) findViewById(R.id.button4);
        btn_delete_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDAO fb = new FirebaseDAO();
                fb.deleteUser(localData.getId(), new MyCallBack() {
                    @Override
                    public void onCallBack(@NotNull Object object) {
                        boolean status = (boolean) object;
                        if (status) {

                            FirebaseAuth auth = FirebaseAuth.getInstance();


                            auth.getCurrentUser().delete();

                            Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                            startActivity(intent);
                            finishAffinity();

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Could not delete the user :(",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });



    }
}