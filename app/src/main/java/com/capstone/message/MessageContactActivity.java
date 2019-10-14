package com.capstone.message;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.capstone.FingerprintHandler;
import com.capstone.contact.Contact;
import com.capstone.contact.ContactDetailsActivity;
import com.capstone.json.MySingleton;
import com.capstone.user.User;
import com.example.dana.capstone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MessageContactActivity extends AppCompatActivity {

    private LinearLayout linearLayout;

    private String cName, cNumber, cRelationship;
    private EditText txtCMessage;
    private Spinner spnType;
    private Button btnCSend, btnCBack;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    private static final String KEY_UID = "uID";
    private static final String KEY_NAME = "name";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_SUBJECT = "subject";

    private Date currentTime;
    private String messageID;
    private String uid;
    private String message;
    private String subject;
    private String receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_contact);

        auth = FirebaseAuth.getInstance();
        linearLayout = findViewById(R.id.linearLayout8);

        spnType = findViewById(R.id.spnSubject);
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
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                String presentDate = df.format(currentTime);
                messageID = currentTime + auth.getUid() + "2" + cName; //2 == to
                uid = auth.getUid();
                message = txtCMessage.getText().toString().trim();
                subject = spnType.getSelectedItem().toString();
                receiver = cName; //changeable to cID + cName

                JSONObject request = new JSONObject();
                try {
                    //Populate the request parameters
                    request.put(KEY_UID, uid);
                    request.put(KEY_NAME, cName);
                    request.put(KEY_MESSAGE, message);
                    request.put(KEY_SUBJECT, subject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MessageContactActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
                String register_url = "https://e-ligtas.000webhostapp.com/json/emergency.php";
                JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                        (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getApplicationContext(), "Emergency Sent", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                // Access the RequestQueue through your singleton class.
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
//                finish();

                MessageContact mc = new MessageContact(messageID, uid, subject, message, receiver, presentDate);
                if(subject.isEmpty()){
                    subject = "Emergency";
                }

                if(message.isEmpty()){
                    message = "Emergency at " + currentTime + presentDate;
                }
                databaseReference.child("Messages").child(messageID).setValue(mc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //snackbar for sending message
                        txtCMessage.setText("");
                        sendSMSMessage();
                        final Snackbar snackbar = Snackbar
                                .make(linearLayout, "Message sent!",
                                        Snackbar.LENGTH_INDEFINITE).setAction("Close", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //leave empty
                                    }
                                });
                        snackbar.show();
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
