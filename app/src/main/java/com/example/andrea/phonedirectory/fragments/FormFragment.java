package com.example.andrea.phonedirectory.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.andrea.phonedirectory.R;
import com.example.andrea.phonedirectory.model.Contact;

/**
 * Created by andrea on 06/01/17.
 */

public class FormFragment extends Fragment {

    private static final String NAME_PATTERN = "^(\\w+\\s*)+";
    private static final String NUMBER_PATTERN = "^\\+[0-9]+ [0-9]+ [0-9]{6,}";

    private Contact selectedContact;
    private Contact newContact = new Contact();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.form_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final EditText nameEditText = (EditText) view.findViewById(R.id.name);
        final EditText surnameEditText = (EditText) view.findViewById(R.id.surname);
        final EditText numberEditText = (EditText) view.findViewById(R.id.number);

        if(getActivity().getIntent().getExtras()!=null) {
            selectedContact = (Contact) getActivity().getIntent().getExtras().getSerializable("contact");
        }

        // "edit", otherwise "add new"
        if(selectedContact!=null) {
            newContact = selectedContact;
            nameEditText.setText(selectedContact.getName());
            surnameEditText.setText(selectedContact.getSurname());
            numberEditText.setText(selectedContact.getNumber());
        }

        Button editFinish = (Button) view.findViewById(R.id.saveButton);
        editFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String surname = surnameEditText.getText().toString();
                String number = numberEditText.getText().toString();

                if(name.matches(NAME_PATTERN) && surname.matches(NAME_PATTERN) && number.matches(NUMBER_PATTERN)) {
                    newContact.setName(name);
                    newContact.setSurname(surname);
                    newContact.setNumber(number);
                    returnContact(newContact);
                }
                else {
                    String message = "";
                    if(!name.matches(NAME_PATTERN))
                        message += "- Name not correct!\n";
                    if(!surname.matches(NAME_PATTERN))
                        message += "- Surname not correct!\n";
                    if(!number.matches(NUMBER_PATTERN))
                        message += "- Number must be in the format: +, group of digits, space, group of digits, space, group of at least 6 digits (e.g. +39 02 1234567)";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Error")
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void returnContact(Contact newContact) {
        Intent intent = new Intent();
        intent.putExtra("contact", newContact);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
