/*
 Name of Module: MainActivity
 Date of creation : 15-04-2018
 Author's Name: Sahib Khan
 Modification History: 15-04-2018 :Created module
                       16-04-2018 : Added Exception Handling
                       17-04-2018:Added comments
 Synopsis of module: This module is responsible for login of the user it identifies user is already logged in
                        and in case of new login it validates whether login is successful or not .
 Different functions supported :
                        onCreate()
                        userLogin() function is called when details has been taken to validate the user details
                        onClick()
 Global variables accessed/modified by the module : none


*/

//package name
package com.example.sahib.student;

//import statements
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
// import packages for firebase
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //defining variables for different views
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignUp;

    //defining database reference variables
    private FirebaseAuth mFirebaseAuthenticationDatabase;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;

    //defining dialog objects
    private ProgressDialog progressDialog;

    @Override
    // this function is executed when activity is created. It initialises the variables
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting firebase auth object . It handles all task regarding user authentication
        mFirebaseAuthenticationDatabase = FirebaseAuth.getInstance();

        /*If the current user reference is not null
        means user is already logged in*/
        if(mFirebaseAuthenticationDatabase.getCurrentUser() != null){
            //initialization of databases references
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            // get user id of current user from database. User id is unique for every user.
            String user_id = mFirebaseAuthenticationDatabase.getCurrentUser().getUid().toString();
            mUserDatabaseReference = mFirebaseDatabase.getReference().child("user").child(user_id);
            mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                //identifying whether user is student or professor and depending on that starting new activity
                public void onDataChange(DataSnapshot data_snapshot_user_database) {
                    // if data_snapshot is not null
                    if (data_snapshot_user_database.getValue()!=null){
                        try {// Exception handling for any unknown event
                            User user = data_snapshot_user_database.getValue(User.class);
                            if(!user.isPermission()){ // if the current user is not professor
                                // start student main activity
                                finish();
                                Intent intent_student_main_activity =new Intent(MainActivity.this, StudentMainActivity.class);
                                intent_student_main_activity.putExtra("rollNo",Integer.toString(user.getRoll()));
                                intent_student_main_activity.putExtra("name",user.getName());
                                startActivity(intent_student_main_activity);
                            }
                            else {
                                // if current user is professor the start professor main activity
                                finish();
                                startActivity(new Intent(MainActivity.this, ProfessorMainActivity.class));
                            }
                        }
                        catch (Exception exception){
                            // Print error if an exception occurs due to undesired event
                            exception.printStackTrace();
                        }
                    }
                }
                // this is default function
                @Override
                public void onCancelled(DatabaseError database_error) {
                    // Display the error message to user which caused error
                    Toast.makeText(MainActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        buttonSignIn = (Button) findViewById(R.id.button_sign_in);
        textViewSignUp = (TextView) findViewById(R.id.text_view_sign_up);
        //progress dialog, it is shown when log in process is going on
        progressDialog = new ProgressDialog(this);

        //attaching click listener
        // the onClick function for these button are defined below at line 199
        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
    }

    //This method verifies user credentials and if they are correct perform user log in
    private void userLogin(){
        // get email and password from text views and also trim them to remove any whitespaces that occur at end or at start.
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();


        //checking if email field is empty
        if(TextUtils.isEmpty(email)){
            // if empty show error message and return
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }
        //checking if password field is empty
        if(TextUtils.isEmpty(password)){
            //if empty show error message and return
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        /*if the email and password are not empty
        displaying a progress dialog*/

        progressDialog.setMessage("Logging you in Please Wait...");
        progressDialog.show();

        //logging in the user
        mFirebaseAuthenticationDatabase.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task_sign_in) {
                        progressDialog.dismiss();
                        //if the sign in task is successfully identify
                        // user profile whether professor or student and start next activity
                        if(task_sign_in.isSuccessful()){
                            mFirebaseDatabase = FirebaseDatabase.getInstance();
                            // stores user id of current user
                            String user_id = mFirebaseAuthenticationDatabase.getCurrentUser().getUid().toString();
                            mUserDatabaseReference = mFirebaseDatabase.getReference().child("user").child(user_id);
                            // read the database and retrieve user type based on permissions of that user
                            mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue()!=null){
                                        try {
                                            User user = dataSnapshot.getValue(User.class);
                                            // if permission ==true start professor activity else start student activity
                                            if(!user.isPermission()){
                                                finish();
                                                Intent intent_student_main_activity =new Intent(MainActivity.this, StudentMainActivity.class);
                                                intent_student_main_activity.putExtra("rollNo",Integer.toString(user.getRoll()));
                                                intent_student_main_activity.putExtra("name",user.getName());
                                                startActivity(intent_student_main_activity);
                                            }
                                            else { // if current user is professor the start professor main activity
                                                finish();
                                                startActivity(new Intent(MainActivity.this, ProfessorMainActivity.class));
                                            }
                                        }
                                        catch (Exception exception){
                                            // Print error if an exception occurs due to undesired event
                                            exception.printStackTrace();
                                        }
                                    }
                                }
                                //default function crated on object creation but of no use
                                @Override
                                public void onCancelled(DatabaseError database_error) {
                                    // Display the error message to user which caused error
                                    Toast.makeText(MainActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            // if sign in failed then show concerned error
                            Toast.makeText(MainActivity.this,((FirebaseAuthException) task_sign_in.getException()).getErrorCode(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    //identifies which button is clicked
    public void onClick(View clicked_view) {
        if(clicked_view == buttonSignIn){
            // open user login
            userLogin();
        }
        if(clicked_view == textViewSignUp){
            // finish current activity and start the signUp activity
            finish();
            startActivity(new Intent(this, SignupActivity.class));
        }
    }
}