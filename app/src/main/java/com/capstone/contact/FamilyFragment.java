package com.capstone.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capstone.RecyclerTouchListener;
import com.capstone.user.User;
import com.example.dana.capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FamilyFragment extends Fragment {
    private List<User> contactList = new ArrayList<>();
    private DatabaseReference databaseContacts;
    private DatabaseReference searchDetails;
    private ContactAdapter mAdapter;
    private String id;

    public FamilyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        id = auth.getCurrentUser().getUid();
        databaseContacts = FirebaseDatabase.getInstance().getReference("Contacts");
        searchDetails = FirebaseDatabase.getInstance().getReference("Contacts").child(id);
    }


    private void showAddContactDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.addcontact_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText addemail = dialogView.findViewById(R.id.con_email);
        final EditText addrelationship = dialogView.findViewById(R.id.con_relationship);
        final Button buttonAdd = dialogView.findViewById(R.id.btn_Add);

        dialogBuilder.setTitle("Add Contact");
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = addemail.getText().toString().trim();
                String relationship = addrelationship.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(relationship)) {
                    addCon(email, relationship);
                    b.dismiss();
                }
            }
        });
    }

    private void addCon(String email, final String relationship) {
        DatabaseReference findByEmail = FirebaseDatabase.getInstance().getReference("Users");
        findByEmail.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String conid = "";
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    conid = datas.getKey();
                }
                if (conid == null || conid.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "email not found!", Toast.LENGTH_SHORT).show();
                } else {
                    Contact con = new Contact(conid, relationship);
                    databaseContacts.child(id).child(conid).setValue(con);
                    Toast.makeText(getActivity().getApplicationContext(), "contact added!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_family, container, false);
        Button addContact = (Button) view.findViewById(R.id.btn_add_contact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddContactDialog();
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mAdapter = new ContactAdapter(contactList);

        //recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                User u = contactList.get(position);
                Intent intent = new Intent(getActivity().getApplicationContext(), ContactDetailsActivity.class);
                intent.putExtra("id", u.getUserId());
                //Toast.makeText(getActivity().getApplicationContext(), u.getUserId(), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        searchDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contactList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final String conid = child.child("contactid").getValue().toString();
                    DatabaseReference con = FirebaseDatabase.getInstance().getReference("Users").child(conid);
                    con.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            User pop = new User();
                            String lname = user.getUserLastName();
                            String fname = user.getUserFirstName();
                            String email = user.getEmail();
                            pop.setUserFirstName(fname);
                            pop.setUserLastName(lname);
                            pop.setEmail(email);
                            pop.setUserId(conid);
                            contactList.add(pop);
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}