package group24.oplevelserbekaemperensomhed.search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import group24.oplevelserbekaemperensomhed.R;
import group24.oplevelserbekaemperensomhed.data.LocalData;
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack;
import group24.oplevelserbekaemperensomhed.view.login.ActivityLogin;

public class Indstillinger extends AppCompatActivity {

    Button btn_vilkår;
    Button btn_kontakt;
    Button btn_log_ud;
    Button btn_delete_acc;

    LocalData localData = LocalData.INSTANCE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        btn_vilkår = (Button) findViewById(R.id.button1);
        btn_vilkår.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PopActivity.class);
                startActivity(i);
            }
        });


        btn_kontakt = (Button) findViewById(R.id.button2);
        btn_kontakt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PopActivityKont.class);
                startActivity(intent);

            }
        });

        btn_log_ud = (Button) findViewById(R.id.button3);
        btn_log_ud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
                            finish();

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