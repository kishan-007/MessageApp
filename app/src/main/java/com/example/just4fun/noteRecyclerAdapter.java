package com.example.just4fun;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class noteRecyclerAdapter extends FirestoreRecyclerAdapter<additems, noteRecyclerAdapter.NoteViewholder> {
    private static final String TAG = "noteRecyclerAdapter";
    ClickMethods clickMethods;
    public noteRecyclerAdapter(@NonNull FirestoreRecyclerOptions<additems> options,ClickMethods clickMethods) {
        super(options);
        this.clickMethods=clickMethods;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewholder holder, int position, @NonNull additems task) {
        String textViewString="";
        if(task.getItem().length()>27){

            String originalMessage=task.getItem();
            if(originalMessage.indexOf(" ")==-1)textViewString=originalMessage;
            else{
                int count=0;
                int i=0;
                String []origMessArr=originalMessage.split(" ");
                for (String mess:origMessArr){
                    textViewString+=mess;
                    textViewString+=" ";
                }
            }
        }
        else textViewString=task.getItem();
        String textViewTimestamp="";
        String timestamp=task.getTimestamp();
        String timestampDd=timestamp.substring(0,2);
        String timestampMm=timestamp.substring(2,4);
        String timestampYr=timestamp.substring(4,8);
        String timestampHr=timestamp.substring(8,10);
        String timestampMin=timestamp.substring(10,12);
        textViewTimestamp+=timestampDd+'/'+timestampMm+'/'+timestampYr;
        int timestamphr=Integer.parseInt(timestampHr);
        if(timestamphr>=12 && timestamphr<=23){
            if(timestamphr>12)
                timestamphr-=12;
            textViewTimestamp+="          @ "+String.valueOf(timestamphr)+':'+timestampMin+" PM";
        }
        else{
            if(timestamphr==00)
                timestamphr=12;
            textViewTimestamp+="          @ "+String.valueOf(timestamphr)+':'+timestampMin+" AM";
        }
        holder.textViewTimestamp.setText(textViewTimestamp);
        holder.textViewMessage.setText(textViewString);
        Glide.with(holder.imageView).load(task.getPhotourl()).into(holder.imageView);
    }

    @NonNull
    @Override
    public NoteViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.itemlist,parent,false);

        return new NoteViewholder(view);

    }

    class NoteViewholder extends RecyclerView.ViewHolder{
        TextView textViewMessage,textViewTimestamp;
        ImageView imageView;
        public NoteViewholder(@NonNull final View itemView) {
            super(itemView);
            textViewMessage=itemView.findViewById(R.id.message);
            imageView=itemView.findViewById(R.id.image);
            textViewTimestamp=itemView.findViewById(R.id.timeStamp);

            textViewTimestamp.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    DocumentSnapshot snapshot=getSnapshots().getSnapshot(getAdapterPosition());
                    clickMethods.longclick(getAdapterPosition(),snapshot);
                    return false;
                }
            });

            textViewTimestamp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentSnapshot snapshot=getSnapshots().getSnapshot(getAdapterPosition());
                        clickMethods.textClick(getAdapterPosition(),snapshot);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentSnapshot snapshot=getSnapshots().getSnapshot(getAdapterPosition());
                    clickMethods.userpicClick(snapshot);
                }
            });


        }
    }
}
