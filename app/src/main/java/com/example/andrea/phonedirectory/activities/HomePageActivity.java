package com.example.andrea.phonedirectory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andrea.phonedirectory.R;
import com.example.andrea.phonedirectory.adapters.ContactAdapter;
import com.example.andrea.phonedirectory.helpers.ContactDbHelper;
import com.example.andrea.phonedirectory.model.Contact;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {

    private final static int ADD_NEW_CONTACT_REQUEST = 100;
    private final static int EDIT_CONTACT_REQUEST = 101;

    // full list with all the contacts in the DB
    private ArrayList<Contact> savedContacts;
    // list with only the contacts displayed after a search
    private ArrayList<Contact> searchedContacts;

    private ContactAdapter contactAdapter;
    private int selectedPosition;

    private RecyclerView recyclerView;
    private Button addNewButton;
    private EditText searchText;

    ContactDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_activity);

        dbHelper = new ContactDbHelper(this);
        searchedContacts = new ArrayList<>();

        // Button with the link to the "Add new" page
        addNewButton = (Button) findViewById(R.id.addNewButton);
        addNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, AddContactActivity.class);
                startActivityForResult(intent, ADD_NEW_CONTACT_REQUEST);
            }
        });

        savedContacts = dbHelper.readAllContact();
        searchedContacts.addAll(savedContacts);
        contactAdapter = new ContactAdapter(searchedContacts);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        // set the focus on the recyclerView (avoid to show the keyboard for the searchbox on opening)
        recyclerView.requestFocus();

        contactAdapter.setOnItemClickListener(new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedPosition = position;

                Bundle bundle = new Bundle();
                bundle.putSerializable("contact", searchedContacts.get(position));

                Intent intent = new Intent(HomePageActivity.this, EditContactActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_CONTACT_REQUEST);
            }
        });

        searchText = (EditText) findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence!=null) {
                    searchedContacts.clear();
                    contactAdapter.notifyDataSetChanged();
                    String searched = charSequence.toString().toLowerCase();
                    for (Contact c : savedContacts) {
                        if (c.contains(searched)) {
                            searchedContacts.add(c);
                            contactAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_NEW_CONTACT_REQUEST) {
            if(resultCode == RESULT_OK) {
                Contact c = (Contact) data.getSerializableExtra("contact");
                Toast.makeText(this, "Contact added correctly", Toast.LENGTH_SHORT).show();

                // save the new Contact in the DB and get the created ID
                c.setId(dbHelper.insertContact(c));

                savedContacts.add(c);

                // update the array of showed contact only if the Contact satisfies the search
                if (c.contains(searchText.getText().toString())) {
                    searchedContacts.add(c);
                }
                contactAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == EDIT_CONTACT_REQUEST) {
            if(resultCode == RESULT_OK) {
                Contact c = (Contact) data.getSerializableExtra("contact");
                Toast.makeText(this, "Contact edited correctly", Toast.LENGTH_SHORT).show();

                // update the Contact in the DB
                dbHelper.updateContact(c);

                // update the array with all the contacts (use ID to avoid problems if not showed after a search)
                for(Contact cont: savedContacts) {
                    if(cont.getId().equals(c.getId())){
                        savedContacts.get(savedContacts.indexOf(cont)).setName(c.getName());
                        savedContacts.get(savedContacts.indexOf(cont)).setSurname(c.getSurname());
                        savedContacts.get(savedContacts.indexOf(cont)).setNumber(c.getNumber());
                    }
                }

                // update the array of showed contacts, if the edited contact satisfies the search
                if(c.contains(searchText.getText().toString())) {
                    searchedContacts.set(selectedPosition, c);
                }
                // remove the contact from the array of showed contact, if does not satisfy the seach
                else {
                    searchedContacts.remove(selectedPosition);
                }
                contactAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
