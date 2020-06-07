package com.example.just4fun;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class pictureUser extends AppCompatActivity  {
    private static final String TAG = "pictureUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_user);

        ImageView userPicture=findViewById(R.id.userPicture);
        TextView  usernamefield=findViewById(R.id.usernamefield);

        String userPicString=getIntent().getStringExtra("userPic");
        String username=getIntent().getStringExtra("username");

        Glide.with(userPicture).load(userPicString).into(userPicture);
        usernamefield.setText(username);
    }

}
