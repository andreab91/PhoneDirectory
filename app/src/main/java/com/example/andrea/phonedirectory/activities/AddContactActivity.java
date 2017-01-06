package com.example.andrea.phonedirectory.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.andrea.phonedirectory.R;
import com.example.andrea.phonedirectory.model.Contact;

/**
 * Created by andrea on 02/01/17.
 */
public class AddContactActivity extends AppCompatActivity {

    private final static int PERMISSIONS_REQUEST_READ_CONTACTS = 101;
    private static final int PICK_CONTACT_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_activity);

        // import contact from the phone
        Button importButton = (Button) findViewById(R.id.importButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // permission is granted, access the contacts
                if (ContextCompat.checkSelfPermission(AddContactActivity.this, Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    accessContacts();
                }
                // permission is not granted, ask to the user (Android >= 6)
                else {
                    ActivityCompat.requestPermissions(AddContactActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        });
    }

    private void accessContacts() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        // show user only contacts with phone numbers
        pickContactIntent.setType(Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    // return from the "pickContactIntent"
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();

                // Perform the query on the contact to get the NUMBER column
                String projection[] = new String[]{ Phone.NUMBER, Phone.CONTACT_ID };
                Cursor phoneCursor = getContentResolver().query(contactUri, projection, null, null, null);
                phoneCursor.moveToFirst();

                String number = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
                String contactId = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.CONTACT_ID));

                projection = new String[]{ StructuredName.GIVEN_NAME, StructuredName.FAMILY_NAME };
                String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + StructuredName.CONTACT_ID + " = ?";
                String[] whereNameParams = new String[] { StructuredName.CONTENT_ITEM_TYPE, contactId};

                Cursor nameCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                        projection, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);

                String name = "", surname = "";
                if(nameCursor.moveToNext()) {
                    name = nameCursor.getString(nameCursor.getColumnIndex(StructuredName.GIVEN_NAME));
                    surname = nameCursor.getString(nameCursor.getColumnIndex(StructuredName.FAMILY_NAME));
                }

                phoneCursor.close();
                nameCursor.close();

                // ? check here if the imported Contanct respect the constraint for the new Contacts

                // send back to the main activity the information on the imported contact
                Contact newContact = new Contact(name != null ? name : "", surname != null ? surname : "", number);
                returnContact(newContact);
            }
        }
    }

    private void returnContact(Contact newContact) {
        Intent intent = new Intent();
        intent.putExtra("contact", newContact);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                String permission = permissions[0];
                // user granted the permission, access the contacts
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    accessContacts();
                }
                // user denied the permission
                else {
                    if(Manifest.permission.READ_CONTACTS.equals(permission)) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                            // user did not check "never ask again"
                            // show a dialog to explain why it is needed
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                            builder.setTitle("Import from Phone")
                                    .setMessage("You need to allow access to Contacts to import them")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityCompat.requestPermissions(AddContactActivity.this,
                                                    new String[]{Manifest.permission.READ_CONTACTS},
                                                    PERMISSIONS_REQUEST_READ_CONTACTS);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                        }
                        else {
                            // user checked "never ask again"
                            // show another dialog explaining again the permission, and asking to open the settings
                            AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                            builder.setTitle("Import from Phone")
                                    .setMessage("You need to allow access to Contacts to import them. You can allow it from the Settings page.\n\nDo you want to open the Settings?")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final Intent i = new Intent();
                                            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            i.addCategory(Intent.CATEGORY_DEFAULT);
                                            i.setData(Uri.parse("package:" + getPackageName()));
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                        }
                    }
                }
            }
        }
    }
}
