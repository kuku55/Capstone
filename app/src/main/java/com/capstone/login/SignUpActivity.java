package com.capstone.login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.capstone.emergency.EmergencyActivity;
import com.capstone.json.MySingleton;
import com.capstone.user.User;
import com.example.dana.capstone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity{

    private EditText inputEmail, inputPassword, inputFirstname, inputLastname, inputBirth, inputnumber;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    FirebaseUser firebaseUser;
    private DatabaseReference databaseUser;
    private RadioGroup radioGenderGroup;
    private RadioButton radioGenderButton;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_UID = "uID";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_DOB = "dateofbirth";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_EMPTY = "";
    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get Firebase auth instance
//        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();

        btnSignIn = findViewById(R.id.return_button);
        btnSignUp = findViewById(R.id.btn_reset_pass);
        inputFirstname = findViewById(R.id.firstname);
        inputLastname = findViewById(R.id.lastname);
        inputBirth = findViewById(R.id.dateofbirth);
        radioGenderGroup = findViewById(R.id.rdoGender);
        inputEmail = findViewById(R.id.txtEmail);
        inputPassword = findViewById(R.id.password);
        inputnumber = findViewById(R.id.contactnumber);
        progressBar = findViewById(R.id.progressBar);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        /*
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, ResetPasswordActivity.class));
            }
        });
        */

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inputBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(SignUpActivity.this,
                        android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        inputBirth.setText((month + 1) + "/" +  String.format("%02d", day) + "/" + year);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fname = inputFirstname.getText().toString().trim();
                final String lname = inputLastname.getText().toString().trim();
                if (radioGenderGroup.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(getApplicationContext(), "Select a gender!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    int selectedId = radioGenderGroup.getCheckedRadioButtonId();
                    radioGenderButton = findViewById(selectedId);
                }
                final String gender = radioGenderButton.getText().toString().trim();
                final String dob = inputBirth.getText().toString().trim();
                final String number = inputnumber.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lname)) {
                    Toast.makeText(getApplicationContext(), "Enter last name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(dob)) {
                    Toast.makeText(getApplicationContext(), "Choose Date of Birth!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(getApplicationContext(), "Enter mobile number!", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                databaseUser = FirebaseDatabase.getInstance().getReference();
                                    String id = auth.getInstance().getCurrentUser().getUid();
                                    String defaultImage = "https://firebasestorage.googleapis.com/v0/b/capstone-cc2de.appspot.com/o/profilepics%2Fgeneric-profile.png?alt=media&token=bfda0283-3821-45eb-ac49-4e9c02a41e42";
                                    User user = new User(id, fname, lname, gender, dob, number, email, defaultImage);
                                    databaseUser.child("Users").child(id).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, "User registered!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });


                                JSONObject request = new JSONObject();
                                try {
                                    //Populate the request parameters
                                    request.put(KEY_UID, id);
                                    request.put(KEY_EMAIL, email);
                                    request.put(KEY_PASSWORD, password);
                                    request.put(KEY_FIRST_NAME, fname);
                                    request.put(KEY_LAST_NAME, lname);
                                    request.put(KEY_GENDER, gender);
                                    request.put(KEY_DOB, dob);
                                    request.put(KEY_MOBILE, number);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String register_url = "https://e-ligtas.000webhostapp.com/json/register.php";
                                JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                                        (Request.Method.POST, register_url, request, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                         Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                // Access the RequestQueue through your singleton class.
                                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsArrayRequest);
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(SignUpActivity.this, EmergencyActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
