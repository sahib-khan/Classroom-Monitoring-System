/*
 Name of Module: StudentMainActivity
 Date of creation : 15-04-2018
 Author's Name: Abhinav Mishra
 Modification History: 15-04-2018 :Created module
                       16-04-2018 : Added Register course button
                        17-04-2018:Added comments
 Synopsis of module: This module is responsible for the boundary class of the student user profile it identifies what
                       user wants to do and depending on that executes different functions.
                       This module overall performs 4 operations on clicking buttons:
                        1.if join session is clicked it prompts for selecting course and session name if session is active it starts
                            the ScanActivity and then marks the students position
                        2.if register course is clicked it prompts for course name and if course exists it registers the student

 Different functions supported :
                        onCreate()
                        openDialoge()
                        openRegisterCourse()
                        selectCourse()
                        onActivityResult()
                        applyTexts()
                        applyTextRegisterCourse()
                        showDialogForJoinSession()

 Global variables accessed/modified by the module : None


*/

//package name
package com.example.sahib.student;

//import statement
import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;



public class StudentMainActivity extends AppCompatActivity implements dialogue.dialogueListner,RegisterCourseDialog.dialogueListner{
    //variable to store QRvalue
    private int QRvalue;
    //variable for storing session name
    private String sessionName;

    //variable decelaration for different views
    private Button buttonJoinSession;
    private Button buttonLogOut;
    private Button buttonRegisterCourse;

    //variable for database references
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRegisteredCourseListDatabaseReference;
    private DatabaseReference mCourseDatabaseReference;
    private DatabaseReference  mAttendanceDatabase;
    private DatabaseReference mCourseSessionReference;
    private DatabaseReference mCurrentUserCourseDatabaseReference;
    private DatabaseReference mRegisteredStudentDatabase;
    private DatabaseReference mAllExitisngCourseReference;
    private DatabaseReference mSessionDatabaseReference =null;
    private FirebaseAuth mFirebaseAuth;

    //variable for identifying dialoge
    public static final int REQUEST_CODE = 100;
    public  static final int PERMISSION_REQUEST=200;

    //variable for storing student's roll no and name
    private String myRollno;
    private String myName;
    //variable for storing new course to be registered in
    private String newCourseName;
    //varible for storing index of selected course list
    private int selectedCourseNo;
    //array of registered course of student
    private ArrayList<String> courseListOfStudent = new ArrayList<>();

