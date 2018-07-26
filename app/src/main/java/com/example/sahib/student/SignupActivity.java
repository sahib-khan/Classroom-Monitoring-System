/*
 Name of Module: SignupActivity
 Date of creation : 15-04-2018
 Author's Name: Abhinav Mishra
 Modification History: 15-04-2018 :Created module
                       16-04-2018 : Added User Details dialog box
                        17-04-2018:Added comments
 Synopsis of module: This module is responsible for registration of new user identifying the user profile and registering other
                        information about the user.
 Different functions supported :
                        registerUser() takes in all the input from the user checks if username is unique or not and identifies
                        the user profile if professor password is correct its a professor
                        userDetails() it prompts for user name and user roll no or professor id
                        onClick()
                        applyTextUserDetails()
                        showPasswordPrompt()
 Global variables accessed/modified by the module : None


*/

//package name
package com.example.sahib.student;

// import required packages
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, SignupDialog.signUpDialogListener {

    //defining view objects
    private boolean isProfessor;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignUp;
    private TextView textViewSignIn;
    private RadioGroup radioGroup;

    private ProgressDialog progressDialog;


    //these are used ito reference the database.
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserProfileDatabaseReference;
    // firebase auth gives access to authentication database which stores all registered user for app
    private FirebaseAuth mFirebaseAuthenticationDatabase;
    private DatabaseReference mUserDatabaseReference;

    @Override
    //function called when activity created responsible for initialization of views
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        isProfessor=false;
        //initializing firebase auth object
        mFirebaseAuthenticationDatabase = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserProfileDatabaseReference = mFirebaseDatabase.getReference().child("user");
        if(mFirebaseAuthenticationDatabase.getCurrentUser() != null){
            // if some user is already logged in
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            // checks the user type by referencing user permission from database
            String user_id = mFirebaseAuthenticationDatabase.getCurrentUser().getUid().toString();
            mUserDatabaseReference = mFirebaseDatabase.getReference().child("user").child(user_id);
            mUserDatabaseReference.child("permission").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot data_snapshot_user) {
                    // if data_snapshot is not empty execute below code
                    if (data_snapshot_user.getValue()!=null){
                        try {                       // Exception handling for unknown event
                            if(data_snapshot_user.getValue().toString().equals("false")){
                                // if permission is false then open student profile activity
                                finish();
                                startActivity(new Intent(SignupActivity.this, StudentMainActivity.class));
                            }
                            else {
                                // if permission is true then open Professor profile activity
                                finish();
                                startActivity(new Intent(SignupActivity.this, ProfessorMainActivity.class));
                            }
                        }
                        catch (Exception exception){
                            exception.printStackTrace();
                        }
                    }
                }
                // these are default functions for this Listener but of no use
                @Override
                public void onCancelled(DatabaseError database_error) {
                    // Display the error message to user which caused error
                    Toast.makeText(SignupActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        //initializing views
        radioGroup = findViewById(R.id.radio_group);
        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);
        textViewSignIn = (TextView) findViewById(R.id.text_view_sign_in);
        buttonSignUp = (Button) findViewById(R.id.button_sign_up);
        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        buttonSignUp.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);
    }
    // this function get credentials from user and then creates a new account
    private void registerUser(){

        //getting email and password from edit texts
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        mFirebaseAuthenticationDatabase.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task_sign_up) {
                        // if sign up is successful
                        if(task_sign_up.isSuccessful()){
                            //get user details
                            getUserDetails();
                        }else{
                            //display unsuccessful message here
                            Toast.makeText(SignupActivity.this,((FirebaseAuthException) task_sign_up.getException()).getErrorCode(),Toast.LENGTH_SHORT).show();
                        }
                        //dismiss progress dialog after sign up is complete
                        progressDialog.dismiss();
                    }
                });
    }
    // This function is called after after user enters the credentials for new account.It opens a dialog to get user name and rollNo/profID
    public void getUserDetails(){
        //Create a new signUp dialog object
        SignupDialog sign_up_dialog_object = new SignupDialog();
        sign_up_dialog_object.setPermission(isProfessor);
        // show dialog for entering user details
        sign_up_dialog_object.show(getSupportFragmentManager(),"dialogue");
    }

    // function to identify new user is a professor or student
    @Override
    public void onClick(View clicked_view) {
        if(clicked_view == buttonSignUp){
            // set isProfessor false initially
            isProfessor=false;
            int selected_id = radioGroup.getCheckedRadioButtonId();
            if (selected_id ==R.id.radio_professor) {
                // if professor radio button is selected then show password prompt (to register as professor a password has to be entered)
                showPasswordPrompt();
            }
            else if (selected_id ==R.id.radio_student){
                // if student radio button is selected register this user
                registerUser();
            }
            else{
                // if none radio button is selected then show error message
                Toast.makeText(SignupActivity.this,"Please Select One User Type",Toast.LENGTH_LONG).show();
            }
        }
        if(clicked_view == textViewSignIn){
            //open login activity when user taps on the already registered
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    // This function is called when user details dialog is finished and stores the name and rollNoProfId of user
    public void applyTextUserDetails(String name, String roll) {
        try {                                           // Exception Handling for any unknown event
            String user_id= mFirebaseAuthenticationDatabase.getCurrentUser().getUid().toString();
            // store user details in database
            mUserProfileDatabaseReference.child(user_id).setValue(new User(isProfessor, Integer.parseInt(roll),name));
            if (isProfessor) {
                // if user is professor open professor activity
                startActivity(new Intent(this, ProfessorMainActivity.class));
            }
            else {
                // if user is student open student activity and pass the rollno
                Intent intent_student_main_activity = new Intent(this, StudentMainActivity.class);
                intent_student_main_activity.putExtra("rollNo", roll);
                intent_student_main_activity.putExtra("name", name);
                startActivity(intent_student_main_activity);
            }
        }
        // if roll number enter is not integer
        catch (NumberFormatException exception) {
            final FirebaseUser user = mFirebaseAuthenticationDatabase.getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential("user@example.com", "password1234");
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task_reauthenticate) {
                    // if exception occurs then stop the registration process and delete the created id from database
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task_delete_user) {
                            if (task_delete_user.isSuccessful()) {
                            }
                        }
                    });
                }
            });
            // Show error if number format exception occur
            Toast.makeText(this,"Please Enter Valid RollNo",Toast.LENGTH_SHORT).show();
        }
    }
    // This function opens a dialog to get password if user wants to register as professor
    public void showPasswordPrompt(){
        //instantiate the dialog with password_prompt xml file
        LayoutInflater layout_inflater = LayoutInflater.from(SignupActivity.this);
        View password_prompt_view = layout_inflater.inflate(R.layout.password_prompt, null);

        AlertDialog.Builder alert_dialog_builder_for_password = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alert_dialog_builder_for_password.setView(password_prompt_view);

        final EditText user_password = (EditText) password_prompt_view.findViewById(R.id.prof_password);

        // set dialog message
        alert_dialog_builder_for_password
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int dialog_id) {
                                // get user input and set it to result
                                // edit text
                                // check if password enterd is correct
                                if (user_password.getText().toString().equals("123456")){
                                    isProfessor=true;
                                    // if password is correct then register this user
                                    registerUser();
                                }
                                else {
                                    Toast.makeText(SignupActivity.this,"Wrong Password",Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                // when negative response close dialog
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int dialog_id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alert_dialog_for_password = alert_dialog_builder_for_password.create();
        // show it
        alert_dialog_for_password.show();
    }
}