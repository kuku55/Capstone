package com.capstone.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dana.capstone.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<Inbox> messageList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sender, subject, date;

        public MyViewHolder(View view) {
            super(view);
            sender = view.findViewById(R.id.sender);
            subject = view.findViewById(R.id.subject);
            date = view.findViewById(R.id.date);
        }
    }


    public MessageAdapter(List<Inbox> messageList) {
        this.messageList = messageList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Inbox mess = messageList.get(position);
        holder.sender.setText(mess.getSender());
        holder.subject.setText(mess.getMessage().getSubject());
        holder.date.setText(mess.getMessage().getDate_sent());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

}
