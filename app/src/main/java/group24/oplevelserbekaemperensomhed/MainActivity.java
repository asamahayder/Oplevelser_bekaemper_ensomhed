package group24.oplevelserbekaemperensomhed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment fragmentHome;
    Fragment fragmentProfile;
    Fragment fragmentCreateEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentHome = new FragmentHome();
        fragmentProfile = new FragmentProfile();
        fragmentCreateEvent = new FragmentCreateEvent();

        final Intent intent = new Intent(this, ActivityCreateEvent.class);

        //This sets the first active fragment
        changeFragment(fragmentHome);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home){
                    changeFragment(fragmentHome);

                }else if (item.getItemId() == R.id.search){

                } else if (item.getItemId() == R.id.create_event){

                    startActivity(intent);

                } else if(item.getItemId() == R.id.navlist){

                } else if (item.getItemId() == R.id.profile){
                    changeFragment(fragmentProfile);
                }

                //This sets the clicked item to be the active one, and gives it another color to stand out.
                item.setChecked(true);

                return false;
            }
        });

    }

    public void changeFragment (Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, fragment).addToBackStack(null).commit();
    }

}