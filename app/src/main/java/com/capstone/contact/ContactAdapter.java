package com.capstone.contact;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.user.User;
import com.example.dana.capstone.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private List<Contact> contactsList;
    String conName, conNumber, conRelationship;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, number, relationship;
        public RelativeLayout familyLayout;

        public MyViewHolder(View view) {
            super(view);
            familyLayout = view.findViewById(R.id.familyLayout);
            name = view.findViewById(R.id.con_name);
            number = view.findViewById(R.id.con_number);
            relationship = view.findViewById(R.id.con_relationship);
        }
    }


    public ContactAdapter(List<Contact> contactsList) {
        this.contactsList = contactsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.family_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Contact con = contactsList.get(position);
        holder.name.setText(con.getName());
        holder.number.setText(con.getNumber());
        holder.relationship.setText(con.getRelationship());

        holder.familyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conName = con.getName();
                conNumber = con.getNumber();
                conRelationship = con.getRelationship();

                Intent intent = new Intent(v.getContext(), ContactDetailsActivity.class);
                intent.putExtra("nameKey", conName);
                intent.putExtra("numberKey", conNumber);
                intent.putExtra("relationshipKey", conRelationship);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

}
