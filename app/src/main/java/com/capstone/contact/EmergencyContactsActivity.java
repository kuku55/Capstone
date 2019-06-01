package com.capstone.contact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.dana.capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class EmergencyContactsActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference databaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
        setTitle("Emergency Contacts");
    }
}
