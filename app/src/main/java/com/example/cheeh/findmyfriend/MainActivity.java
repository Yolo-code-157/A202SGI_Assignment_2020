package com.example.cheeh.findmyfriend;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.cheeh.findmyfriend.Model.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    DatabaseReference user_information; //declare referencing value of data from database
    private static final int MY_REQUEST_CODE = 7117; //any number also can
    List<AuthUI.IdpConfig> providers; //declare Firebase-ready layout authentication options in arraylist

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing Paper Database with immediate read, write, and update to Firebase
        Paper.init(this);

        //Initialize Firebase child: User Information
        user_information = FirebaseDatabase.getInstance().getReference(com.example.cheeh.findmyfriend.Utils.Common.USER_INFORMATION);

        //Initialize providers (email or google)
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        //Permission manager requesting permission on the app
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION) //with access the location services
                .withListener(new PermissionListener() {
                    @Override //while permission is accepted from user
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        showSignInOption(); //show login type, see line 83
                    }

                    @Override //while permission is denied from user
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "You must accept permission to use app", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    //After permission is granted successfully
    private void showSignInOption() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder() //using pre build UI from Firebase AuthUI
                .setAvailableProviders(providers) //show available list of login type
                .build(), MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //when activity called request code is same with value declared
        if(requestCode == MY_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            //when activity received result code is shown OK
            if(resultCode == RESULT_OK){
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                //Check if current user exist on database
                user_information.orderByKey()
                        .equalTo(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue() == null){ //if current user not exist in database
                                    if(!dataSnapshot.child(firebaseUser.getUid()).exists()){ //if key uid is not exist
                                        com.example.cheeh.findmyfriend.Utils.Common.loggedUser = new User(firebaseUser.getUid(),
                                                firebaseUser.getEmail());

                                        //create new info to the database
                                        user_information.child(com.example.cheeh.findmyfriend.Utils.Common.loggedUser
                                                .getUid())
                                                .setValue(com.example.cheeh.findmyfriend.Utils.Common.loggedUser);
                                    }
                                } else{ //if user is exist in database, just get the user info from database
                                    com.example.cheeh.findmyfriend.Utils
                                            .Common.loggedUser = dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);
                                }

                                //Save UID to storage to update location from background
                                Paper.book().write(com.example.cheeh.findmyfriend.Utils.Common.USER_UID_SAVE_KEY,
                                        com.example.cheeh.findmyfriend.Utils.Common.loggedUser.getUid());
                                updateToken(firebaseUser); //update user's token, see line 143
                                setupUI(); //start intent activity, see line 137
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }
    }

    private void setupUI() {
        //Navigate item
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }

    private void updateToken(final FirebaseUser firebaseUser) {
        //At the database and get database child: Tokens
        final DatabaseReference tokens = FirebaseDatabase.getInstance()
                .getReference(com.example.cheeh.findmyfriend.Utils.Common.TOKENS);

        //When get token is successful
        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    tokens.child(firebaseUser.getUid())
                            .setValue(instanceIdResult.getToken());
                }
            }).addOnFailureListener(new OnFailureListener() { //or else token gets unsuccessful
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
