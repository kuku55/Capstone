package com.capstone.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.capstone.fingerprint.FingerScanActivity;
import com.capstone.location.EmergencyLocation;
import com.example.dana.capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {
    private String cid;
    private String uid;
    private String name;
    private String age;
    private EditText message, subject;
    private DatabaseReference databaseMessage;
    private DatabaseReference searchDetails;
    private Button send, back;
    private ProgressBar progressBar;
    DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy HH:mm:ss", Locale.US);
    Date date = new Date();
    private FirebaseAuth auth;
    String phoneNo;
    String messager;
    Date c = null;
    SimpleDateFormat df = null;
    String presentDate = null;
    String msg = null;
    String sub = null;
    String isRead = null;
    Boolean check = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        auth = FirebaseAuth.getInstance();
        message = findViewById(R.id.txtMessage);
        subject = findViewById(R.id.txtSubject);
        cid =  getIntent().getStringExtra("CONTACT_ID");
        uid = getIntent().getStringExtra("CURRENT_ID");
        name = getIntent().getStringExtra("NAME");
        age = getIntent().getStringExtra("AGE");
        final EmergencyLocation el = (EmergencyLocation) getIntent().getSerializableExtra("EMERGENCY");
        back = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send = findViewById(R.id.btnSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance().getTime();
                df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                presentDate = df.format(c);
                msg = message.getText().toString().trim();
                sub = subject.getText().toString().trim();
                isRead = "unread";
                //progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), FingerScanActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("uid", uid);
                intent.putExtra("cid", cid);
                intent.putExtra("msg", msg);
                intent.putExtra("sub", sub);
                intent.putExtra("presentDate", presentDate);
                intent.putExtra("isRead", isRead);
                intent.putExtra("el", el);

                startActivity(intent);
            }
        });


    }
}
