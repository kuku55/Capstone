package com.capstone.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.capstone.emergency.EmergencyActivity;
import com.capstone.user.UserDetailsActivity;
import com.example.dana.capstone.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        dl = findViewById(R.id.activity_contact);
        t = new ActionBarDrawerToggle(this, dl, R.string.Close, R.string.Open);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.emergency:
                        startActivity(new Intent(ContactActivity.this, EmergencyActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        dl.closeDrawers();
                        break;
                    case R.id.contacts:
                        Toast.makeText(ContactActivity.this, "Contacts", Toast.LENGTH_SHORT).show();
                        dl.closeDrawers();
                        break;
                    case R.id.profile:
                        startActivity(new Intent(ContactActivity.this, UserDetailsActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        dl.closeDrawers();
                        break;
                    case R.id.logout:
                        signOut();
                        break;
                }
                return true;
            }
        });

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FamilyFragment(), "Family");
        adapter.addFragment(new PoliceFragment(), "Police");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
    public void signOut() {
        auth.signOut();
    }
}

