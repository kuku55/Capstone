package com.capstone.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class UnreadFragment extends Fragment {
    private MessageAdapter mAdapter;
    private DatabaseReference databaseMessages;
    private FirebaseAuth auth;
    private String id;
    private RecyclerView recyclerView;
    private List<Inbox> unreadList = new ArrayList<>();

    public UnreadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        id = auth.getCurrentUser().getUid();
        databaseMessages = FirebaseDatabase.getInstance().getReference("Messages");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unread, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        mAdapter = new MessageAdapter(unreadList);

        //recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Inbox i = unreadList.get(position);
                Intent intent = new Intent(getActivity().getApplicationContext(), ViewMessageActivity.class);
                intent.putExtra("MESSAGE_ID", i.getMessage().getMessage_id());
                intent.putExtra("SENDER_ID", i.getMessage().getUser_id());
                intent.putExtra("RECEIVER_ID", i.getMessage().getContact_id());
                intent.putExtra("SENDER", i.getSender());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        databaseMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Inbox> data = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    final String senderId = child.getKey();
                    DatabaseReference sender = FirebaseDatabase.getInstance().getReference("Messages").child(senderId);
                    sender.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot child : dataSnapshot.getChildren())
                            {
                                final String receiverId = child.getKey();
                                if(id.equals(receiverId))
                                {
                                    DatabaseReference inbox = FirebaseDatabase.getInstance().getReference("Messages").child(senderId).child(receiverId);
                                    inbox.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(DataSnapshot child : dataSnapshot.getChildren())
                                            {
                                                final Message m = child.getValue(Message.class);
                                                DatabaseReference con = FirebaseDatabase.getInstance().getReference("Users").child(senderId);
                                                con.addValueEventListener(new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        unreadList.clear();
                                                        if(m.getIsRead().equals("unread")) {
                                                            User user = dataSnapshot.getValue(User.class);
                                                            Inbox i = new Inbox();
                                                            String lname = user.getUserLastName();
                                                            String fname = user.getUserFirstName();
                                                            i.setSender(lname + ", " + fname);
                                                            i.setMessage(m);
                                                            data.clear();
                                                            data.add(i);
                                                            unreadList.add(data.get(0));
                                                            mAdapter.notifyDataSetChanged();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
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
