package com.example.nida.queuemanagement;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission_group.CALENDAR;
import static android.Manifest.permission_group.SMS;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase smsDB;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    final private int PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "You have already granted permission to read SMS !",
                    Toast.LENGTH_SHORT).show();
            try {
                showRecyclerView();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            requestStoragePermission();
        }


    }


    private void showRecyclerView() throws ParseException {

        SMSHelper smsHelper = new SMSHelper(this);
        smsDB = smsHelper.getWritableDatabase();
        smsDB.delete(Contact.SMSEntry.TABLE_NAME, null, null);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        messageAdapter = new MessageAdapter(this,getAllItems());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        prepareMessages();
        recyclerView.setAdapter(messageAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

        messageAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Send Invite")
                        .setMessage("Do you want to send an invite?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                messageAdapter.sendMessage();
                                Toast.makeText(MainActivity.this, "Invite Sent", Toast.LENGTH_SHORT);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.alert_dark_frame)
                        .show();
            }
        });



    }

    private void prepareMessages() throws ParseException {

        Uri uri = Uri.parse("content://sms/inbox");

        /*SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Date interviewDate = formatter.parse(day+mon+year);
        formatter = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = formatter.format(interviewDate);*/

        Cursor c = getContentResolver().query(uri, null, null, null, null);
        startManagingCursor(c);

        // Read the sms data and store it in the list
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {

                ContentValues cv = new ContentValues();
                String name = c.getString(c.getColumnIndexOrThrow("body")).toString();
                String number = c.getString(c.getColumnIndexOrThrow("address")).toString();
                Calendar calender = Calendar.getInstance();
                String DateTime = c.getString(c.getColumnIndexOrThrow("date")).toString();
                Long timestamp = Long.parseLong(DateTime);
                calender.setTimeInMillis(timestamp);
                Date finaldate = calender.getTime();
                String smsDate = DateFormat.getDateInstance().format(finaldate);
                String smsTime = DateFormat.getTimeInstance().format(finaldate);
                String date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date());
                Log.d("System Date", date);
                if(date.compareTo(smsDate)==0) {
                    cv.put(Contact.SMSEntry.COLUMN_NAME, name);
                    cv.put(Contact.SMSEntry.COLUMN_NUMBER, number);
                    cv.put(Contact.SMSEntry.COLUMN_DATE, smsDate);
                    cv.put(Contact.SMSEntry.COLUMN_TIME, smsTime);

                    if (number.trim().length() >= 10) {


                        smsDB.insert(Contact.SMSEntry.TABLE_NAME, null, cv);
                    }
                }
                    c.moveToNext();
                    messageAdapter.swapCursor(getAllItems());

                }
            }
            messageAdapter.notifyDataSetChanged();
            //c.close();
        }

    private void removeItem(long tag) {
            smsDB.delete(Contact.SMSEntry.TABLE_NAME,
                    Contact.SMSEntry._ID + "=" + tag, null);
            messageAdapter.swapCursor(getAllItems());

    }



    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_SMS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to read and send SMSes from your Inbox")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, PERMISSION_CODE);
        }
    }

    private Cursor getAllItems() {
        return smsDB.query(
                Contact.SMSEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Contact.SMSEntry.COLUMN_TIME + " ASC"
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                try {
                    showRecyclerView();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}