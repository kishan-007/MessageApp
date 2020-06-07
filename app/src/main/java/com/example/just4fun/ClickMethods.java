package com.example.just4fun;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public interface ClickMethods {
    public void textClick(int id,DocumentSnapshot snapshot);
    public void longclick(int id, DocumentSnapshot snapshot);
    public void userpicClick(DocumentSnapshot snapshot);

}
