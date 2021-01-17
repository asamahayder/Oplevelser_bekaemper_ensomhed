package group24.oplevelserbekaemperensomhed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import group24.oplevelserbekaemperensomhed.view.profile.FragmentProfile;
import group24.oplevelserbekaemperensomhed.view.search.FragmentSearchHome;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment fragmentHome;
    Fragment fragmentProfile;
    Fragment fragmentSearch;
    Fragment fragmentCreateEvent;
    Fragment fragmentList;
    int currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentHome = new FragmentHome();
        fragmentProfile = new FragmentProfile();
        fragmentSearch = new FragmentSearchHome();
        fragmentList = new FragmentList();

        final Intent intent = new Intent(this, ActivityCreateEvent.class);

        //This sets the first active fragment
        changeFragment(fragmentHome, getString(R.string.fragment_home));
        currentFragment = 0;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home){
                    changeFragment(fragmentHome, getString(R.string.fragment_home));
                    currentFragment = 0;

                }else if (item.getItemId() == R.id.search){
                    changeFragment(fragmentSearch, getString(R.string.fragment_search));
                    currentFragment = 1;

                } else if (item.getItemId() == R.id.create_event){
                    currentFragment = 2;
                    startActivity(intent);

                } else if(item.getItemId() == R.id.navlist){
                    changeFragment(fragmentList, getString(R.string.fragment_list));
                    currentFragment = 3;

                } else if (item.getItemId() == R.id.profile){
                    changeFragment(fragmentProfile, getString(R.string.fragment_profile));
                    currentFragment = 4;
                }

                //This sets the clicked item to be the active one, and gives it another color to stand out.
                if (currentFragment != 2){
                    item.setChecked(true);
                }


                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void changeFragment (Fragment fragment, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations();
        transaction.replace(R.id.mainFragment, fragment, tag);
        //if (addToBackStack) {
        //    transaction.addToBackStack(tag);
        //}
        transaction.commit();
    }

    public void slideActivityInto(){
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void slideActivityOut(){
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}