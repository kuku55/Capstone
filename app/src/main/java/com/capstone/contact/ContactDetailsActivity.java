package com.capstone.contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.login.MainActivity;
import com.capstone.message.Message;
import com.capstone.message.MessageActivity;
import com.capstone.message.MessageContactActivity;
import com.capstone.user.User;
import com.example.dana.capstone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    //private String cid;
    private FirebaseAuth auth;
    private FloatingActionButton send, call;
    private ImageButton btnDeleteContact;

    private String cID, cName, cNumber, cRelationship;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        btnDeleteContact = findViewById(R.id.btnDeleteContact);
        name = findViewById(R.id.txtName);
        relationship = findViewById(R.id.txtRelationship);
        mobile = findViewById(R.id.txtNumber);
//        email = findViewById(R.id.txtEmail);
//        profileImage = findViewById(R.id.imageView2);
        auth = FirebaseAuth.getInstance();
//        final String uid = auth.getCurrentUser().getUid();
//        cid = getIntent().getExtras().get("id").toString();
        databaseUser = FirebaseDatabase.getInstance().getReference("Contacts");
        send = findViewById(R.id.btnSendMessage);
        call = findViewById(R.id.btnCall);
//        send.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), MessageActivity.class);
//                intent.putExtra("CURRENT_ID", uid);
//                intent.putExtra("CONTACT_ID", cid);
//                startActivity(intent);
//                finish();
//            }
//        });

        //gets data from Contact
        cID = getIntent().getStringExtra("idKey");
        cName = getIntent().getStringExtra("nameKey");
        cNumber = getIntent().getStringExtra("numberKey");
        cRelationship = getIntent().getStringExtra("relationshipKey");

        name.setText("Name: " + cName);
        mobile.setText("Number: " + cNumber);
        relationship.setText("Relationship: " + cRelationship);
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        databaseUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Contact con = dataSnapshot.getValue(Contact.class);
//                relationship.setText(getString(R.string.update_relationship, con.getRelationship()));
//                DatabaseReference user = FirebaseDatabase.getInstance().getReference("Users").child(cid);
//                user.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        User user = dataSnapshot.getValue(User.class);
//                        name.setText(getString(R.string.update_name, user.getUserLastName(), user.getUserFirstName()));
//                        mobile.setText(getString(R.string.update_number, user.getMobileNumber()));
//                        email.setText(getString(R.string.update_email, user.getEmail()));
//                        String profile = user.getImage();
//                        Picasso.with(ContactDetailsActivity.this).load(profile).into(profileImage);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//
//                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    //passes value to Message Activity
    public void onClickMessage(View v){
        //startActivity(new Intent(ContactDetailsActivity.this, MessageActivity.class));
        Intent intent = new Intent(this, MessageContactActivity.class);
        intent.putExtra("nameKey", cName);
        intent.putExtra("numberKey", cNumber);
        intent.putExtra("relationshipKey", cRelationship);
        startActivity(intent);
    }

    //call
    public void onClickCall(View view){
        Intent intent = new Intent(Intent.ACTION_CALL);
        String number = "tel:" + cNumber;
        intent.setData(Uri.parse(number));

        if (ContextCompat.checkSelfPermission(ContactDetailsActivity.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ContactDetailsActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            //You already have permission
            try {
                startActivity(intent);
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }

//        if (ActivityCompat.checkSelfPermission(ContactDetailsActivity.this,
//                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        else
//        {
//            Toast.makeText(this, "Error in making call.", Toast.LENGTH_LONG);
//        }
    }

    public void onClickDeleteContact(View v){
        databaseUser = FirebaseDatabase.getInstance().getReference("Contacts").child(cID + cName);
        databaseUser.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ContactDetailsActivity.this, "Successfully Deleted Contact", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else{
                    Toast.makeText(ContactDetailsActivity.this, "There's an error in deleting the contact\n" +
                                    task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
