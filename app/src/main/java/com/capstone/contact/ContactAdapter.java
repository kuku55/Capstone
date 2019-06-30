package com.capstone.contact;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.user.User;
import com.example.dana.capstone.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private List<Contact> contactsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, number, relationship;

        public MyViewHolder(View view) {
            super(view);
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
        Contact con = contactsList.get(position);
        holder.name.setText(con.getName());
        holder.number.setText(con.getNumber());
        holder.relationship.setText(con.getRelationship());
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

}
