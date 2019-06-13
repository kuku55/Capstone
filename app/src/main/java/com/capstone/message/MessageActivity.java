package com.capstone.message;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.capstone.contact.Contact;
import com.capstone.json.MySingleton;
import com.capstone.location.EmergencyLocation;
import com.capstone.user.User;
import com.example.dana.capstone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo;
    String messager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
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
                databaseMessage = FirebaseDatabase.getInstance().getReference("Messages").child(uid).child(cid);
                String pushId = databaseMessage.push().getKey();
                final String msg = message.getText().toString().trim();
                String sub = subject.getText().toString().trim();
                String isRead = "unread";

                if(cid.equals("Police")){
                    name = getIntent().getStringExtra("NAME");
                    age = getIntent().getStringExtra("AGE");
                    EmergencyLocation el = (EmergencyLocation) getIntent().getSerializableExtra("EMERGENCY");
                    subject.setText(el.getAddress());

                    JSONObject request = new JSONObject();
                    try {
                        //Populate the request parameters
                        request.put(KEY_UID, uid);
                        request.put(KEY_NAME,name);
                        request.put(KEY_MESSAGE, message.getText());
                        request.put(KEY_SUBJECT, subject.getText());
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
                    finish();
                }else{
                    Message mg = new Message(pushId, uid, cid, msg, presentDate, sub, isRead);
                    databaseMessage.child(pushId).setValue(mg);
                    DatabaseReference getNumber =  FirebaseDatabase.getInstance().getReference("Users").child(cid);
                    getNumber.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                            String num = user.getMobileNumber();
                            sendSMSMessage(num, msg);
                            Toast.makeText(getBaseContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                            finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                }
            }
        });


    }
    protected void sendSMSMessage(String num, String msg) {
        phoneNo = num;
        messager = msg;

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, messager, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "failed sending SMS.", Toast.LENGTH_LONG).show();
            }
        }

    }
}
