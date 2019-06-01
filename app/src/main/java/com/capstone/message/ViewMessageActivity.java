package com.capstone.message;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dana.capstone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewMessageActivity extends AppCompatActivity {
    String message_id, sender, sender_id, receiver_id;
    TextView from, subject;
    Button back;
    EditText message;
    private DatabaseReference databaseMessages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);
        message_id = getIntent().getStringExtra("MESSAGE_ID");
        sender = getIntent().getStringExtra("SENDER");
        sender_id = getIntent().getStringExtra("SENDER_ID");
        receiver_id = getIntent().getStringExtra("RECEIVER_ID");
        databaseMessages =  FirebaseDatabase.getInstance().getReference("Messages").child(sender_id).child(receiver_id).child(message_id);
        from = findViewById(R.id.txtFrom);
        subject = findViewById(R.id.txtSubject);
        message = findViewById(R.id.txtMessage);
        back = findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        databaseMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                from.setText(getString(R.string.sentBy, sender));
                databaseMessages.child("isRead").setValue("read");
                Message m = dataSnapshot.getValue(Message.class);
                subject.setText(getString(R.string.subject, m.getSubject()));
                message.setText(m.getMessage());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
