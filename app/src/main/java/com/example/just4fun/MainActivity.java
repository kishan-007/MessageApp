package com.example.just4fun;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SnapshotMetadata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.provider.Settings;
import android.text.Html;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener,ClickMethods{
    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    boolean connected=false;
    CoordinatorLayout coordinatorLayout;
    noteRecyclerAdapter noteRecyclerAdapter;
    boolean installed=false;
    boolean showinfofirsttime=true;
    TextView textViewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        coordinatorLayout=findViewById(R.id.coordlayout);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        textViewMessage=findViewById(R.id.message);

        showinfofirsttime=getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("showinfofirsttime",true);
        if(showinfofirsttime)showInfo();
        getSharedPreferences("PREFERENCE",0).edit().putBoolean("showinfofirsttime",false).apply();



        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected()){
                    final EditText editText=new EditText(MainActivity.this);
                    editText.setMaxLines(20);
                    editText.setMinLines(3);
                    editText.setVerticalScrollBarEnabled(true);
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("New Message")
                                .setView(editText)
                                .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String note=editText.getText().toString();
                                        if(note.length()==0)
                                            Snackbar.make(recyclerView,"Please write something",Snackbar.LENGTH_LONG).show();
                                        else
                                            addNote(note);
                                        Log.d(TAG, "onClick: "+note);
                                    }
                                })
                                .setNegativeButton("Cancel",null);
                        AlertDialog dialog=builder.create();
                        dialog.getWindow().setSoftInputMode(5);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8BDC39")));
                        dialog.show();


                }
                else{
                    Snackbar.make(recyclerView,Html.fromHtml("<font color='yellow'>Connection Lost</font>"),Snackbar.LENGTH_LONG)
                            .setAction(Html.fromHtml("<font color='yellow'>Connect</font>"), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openSettings();
                                }
                            })
                            .setDuration(2000)
                            .show();

                }

            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    private void showInfo(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Info :: ")
                .setMessage("---> Click On Pencil To Post A new \tMessage\n" +
                        "---> Click On Date/Time to Edit Your Message & Click + Hold to Delete It. \n" +
                        "---> Click On UNDO To Recover Back Your Recent Deleted Message\n\n" +
                        "***\nYou Are NOT PERMITTED To Edit OR Delete Other's Messages\nThank You");

        AlertDialog dialog=builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFCAE6CD")));
        dialog.show();
    }


    private void addNote(String Value){
        String uid=FirebaseAuth.getInstance().getUid();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        String timeStamp = simpleDateFormat.format(new Date());
        String photourl=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        String username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        additems item=new additems(Value,timeStamp,uid,photourl,username);
        FirebaseFirestore.getInstance()
                        .collection("Messages")
                        .add(item)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(recyclerView,"Can Be Edited Till MidNight",Snackbar.LENGTH_LONG).show();
                                }
                                else
                                    Log.d(TAG, "onComplete: not added");
                            }
                        });
    }

    private  boolean isConnected(){
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Objects.requireNonNull(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState()== NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState()==NetworkInfo.State.CONNECTED)
            connected=true;
        else connected=false;

        return connected;
    }

    private void openSettings(){
        startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
    }


    private  void loginRegister(){
        startActivity(new Intent(this,splashScreen.class));
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if developer selected
        if(isConnected()) {
            if(id==R.id.action_info){
                showInfo();

            }
            if (id == R.id.action_developer) {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setMessage("AJAY ").setTitle("Created By :)")
                        .setIcon(R.drawable.iconaj);
                AlertDialog dialog=builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFCAE6CD")));
                dialog.show();
                return true;
            }
            //if profile selected
            if (id == R.id.action_profile) {
                startActivity(new Intent(this, profile.class));
                return true;

            }

            //if logout selected
            if (id == R.id.action_logout) {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Sign Out").setMessage("Want to Logout")
                        .setPositiveButton("LogOut", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AuthUI.getInstance().signOut(MainActivity.this);
                            }
                        }).setNegativeButton("Cancel", null);
                AlertDialog dialog=builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFCAE6CD")));
                dialog.show();
                return true;

            }
        }
        else{
            Snackbar.make(recyclerView,Html.fromHtml("<font color='yellow'>Connection Lost</font>"),Snackbar.LENGTH_LONG)
                    .setAction(Html.fromHtml("<font color='yellow'>Connect</font>"), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openSettings();
                        }
                    })
                    .setDuration(2000)
                    .show();

        }




        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if(noteRecyclerAdapter!=null)
            noteRecyclerAdapter.stopListening();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser()==null){
            loginRegister();
            return;
        }
        init(firebaseAuth.getUid());


        firebaseAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                Log.d(TAG, "onSuccess: "+getTokenResult.getToken());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e);
            }
        });

    }
