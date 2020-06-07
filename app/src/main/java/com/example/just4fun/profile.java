package com.example.just4fun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class profile extends AppCompatActivity {
    ImageView userpic;
    TextView userdispname,hiusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userpic=findViewById(R.id.userphoto);
        userdispname=findViewById(R.id.userdispname);
        hiusername=findViewById(R.id.hiuser);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(userpic);
        String name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        assert name != null;
        String[] fname=name.split(" ");
        String dispFirstname=fname[0];
        if(dispFirstname.length()>10){
            userdispname.setText(dispFirstname);
            hiusername.setTextSize(15);
            userdispname.setTextSize(20);

        }
        else
            userdispname.setText(dispFirstname);

        getWindow().setLayout((int)(width),(int)(height*0.3));
    }
}
