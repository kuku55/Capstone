package com.capstone.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.contact.ContactActivity;
import com.capstone.emergency.EmergencyActivity;
import com.capstone.login.MainActivity;
import com.example.dana.capstone.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserDetailsActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String id;
    private DatabaseReference databaseUser;
    private TextView inputFullName, inputAge, inputGender, inputNumber, inputEmail;
    private final static int GALLERY_REQ = 1;
    private CircularImageView imageView;
    private Uri uri = null;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        setTitle("My Profile");
        dl = findViewById(R.id.activity_user_details);
        t = new ActionBarDrawerToggle(this, dl, R.string.Close, R.string.Open);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.emergency:
                        startActivity(new Intent(UserDetailsActivity.this, EmergencyActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        dl.closeDrawers();
                        break;
                    case R.id.contacts:
                        startActivity(new Intent(UserDetailsActivity.this, ContactActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        dl.closeDrawers();
                        break;
                    case R.id.profile:
                        Toast.makeText(UserDetailsActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        dl.closeDrawers();
                        break;
                    case R.id.logout:
                        signOut();
                        break;
                }
                return true;
            }
        });
        inputFullName = findViewById(R.id.txtFullName);
        inputAge = findViewById(R.id.txtAge);
        inputGender = findViewById(R.id.txtGender);
        inputNumber = findViewById(R.id.txtContactNumber);
        inputEmail = findViewById(R.id.txtEMail);
        imageView = findViewById(R.id.imageProfile);
        auth = FirebaseAuth.getInstance();
        id = auth.getCurrentUser().getUid();
        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(id);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(UserDetailsActivity.this, MainActivity.class));
                    finish();
                }
            }
        };
//        inputFullName.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showUpdateLNameDialog(id);
//                return true;
//            }
//        });
        inputAge.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showUpdateFNameDialog(id);
                return true;
            }
        });
        inputGender.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showUpdateFNameDialog(id);
                return true;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        final CircularImageView image = (CircularImageView) findViewById(R.id.imageProfile);
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        ((AppBarLayout) findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int min_height = ViewCompat.getMinimumHeight(collapsingToolbar) * 2;
                float scale = (float) (min_height + verticalOffset) / min_height;
                image.setScaleX(scale >= 0 ? scale : 0);
                image.setScaleY(scale >= 0 ? scale : 0);
            }
        });

    }

    private void showUpdateFNameDialog(final String userId) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.updatefname_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editField);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.btnUpdate);

        dialogBuilder.setTitle("Edit First Name");
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    updateFirstName(userId, name);
                    b.dismiss();
                }
            }
        });
    }

//    private void showUpdateLNameDialog(final String userId) {
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.updatelname_dialog, null);
//        dialogBuilder.setView(dialogView);
//
//        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editField);
//        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.btnUpdate);
//
//        dialogBuilder.setTitle("Edit Last Name");
//        final AlertDialog b = dialogBuilder.create();
//        b.show();
//
//
//        buttonUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = editTextName.getText().toString().trim();
//                if (!TextUtils.isEmpty(name)) {
//                    updateLastName(userId, name);
//                    b.dismiss();
//                }
//            }
//        });
//    }

    private boolean updateFirstName(String id, String name) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(id).child("userFirstName");

        dR.setValue(name);
        Toast.makeText(getApplicationContext(), "First Name Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean updateLastName(String id, String name) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(id).child("userLastName");

        dR.setValue(name);
        Toast.makeText(getApplicationContext(), "Last Name Updated", Toast.LENGTH_LONG).show();
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                inputFullName.setText(user.getUserLastName() + ", " + user.getUserFirstName());
                inputGender.setText(getString(R.string.update_gender, user.getGender()));
                inputNumber.setText(getString(R.string.update_number, user.getMobileNumber()));
                inputEmail.setText(getString(R.string.update_email, auth.getCurrentUser().getEmail()));
                Picasso.with(UserDetailsActivity.this).load(user.getImage()).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    // image from gallery result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK){

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                uri = result.getUri();
                imageView.setImageURI(uri);
                uploadImageToFirebaseStorage();
            }else {
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception err = result.getError();
                }
            }
        }
    }

    public String calcAge (String dateBirth)
    {
        Date today = Calendar.getInstance().getTime();
        Date birth = null;
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            birth = formatter.parse(dateBirth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long msDiff = today.getTime() - birth.getTime();
        int p = (int)(msDiff / 1000 / 60 / 60 / 24 / 365.25);
        return String.valueOf(p);
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = storageReference.child("profilepics/" + System.currentTimeMillis());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                final Uri downloadUrl = urlTask.getResult();
                DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Users").child(id).child("image");
                dR.setValue(downloadUrl.toString());
                Toast.makeText(getApplicationContext(), "Succesfully Uploaded", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showImageChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQ);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
    public void signOut() {
        auth.signOut();
    }
}
