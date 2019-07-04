package com.capstone.message;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.FingerprintHandler;
import com.capstone.contact.Contact;
import com.capstone.contact.ContactDetailsActivity;
import com.capstone.user.User;
import com.example.dana.capstone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class MessageContactActivity extends AppCompatActivity {

    private String cName, cNumber, cRelationship;
    private EditText txtCSubject, txtCMessage;
    private Button btnCSend, btnCBack;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0;

    private Date currentTime;
    private String messageID;
    private String uid;
    private String message;
    private String subject;
    private String receiver;
    private String dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_contact);

        auth = FirebaseAuth.getInstance();

        txtCSubject = findViewById(R.id.txtCSubject);
        txtCMessage = findViewById(R.id.txtCMessage);
        btnCSend = findViewById(R.id.btnCSend);
        btnCBack = findViewById(R.id.btnCBack);

        cName = getIntent().getStringExtra("nameKey");
        cNumber = getIntent().getStringExtra("numberKey");
        cRelationship = getIntent().getStringExtra("relationshipKey");


        fingerprintRead();

        btnCSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference = FirebaseDatabase.getInstance().getReference();

                currentTime = Calendar.getInstance().getTime();
                messageID = currentTime + auth.getUid() + "2" + cName; //2 == to
                uid = auth.getUid();
                message = txtCMessage.getText().toString().trim();
                subject = txtCSubject.getText().toString().trim();
                receiver = cName; //changeable to cID + cName
                dateTime = currentTime.toString().trim();

                MessageContact mc = new MessageContact(messageID, uid, subject, message, receiver, dateTime);
                databaseReference.child("Messages").child(messageID).setValue(mc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MessageContactActivity.this, "Message has been sent.", Toast.LENGTH_SHORT).show();
                        txtCSubject.setText("");
                        txtCMessage.setText("");
                        sendSMSMessage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessageContactActivity.this, "There's an error in sending the message, please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btnCBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageContactActivity.this, ContactDetailsActivity.class);
                intent.putExtra("nameKey", cName);
                intent.putExtra("numberKey", cNumber);
                intent.putExtra("relationshipKey", cRelationship);
                startActivity(intent);
            }
        });
    }

    private void fingerprintRead() {
        //checks if SDK is Marshmallow or above
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if(!fingerprintManager.isHardwareDetected()){

                Toast.makeText(this, "No fingerprint scanner detected.", Toast.LENGTH_LONG).show();

            } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) !=
                    PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this, "Permission not granted to use the Fingerprint Scanner", Toast.LENGTH_LONG).show();

            } else if (!keyguardManager.isKeyguardSecure()) {

                Toast.makeText(this, "Add lock to your device.", Toast.LENGTH_LONG).show();

            }else if(!fingerprintManager.hasEnrolledFingerprints()){

                Toast.makeText(this, "Add a fingerprint in the device.", Toast.LENGTH_LONG).show();

            } else {

                FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
                fingerprintHandler.startAuth(fingerprintManager, null);
            }
        }
    }

    // This Method for Send a message
    protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(cNumber, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            cNumber + " " + message, Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
}