//Calling NoteRecyclerAdapter
    private  void init(String user){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query=FirebaseFirestore.getInstance()
                    .collection("Messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<additems> options=new FirestoreRecyclerOptions.Builder<additems>()
                                                    .setQuery(query,additems.class)
                                                    .build();
        noteRecyclerAdapter noteRecyclerAdapter=new noteRecyclerAdapter(options,this);
        recyclerView.setAdapter(noteRecyclerAdapter);
        noteRecyclerAdapter.startListening();
    }


    @Override
    public void  textClick(int id, final DocumentSnapshot snapshot) {
        final EditText editText=new EditText(this);
        editText.setMaxLines(20);
        editText.setMinLines(3);
        editText.setVerticalScrollBarEnabled(true);
        final String item=snapshot.get("item").toString();
        editText.setText(item);
        if(isConnected()) {
            if(FirebaseAuth.getInstance().getUid().toString().equals(snapshot.get("uid"))){
                String timestamp=(snapshot.get("timestamp").toString());
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyHHmmss");
                final String currentTime=simpleDateFormat.format(new Date());
                String oldDate=timestamp.substring(0,8);
                int oldDateVal=Integer.parseInt(oldDate);
                String curDate=currentTime.substring(0,8);
                int curDateVal=Integer.parseInt(curDate);
                if(curDateVal==oldDateVal){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Edit")
                            .setView(editText)
                            .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String newitem = editText.getText().toString();
                                    if(newitem.length()>0)
                                        editNote(newitem, snapshot,currentTime);
                                }
                            })
                            .setNegativeButton("Cancel", null);
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setSoftInputMode(5);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8BDC39")));
                    dialog.show();
                }
                else
                    Toast.makeText(this, "Can't be Edited Any More", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission Denied !! ", Toast.LENGTH_SHORT).show();

            }


        }
        else{
            Snackbar.make(recyclerView,Html.fromHtml("<font color='yellow'>Connection Lost</font>"),Snackbar.LENGTH_LONG)
                    .setAction(Html.fromHtml("<font color='yellow'>Connect</font>"), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openSettings();
                        }
                    })
                    .setDuration(2000)
                    .show();

        }
    }
    private  void editNote(String item,DocumentSnapshot snapshot,String currentTimeStamp){
        if(!snapshot.get("item").toString().equals(item)){
            DocumentReference documentReference=snapshot.getReference();
            documentReference.update("item",item,"timestamp",currentTimeStamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Snackbar.make(recyclerView,"Updated",Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void longclick(int id, final DocumentSnapshot snapshot) {
        if(isConnected()){
            if(FirebaseAuth.getInstance().getUid().toString().equals(snapshot.get("uid"))){
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(Html.fromHtml("<font color='#888b7'>Delete Message</font>"))
                    .setMessage(Html.fromHtml("<font color='#888b7'>Want to Delete</font>"))
                    .setPositiveButton(Html.fromHtml("<font color='#888b7'>Delete</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                               DocumentReference documentReference=snapshot.getReference();
                               documentReference.delete()
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Snackbar.make(recyclerView,"Deleted",Snackbar.LENGTH_LONG)
                                                            .setAction("Undo", new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    undoAddNote(snapshot);
                                                                }
                                                            })
                                                            .setDuration(3000)
                                                            .show();
                                                }
                                           }
                                       });

                        }
                    })
                    .setNegativeButton(Html.fromHtml("<font color='#888b7'>Cancel</font>"),null);
                AlertDialog dialog=builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFCAE6CD")));
                dialog.show();
            }
            return;
        }

    }




    private void undoAddNote(DocumentSnapshot snapshot){
        String photourl=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
        String username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        additems item=new additems(snapshot.get("item").toString(),snapshot.get("timestamp").toString(),snapshot.get("uid").toString(),photourl,username);
        FirebaseFirestore.getInstance().collection("Messages")
                .add(item)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(MainActivity.this, "Recovered", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void userpicClick(DocumentSnapshot snapshot) {
            String userpicString =snapshot.get("photourl").toString();
            String username=snapshot.get("username").toString();
            Intent intent=new Intent(this,pictureUser.class);
            intent.putExtra("userPic",userpicString);
            intent.putExtra("username",username);
            startActivity(intent);
    }
}
