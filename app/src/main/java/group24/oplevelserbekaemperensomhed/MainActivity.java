package group24.oplevelserbekaemperensomhed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment fragmentHome;
    Fragment fragmentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentHome = new FragmentHome();
        fragmentProfile = new FragmentProfile();


        //This sets the first active fragment
        changeFragment(fragmentHome);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //item.getIcon().setTint(ContextCompat.getColor(getParent(),R.color.com_facebook_blue));

                if (item.getItemId() == R.id.home){
                    changeFragment(fragmentHome);

                }else if (item.getItemId() == R.id.search){

                } else if(item.getItemId() == R.id.navlist){

                } else if (item.getItemId() == R.id.profile){
                    changeFragment(fragmentProfile);

                }
                return false;
            }
        });

    }

    public void changeFragment (Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, fragment).addToBackStack(null).commit();
    }

}