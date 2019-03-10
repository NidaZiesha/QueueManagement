package com.example.nida.queuemanagement;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Nida on 2/22/2019.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener mListener;

    public int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName, contactNumber, sentTime, sentDate;

        public MyViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.contactName);
            contactNumber = (TextView) itemView.findViewById(R.id.contactNumber);
            sentTime = (TextView)itemView.findViewById(R.id.sentTime);
            sentDate = (TextView)itemView.findViewById(R.id.sentDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  if(listener != null){
                      int position = getAdapterPosition();
                      if(position != RecyclerView.NO_POSITION){
                          listener.onItemClick(position);
                      }

                  }
                }
            });
        }
    }


    public MessageAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    public void sendMessage(){

        int position = getPosition();
        String number = mCursor.getString(mCursor.getColumnIndex(Contact.SMSEntry.COLUMN_NUMBER));
        String name = mCursor.getString(mCursor.getColumnIndex(Contact.SMSEntry.COLUMN_NAME));
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, "Hey, "+ name+" kindly come for the interview.", null, null );


    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_row, parent, false);
        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(Contact.SMSEntry.COLUMN_NAME));
        String number = mCursor.getString(mCursor.getColumnIndex(Contact.SMSEntry.COLUMN_NUMBER));
        String smsDate = mCursor.getString(mCursor.getColumnIndex(Contact.SMSEntry.COLUMN_DATE));
        String smsTime = mCursor.getString(mCursor.getColumnIndex(Contact.SMSEntry.COLUMN_TIME));

        long id = mCursor.getLong(mCursor.getColumnIndex(Contact.SMSEntry._ID));


        holder.contactName.setText(name);
        holder.contactNumber.setText(number);
        holder.sentDate.setText(smsDate);
        holder.sentTime.setText(smsTime);
        holder.itemView.setTag(id);
    }


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
