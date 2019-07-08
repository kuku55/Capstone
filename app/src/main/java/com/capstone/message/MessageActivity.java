package com.capstone.message;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.capstone.FingerprintHandler;
import com.capstone.json.MySingleton;
import com.capstone.location.EmergencyLocation;
import com.capstone.login.SignUpActivity;
import com.example.dana.capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {
    private String cid;
    private String uid;
    private String name;
    private String cNumber = "639390010572"; //police number.
    private String age;
    private EditText message, subject;
    private DatabaseReference databaseMessage;
    private Button send, back;
    private static final String KEY_UID = "uID";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_CITY = "city";
    private static final String KEY_STATE = "state";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_POSTAL = "postalCode";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_MESSAGE = "message";
    private FirebaseAuth auth;
    private DatabaseReference getNumber;
    private String msg;
    private String sub;
    private String smsMessage;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        fingerprintRead();

        cid = getIntent().getStringExtra("CONTACT_ID");
        uid = getIntent().getStringExtra("CURRENT_ID");
        message = findViewById(R.id.txtMessage);
        subject = findViewById(R.id.txtSubject);
        back = findViewById(R.id.btnBack);
        //name = getIntent().getStringExtra("NAME");
        //age = getIntent().getStringExtra("AGE");
        //EmergencyLocation el = (EmergencyLocation) getIntent().getSerializableExtra("EMERGENCY");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //String sql = "INSERT INTO emergency(uID, eName, eAddress, eCity, eState, eCountry, ePostalCode, eSubject, eMessage) VALUES ("+uid+","+name+","+el.getAddress()+","+el.getCity()+","+el.getState()+","+el.getCountry()+","+el.getPostalCode()+","+el.getCountry()+","+el.getAddress()+")";
        //message.setText(sql);
        send = findViewById(R.id.btnSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                String presentDate = df.format(c);
                databaseMessage = FirebaseDatabase.getInstance().getReference().child("Messages").child(uid).child(cid);
                String pushId = databaseMessage.push().getKey();
                msg = message.getText().toString().trim();
                sub = subject.getText().toString().trim();
                String isRead = "unread";

                    name = getIntent().getStringExtra("NAME");
                    age = getIntent().getStringExtra("AGE");
                    EmergencyLocation el = (EmergencyLocation) getIntent().getSerializableExtra("EMERGENCY");
                    getNumber = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                    JSONObject request = new JSONObject();
                    try {
                        //Populate the request parameters
                        request.put(KEY_UID, uid);
                        request.put(KEY_NAME,name);
                        request.put(KEY_MESSAGE, msg);
                        request.put(KEY_SUBJECT, sub);
                        request.put(KEY_ADDRESS, el.getAddress());
                        request.put(KEY_CITY, el.getCity());
                        request.put(KEY_STATE, el.getState());
                        request.put(KEY_COUNTRY, el.getCountry());
                        request.put(KEY_POSTAL, el.getPostalCode());
                        request.put(KEY_STATE, el.getState());
                        request.put(KEY_LATITUDE, el.getLatitude());
                        request.put(KEY_LONGITUDE, el.getLongitude());
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                    smsMessage = "Sender: " + name + "\nUser Location: " + el.getAddress() + "\nSubject: " + sub + "\n\n" + msg + "\nSent on: " + currentDate();
                    requestSmsPermission();
                    finish();
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

    protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                Toast.makeText(getApplicationContext(),
                        cNumber + " " + message, Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // permission already granted run sms send
            sendSms(cNumber, smsMessage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    sendSms(cNumber, smsMessage);
                } else {
                    // permission denied
                }
                return;
            }
        }
    }

    private void sendSms(String phoneNumber, String message){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public String currentDate()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
