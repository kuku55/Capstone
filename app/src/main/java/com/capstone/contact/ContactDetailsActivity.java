package com.capstone.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.message.MessageActivity;
import com.capstone.user.User;
import com.example.dana.capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ContactDetailsActivity extends AppCompatActivity {
    private TextView name, relationship, mobile, email;
    private DatabaseReference databaseUser;
    private ImageView profileImage;
    private String cid;
    private FirebaseAuth auth;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        name = findViewById(R.id.txtName);
        relationship = findViewById(R.id.txtRelationship);
        mobile = findViewById(R.id.txtNumber);
        email = findViewById(R.id.txtEmail);
        profileImage = findViewById(R.id.imageView2);
        auth = FirebaseAuth.getInstance();
        final String uid = auth.getCurrentUser().getUid();
        cid = getIntent().getExtras().get("id").toString();
        databaseUser = FirebaseDatabase.getInstance().getReference("Contacts").child(uid).child(cid);
        send = findViewById(R.id.btnSendMessage);
        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MessageActivity.class);
                intent.putExtra("CURRENT_ID", uid);
                intent.putExtra("CONTACT_ID", cid);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contact con = dataSnapshot.getValue(Contact.class);
                relationship.setText(getString(R.string.update_relationship, con.getRelationship()));
                DatabaseReference user = FirebaseDatabase.getInstance().getReference("Users").child(cid);
                user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        name.setText(getString(R.string.update_name, user.getUserLastName(), user.getUserFirstName()));
                        mobile.setText(getString(R.string.update_number, user.getMobileNumber()));
                        email.setText(getString(R.string.update_email, user.getEmail()));
                        String profile = user.getImage();
                        Picasso.with(ContactDetailsActivity.this).load(profile).into(profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