    @Override
    //function called when activity is created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialization of layout ,rollno and database references
        setContentView(R.layout.activity_student_main);
        Bundle extras_student_details = getIntent().getExtras();
        if (extras_student_details != null) {
            myRollno = extras_student_details.getString("rollNo");
            myName = extras_student_details.getString("name");
        }
        // get references of all database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRegisteredStudentDatabase = mFirebaseDatabase.getReference().child("RegisteredStudent");
        mAllExitisngCourseReference = mFirebaseDatabase.getReference().child("AllCourses");
        mCourseSessionReference = mFirebaseDatabase.getReference().child("Courses").child("Lectures");
        mCourseDatabaseReference=mFirebaseDatabase.getReference().child("Courses").child("Student");
        mAttendanceDatabase= mFirebaseDatabase.getReference().child("Attendance");
        mFirebaseAuth = FirebaseAuth.getInstance();
        //if no user exists
        if(mFirebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, MainActivity.class));
        }
        //databases references
        mCurrentUserCourseDatabaseReference = mCourseDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid().toString());
        //store new course in the course list of student when new is added
        mCurrentUserCourseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot data_snapshot_course_instance, String prev_child_key) {
                String new_course = data_snapshot_course_instance.getValue(String.class);
                courseListOfStudent.add(new_course);
            }
            //default functions created when object is created but is of no use
            @Override
            public void onChildChanged(DataSnapshot data_snapshot_course_instance, String prev_child_key) {

            }
            @Override
            public void onChildRemoved(DataSnapshot data_snapshot_course_instance) {

            }
            @Override
            public void onChildMoved(DataSnapshot data_snapshot_course_instance, String prev_child_key) {

            }
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(StudentMainActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        //defining buttons and onclick listeners
        buttonRegisterCourse = (Button) findViewById(R.id.button_register_course);
        buttonJoinSession = (Button) findViewById(R.id.button_start_session);
        buttonLogOut = (Button) findViewById(R.id.button_logout);

        buttonRegisterCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button_register_course) {
                //call openRegisterCourse function
                openRegisterCourse();
            }
        });
        buttonJoinSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call selectCourse function
                selectCourse(view);
            }
        });

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(StudentMainActivity.this, MainActivity.class));
            }
        });
        //checking camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST) ;
        }
    }
    //calls dialoge for session password
    public void openDialogeForSessionName(){
        dialogue dialoge_for_session_object = new dialogue();
        dialoge_for_session_object.show(getSupportFragmentManager(),"dialogue");
    }
    //calls dialoge responsible for registration of courses
    public void openRegisterCourse(){
        RegisterCourseDialog dialoge_for_register_object = new RegisterCourseDialog();
        dialoge_for_register_object.show(getSupportFragmentManager(),"dialog");
    }

    //dialouge for selecting course for joining session
    public void selectCourse(View view_join_session){
        showDialogForJoinSession();
    }

    @Override
    //function identifies the dialogue completed and depending upon store it in database
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE){// checks if finished activity is scanned activity or not
            if (resultCode == RESULT_OK){// check if activity finished successfully
                if (data != null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    // retrieve QR value
                    QRvalue = Integer.parseInt(barcode.displayValue);
                    Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show();
                    if(mSessionDatabaseReference != null) {
                        // push student position in database
                        mSessionDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid().toString())
                                .setValue(new Position(QRvalue / 100, QRvalue % 100, Integer.parseInt(myRollno),myName));
                    }
                    else {
                        //give toast to show error
                        Toast.makeText(this, "Session is not initialised", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    @Override
    //responsible for storing value of password entered and if correct start the scanner
    public void applyTextsSessionName(String password) {
        sessionName = password;
        mRegisteredCourseListDatabaseReference = mFirebaseDatabase.getReference().child(courseListOfStudent.get(selectedCourseNo));
        mCourseSessionReference.child(courseListOfStudent.get(selectedCourseNo)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data_snapshot_session_list) {
                if (!sessionName.equals("") && data_snapshot_session_list.hasChild(sessionName)){
                    // if course and session name are correct then start scan activity
                    mSessionDatabaseReference = mRegisteredCourseListDatabaseReference.child(sessionName);
                    mAttendanceDatabase.child(courseListOfStudent.get(selectedCourseNo)).child(sessionName).child(myRollno).setValue(myRollno+"_"+myName);
                    Intent intent_scan_activity = new Intent(StudentMainActivity.this, ScanActivity.class);
                    startActivityForResult(intent_scan_activity,REQUEST_CODE);
                }
                else {
                    //else toast for error
                    Toast.makeText(StudentMainActivity.this,"Wrong Session Password",Toast.LENGTH_SHORT).show();
                }
            }
            //default function crated on object creation but of no use
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(StudentMainActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    //function stores registers the student in the course if correct information is given
    @Override
    public void applyTextRegisterCourse(final String course_name) {

        mAllExitisngCourseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data_snapshot_course_list) {
                if (data_snapshot_course_list.hasChild(course_name)){
                    // registers student in the course if such a course exists
                    newCourseName= course_name;
                    // add student in database of course
                    mCourseDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid().toString()).push().setValue(newCourseName);
                    mRegisteredStudentDatabase.child(newCourseName).child(myRollno).setValue(myRollno+"_"+myName);
                    Toast.makeText(StudentMainActivity.this,"Registered in "+ course_name,Toast.LENGTH_SHORT).show();
                }
                else {
                    //show message if not completed
                    Toast.makeText(StudentMainActivity.this,"No such Course exists",Toast.LENGTH_SHORT).show();
                }
            }
            //default function crated on object creation but of no use
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(StudentMainActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();                }
        });

    }

    //show dialog for joining session
    public void showDialogForJoinSession(){
        //items is array containing course list of student
        String items[] =new String [courseListOfStudent.size()];
        for (int i = 0; i< courseListOfStudent.size(); i++){
            items[i]= courseListOfStudent.get(i);
        }
        // build dialog to display course option to choose from
        AlertDialog.Builder alert_dialog_builder_course_selection = new AlertDialog.Builder(this);
        alert_dialog_builder_course_selection.setTitle("Courses").setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            //when selected identify the course index selected
            public void onClick(DialogInterface dialog, int selected_index) {
                selectedCourseNo= selected_index;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            //on positive response allow session password taking
            public void onClick(DialogInterface dialog, int selected_index) {
                openDialogeForSessionName();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            //on negative response end the dialog
            public void onClick(DialogInterface dialog, int selected_index) {
                dialog.cancel();
            }
        });
        AlertDialog alert_dialog_course_selection = alert_dialog_builder_course_selection.create();

        // show it
        alert_dialog_course_selection.show();
    }
}
