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
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.AlertActivity;
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

import java.util.Calendar;
import java.util.Date;

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
                String messageID = currentTime + auth.getUid() + "2" + cName; //2 == to
                String uid = auth.getUid();
                String message = "Latitude: " + latValue + " Longitude: " + longValue;
                String subject = "EMERGENCY";
                String receiver = cName; //changeable to cID + cName
                String dateTime = currentTime.toString().trim();

                MessageContact mc = new MessageContact(messageID, uid, subject, message, receiver, dateTime);
                databaseReference.child("Messages").child(messageID).setValue(mc).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ((Activity)context).finish();
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
