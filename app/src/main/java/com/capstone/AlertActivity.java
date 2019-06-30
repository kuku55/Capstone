package com.capstone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.capstone.emergency.Emergency;
import com.capstone.emergency.EmergencyActivity;
import com.example.dana.capstone.R;

public class AlertActivity extends AppCompatActivity {

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlertActivity.this, EmergencyActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
    }
}
