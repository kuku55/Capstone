package com.capstone.contact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dana.capstone.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FamilyFragment extends Fragment {
    private View FamilyView;
    private ArrayList<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public RelativeLayout familyLayout;
//    private DatabaseReference databaseContacts;
    private ContactAdapter mAdapter;
    private String id;

    private TextView txtContactName;
    private TextView txtContactNumber;
    private TextView txtContactRelationship;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private FirebaseAuth auth = FirebaseAuth.getInstance();


    public FamilyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return inflater.inflate(R.layout.content_family, container, false);
    }

        @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtContactName = getView().findViewById(R.id.con_name);
        txtContactNumber = getView().findViewById(R.id.con_number);
        txtContactRelationship = getView().findViewById(R.id.con_relationship);
        familyLayout = getView().findViewById(R.id.familyLayout);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Contacts");
        databaseReference.keepSynced(true);

        recyclerView = getView().findViewById(R.id.recycler_view_family);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        //only shows fields where id = uid
        databaseReference.orderByChild("id").equalTo(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                    //set value to the recyclerview
                    String id = dataSnapshot1.child("id").getValue(String.class);
                    String name = dataSnapshot1.child("name").getValue(String.class);
                    String number = dataSnapshot1.child("number").getValue(String.class);
                    String relationship = dataSnapshot1.child("relationship").getValue(String.class);
                    Contact contact = new Contact(id, name, number, relationship);

                    contactList.add(contact);
                }

                mAdapter = new ContactAdapter(contactList);
                mAdapter.notifyDataSetChanged();

                //output
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }
}
