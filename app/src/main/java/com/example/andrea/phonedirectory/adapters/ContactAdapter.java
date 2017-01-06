package com.example.andrea.phonedirectory.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.andrea.phonedirectory.R;
import com.example.andrea.phonedirectory.model.Contact;

import java.util.ArrayList;

/**
 * Created by andrea on 31/12/16.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private OnItemClickListener itemClickListener;
    private ArrayList<Contact> contactList;

    public ContactAdapter(ArrayList<Contact> contactList) {
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_content, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Contact contact = contactList.get(position);

        holder.name.setText(contact.getName() + " " + contact.getSurname());
        holder.number.setText(contact.getNumber());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    /* ViewHolder class */
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView number;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.contactName);
            number = (TextView) view.findViewById(R.id.contactNumber);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(itemClickListener!=null) {
                itemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
