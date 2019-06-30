package com.capstone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.AlertActivity;
import com.example.dana.capstone.R;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    public FingerprintHandler(Context context){
        this.context = context;
    }

    public void startAuth (FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this,null);
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
        ((Activity)context).finish();
        Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT).show();
    }

    private void update(String s, boolean b) {

//        TextView lblMessage = ((Activity)context).findViewById(R.id.lblMessage);

        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
//        if(b == false){
//            lblMessage.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
//        }

    }
}
