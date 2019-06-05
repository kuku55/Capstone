package com.capstone.emergency;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.contact.ContactActivity;
import com.capstone.location.EmergencyLocation;
import com.capstone.login.MainActivity;
import com.capstone.message.MessageActivity;
import com.capstone.user.User;
import com.capstone.user.UserDetailsActivity;
import com.example.dana.capstone.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmergencyActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    //maps problem https://stackoverflow.com/questions/29441384/fusedlocationapi-getlastlocation-always-null

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
//    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
//    private static final int DEFAULT_ZOOM = 15;
//    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
//    private boolean mLocationPermissionGranted;
//    private LocationRequest locationRequest;
//    private LocationCallback locationCallback;
//
//    // The geographical location where the device is currently located. That is, the last-known
//    // location retrieved by the Fused Location Provider.
//    private Location mLastKnownLocation;
//    private GoogleApiClient mGoogleApiClient;

    private TextView name, age, gender, loc;
    String address="address";
    String city="city";
    String state="state";
    String country="country";
    String postalCode="postalCode";
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference databaseUser;
    private CircularImageView profilepic;
    private Button btnPolice;

    private boolean isContinue = false;
    private boolean isGPS = false;

    private Location mLastLocation;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        dl = findViewById(R.id.activity_emergency);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);

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
                    case R.id.contacts:
                        startActivity(new Intent(EmergencyActivity.this, ContactActivity.class));
                        dl.closeDrawers();
                        break;
                    case R.id.profile:
                        startActivity(new Intent(EmergencyActivity.this, UserDetailsActivity.class));
                        dl.closeDrawers();
                        break;
                    case R.id.logout:
                        signOut();
                        break;
                }
                return true;
            }
        });
        auth = FirebaseAuth.getInstance();
        final String uid = auth.getCurrentUser().getUid();


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(EmergencyActivity.this, MainActivity.class));
                    finish();
                }
            }
        };
        databaseUser = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
        name = findViewById(R.id.lblName);
        age = findViewById(R.id.lblAge);
        gender =  findViewById(R.id.lblGender);
        loc =  findViewById(R.id.lblAddress);
        profilepic = findViewById(R.id.imageView2);
        btnPolice = findViewById(R.id.btnPolice);

        btnPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MessageActivity.class);
                String lat = String.valueOf(wayLatitude);
                String lon = String.valueOf(wayLongitude);
                EmergencyLocation el = new EmergencyLocation(address, city, state, country, postalCode,lat,lon);
                intent.putExtra("CURRENT_ID", uid);
                intent.putExtra("CONTACT_ID", "Police");
                intent.putExtra("NAME", name.getText());
                intent.putExtra("AGE", age.getText());
                intent.putExtra("EMERGENCY", el);
                startActivity(intent);
            }
        });
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(getString(R.string.full_name, user.getUserFirstName(), user.getUserLastName()));
                age.setText(calcAge(user.getDob()));
                gender.setText(user.getGender());
                String profile = user.getImage();
                Picasso.with(EmergencyActivity.this).load(profile).into(profilepic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //code not compatible with older phones
    public String calcAge (String dateBirth)
    {
        //LocalDate today = LocalDate.now();
        Date today = Calendar.getInstance().getTime();
        Date birth = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            birth = formatter.parse(dateBirth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //LocalDate bday =  birth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //Period p = Period.between(bday, today);
        long msDiff = today.getTime() - birth.getTime();
        int p = (int)(msDiff / 1000 / 60 / 60 / 24 / 365.25);
        return String.valueOf(p);
    }

    public void onMapReady(GoogleMap map) {
        mMap = map;

//        // Prompt the user for permission.
//        getLocationPermission();
//        // Turn on the My Location layer and the related control on the map.
//        updateLocationUI();
//
//        // Get the current location of the device and set the position of the map.
//        getDeviceLocation();
    }
//    private void getDeviceLocation() {
//        /*
//         * Get the best and most recent location of the device, which may be null in rare
//         * cases when a location is not available.
//         */
//        try {
//            if (mLocationPermissionGranted) {
//                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            // Set the map's camera position to the current location of the device.
//                            mLastKnownLocation = task.getResult();
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                    new LatLng(mLastKnownLocation.getLatitude(),
//                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                            wayLatitude = mLastKnownLocation.getLatitude();
//                            wayLongitude = mLastKnownLocation.getLongitude();
//                            getAddress();
//                        } else {
//                            mMap.moveCamera(CameraUpdateFactory
//                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
//                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
//            }
//        } catch (SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }

//    private void getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//
//    private void updateLocationUI() {
//        if (mMap == null) {
//            return;
//        }
//        try {
//            if (mLocationPermissionGranted) {
//                mMap.setMyLocationEnabled(true);
//                mMap.getUiSettings().setMyLocationButtonEnabled(true);
//            } else {
//                mMap.setMyLocationEnabled(false);
//                mMap.getUiSettings().setMyLocationButtonEnabled(false);
//                mLastKnownLocation = null;
//                getLocationPermission();
//            }
//        } catch (SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }

    public Address getAddress(double latitude,double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public void getAddress()
    {

        Address locationAddress=getAddress(wayLatitude,wayLongitude);

        if(locationAddress!=null)
        {
            address = locationAddress.getAddressLine(0);
            city = locationAddress.getLocality();
            state = locationAddress.getAdminArea();
            country = locationAddress.getCountryName();
            postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;

                loc.setText(currentLocation);
            }

        }

    }

}
