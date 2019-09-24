package com.capstone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.location.Location;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.capstone.AlertActivity;
import com.capstone.json.MySingleton;
import com.capstone.location.EmergencyLocation;
import com.capstone.message.MessageContact;
import com.capstone.message.MessageContactActivity;
import com.example.dana.capstone.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private FirebaseAuth auth;
    private String cName, cNumber, cRelationship;
    private DatabaseReference databaseReference;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public FingerprintHandler(Context context) {
        this.context = context;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        this.update("There was an error.\n" + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {

        this.update("Authentication failed.", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //get current location
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                String latValue = String.valueOf(location.getLatitude());
                String longValue = String.valueOf(location.getLongitude());

                String KEY_UID = "uID";
                String KEY_NAME = "name";
                String KEY_ADDRESS = "address";
                String KEY_CITY = "city";
                String KEY_STATE = "state";
                String KEY_COUNTRY = "country";
                String KEY_POSTAL = "postalCode";
                String KEY_LATITUDE = "latitude";
                String KEY_LONGITUDE = "longitude";
                String KEY_SUBJECT = "subject";
                String KEY_MESSAGE = "message";

                auth = FirebaseAuth.getInstance();
                databaseReference = FirebaseDatabase.getInstance().getReference();

                //gets values from previous activity
                cName = ((Activity)context).getIntent().getStringExtra("nameKey");
                cNumber = ((Activity)context).getIntent().getStringExtra("numberKey");
                cRelationship = ((Activity)context).getIntent().getStringExtra("relationshipKey");

                //receiver will be Police if Police button is used
                if(cName == null){
                    cName = "Police";
                }

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                String presentDate = df.format(currentTime);
                String messageID = currentTime + auth.getUid() + "2" + cName; //2 == to
                String uid = auth.getUid();
                String message = "Latitude: " + latValue + " Longitude: " + longValue;
                String subject = "EMERGENCY";
                String receiver = cName; //changeable to cID + cName
                String isRead = "unread";

                if(cName == "Police"){
                    EmergencyLocation el = (EmergencyLocation) ((Activity)context).getIntent().getSerializableExtra("EMERGENCY");

                    JSONObject request = new JSONObject();
                    try {
                        //Populate the request parameters
                        request.put(KEY_UID, uid);
                        request.put(KEY_NAME, cName);
                        request.put(KEY_MESSAGE, message);
                        request.put(KEY_SUBJECT, subject);
//                        request.put(KEY_ADDRESS, el.getAddress());
//                        request.put(KEY_CITY, el.getCity());
//                        request.put(KEY_STATE, el.getState());
//                        request.put(KEY_COUNTRY, el.getCountry());
//                        request.put(KEY_POSTAL, el.getPostalCode());
//                        request.put(KEY_STATE, el.getState());
//                        request.put(KEY_LATITUDE, el.getLatitude());
//                        request.put(KEY_LONGITUDE, el.getLongitude());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String register_url = "https://e-ligtas.000webhostapp.com/json/emergency.php";
                    JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                            (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(context.getApplicationContext(), "Emergency Sent", Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                    // Access the RequestQueue through your singleton class.
                    MySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsArrayRequest);
                }

                MessageContact mc = new MessageContact(messageID, uid, subject, message, receiver, presentDate);
                databaseReference.child("Messages").child(messageID).setValue(mc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        ((Activity)context).finish();
                        Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "There's an error in sending the message, please try again.\n" + e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void update(String s, boolean b) {

//        TextView lblMessage = ((Activity)context).findViewById(R.id.lblMessage);

        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
//        if(b == false){
//            lblMessage.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
//        }

    }
}
