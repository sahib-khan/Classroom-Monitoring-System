/*
 Name of Module: ProfessorActivity
 Date of creation : 15-04-2018
 Author's Name: Sahib Khan
 Modification History: 15-04-2018 :Created module
                       16-04-2018 : Added View Past Record feature
                        17-04-2018:Added comments
 Synopsis of module: This module is responsible for the boundary class of the professor user profile it identifies what
                       user wants to do and depending on that executes different functions.
                       This module overall performs 4 operations on clicking buttons:
                        1.if start session is clicked by the user it prompts for selecting course and session name
                        and starts a session.
                        2.if grid view is clicked it checks if session is active, if it is active it starts the grid activity for display
                        3.if add course is clicked it prompts for course name and size of classroom
                        4.if view past records is clicked it starts view past record activity
 Different functions supported :
                        onCreate()
                        selectCourse()
                        openDialogSessionPassword()
                        openDialogToAddCourse()
                        applyTextsSessionName()
                        saveData()
                        getSessionName()
                        setSessionName()
                        showDialogForStartSession()
 Global variables accessed/modified by the module : None


*/

//package declaration
package com.example.sahib.student;

//import statements
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;

public class ProfessorMainActivity extends AppCompatActivity implements dialogue.dialogueListner,AddCourseDialog.dialogeListner {
    // variable declaration
    private String sessionName="" ;
    private Button buttonStartSession;
    private Button buttonLogOut;
    private Button buttonAddCourse;
    private Button buttonGridView;
    private Button buttonPastRecord;
    // Database variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCourseDatabaseReference;
    private DatabaseReference mSessionsReference;
    private DatabaseReference mCurrentUserCourseDatabaseReference;
    private DatabaseReference mCourseLectureReference;
    private DatabaseReference mAllCourseReference;

    private FirebaseAuth mFirebaseAuthenticationDatabase;
    // course which is selected for starting session
    private int selectedCourseNo;
    // list of all course which the professor takes
    private ArrayList<CourseDetail> courseListOfProfessor = new ArrayList<>();
    private boolean sessionIsActive;

    @Override
    //function called when activity created responsible for initialization of views
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
        // initialise session active status with false
        sessionIsActive=false;
        // get all references from database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAllCourseReference = mFirebaseDatabase.getReference().child("AllCourses");
        mCourseDatabaseReference = mFirebaseDatabase.getReference().child("Courses").child("Professor");
        mCourseLectureReference= mFirebaseDatabase.getReference().child("Courses").child("Lectures");
        mSessionsReference= mFirebaseDatabase.getReference().child("Sessions");
        mFirebaseAuthenticationDatabase = FirebaseAuth.getInstance();
        // initially if no user is logged in redirect to login screen
        if(mFirebaseAuthenticationDatabase.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, MainActivity.class));
        }
        // get user id of current user. User id is used to uniquely identify a user.
        String user_id=mFirebaseAuthenticationDatabase.getCurrentUser().getUid().toString();
        mCurrentUserCourseDatabaseReference =mCourseDatabaseReference.child(user_id);
        // this listener is added to read all courses taken by the currently logged in professor
        mCurrentUserCourseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot data_snapshot_course_instance, String prev_child_key) {
                // read all courses for current professor from the database
                CourseDetail new_course = data_snapshot_course_instance.getValue(CourseDetail.class);
                courseListOfProfessor.add(new_course);
            }
            // these are default functions for this Listener
            @Override
            public void onChildChanged(DataSnapshot data_snapshot_course_instance, String prev_child_key) {

            }
            @Override
            public void onChildRemoved(DataSnapshot data_snapshot_course_instance){

            }
            @Override
            public void onChildMoved(DataSnapshot data_snapshot_course_instance, String prev_child_key){

            }
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(ProfessorMainActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        // initialize views by id
        buttonStartSession = (Button) findViewById(R.id.button_start_session);
        buttonStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if no session is active then open select course dialog
                if (!sessionIsActive) {
                    selectCourse(view);
                }
                else {
                    // if session is already active and button is pressed then end the session
                    sessionIsActive=false;
                    mCourseLectureReference.child(courseListOfProfessor.get(selectedCourseNo).getCourseName()).child(getSessionName()).setValue(null);
                    buttonStartSession.setText("START SESSION");
                }
            }
        });
        // initialize views by id
        buttonPastRecord = findViewById(R.id.button_past_record);
        buttonPastRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if this button is pressed then ViewPastRecordActivity is started and list of courses of professor is passed to it
                //items stores name of all courses  whereas courseListOfProfessor stores a list of CourseDetail objects.
                String items[] = new String [courseListOfProfessor.size()];
                for (int i = 0; i< courseListOfProfessor.size(); i++){
                    items[i]= courseListOfProfessor.get(i).getCourseName();}
                Bundle courses = new Bundle();
                // pass items to ViewPastRecordActivity
                courses.putStringArray("courses",items);
                Intent intent_view_past_record_activity = new Intent(ProfessorMainActivity.this,ViewPastRecordActivity.class);
                intent_view_past_record_activity.putExtras(courses);
                // start ViewPastRecordActivity
                startActivity(intent_view_past_record_activity);
            }
        });
        // initialize views by id
        buttonLogOut = (Button) findViewById(R.id.button_logout);
        buttonAddCourse = (Button) findViewById(R.id.button_add_course);
        buttonAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogToAddCourse();
            }
        });
        // initialize views by id
        buttonGridView = (Button) findViewById(R.id.button_grid_view);
        buttonGridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if Session is active then open grid View
                if (sessionIsActive) {
                    Intent intent_grid_view = new Intent(ProfessorMainActivity.this, GridActivity.class);
                    CourseDetail selected_course = courseListOfProfessor.get(selectedCourseNo);
                    // pass selected course details (name , row ,columns) to GridView Activity
                    intent_grid_view.putExtra("sessionname", getSessionName());
                    intent_grid_view.putExtra("coursename", selected_course.getCourseName());
                    intent_grid_view.putExtra("row", selected_course.getRow());
                    intent_grid_view.putExtra("col", selected_course.getColumn());
                    // start GridView Activity
                    startActivity(intent_grid_view);
                }
                else {
                    // if grid view is pressed and session is not active then display message
                    Toast.makeText(ProfessorMainActivity.this,"Session is not Active",Toast.LENGTH_SHORT).show();
                }

            }
        });
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SignOut current user
                if (sessionIsActive){
                    sessionIsActive=false;
                    mCourseLectureReference.child(courseListOfProfessor.get(selectedCourseNo).getCourseName()).child(getSessionName()).setValue(null);
                    buttonStartSession.setText("START SESSION");
                }
                mFirebaseAuthenticationDatabase.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(ProfessorMainActivity.this, MainActivity.class));
            }
        });
    }
    // this function calls dialog to let user select course
    public void selectCourse(View view_start_session){
        showDialogForStartSession();
    }
    // this function creates dialog that lets user enter session password
    public void openDialogSessionPassword(){
        // open dialog for entering session key.
        dialogue dialoge_for_session_object = new dialogue();
        // show dialog
        dialoge_for_session_object.show(getSupportFragmentManager(),"dialogue");
    }
    // this function create dialog that lets user enter name of course to be added
    public void openDialogToAddCourse(){
        // open dialog for add course
        AddCourseDialog object_for_add_course = new AddCourseDialog();
        // show dialog
        object_for_add_course.show(getSupportFragmentManager(),"dialog");
    }

    @Override
    // This function is called when the sessionPassword dialog is finished and it saves the password enterd by user
    public void applyTextsSessionName(final String session_name) {
        // initialise session_name with data received from dialog
        setSessionName(session_name);
        Toast.makeText(this, session_name, Toast.LENGTH_SHORT).show();
        // stores selected course name
        final String selected_course_name=courseListOfProfessor.get(selectedCourseNo).getCourseName();
        mFirebaseDatabase.getReference().child(selected_course_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data_snap_shot_session_list) {
                if (data_snap_shot_session_list.hasChild(session_name)){
                    // if there is already a session with same name the show message
                    Toast.makeText(ProfessorMainActivity.this,"Session with same name exists",Toast.LENGTH_SHORT).show();
                }
                else {
                     // create a session in database
                    mCourseLectureReference.child(selected_course_name).child(session_name).setValue(session_name);
                    mSessionsReference.child(selected_course_name).child(session_name).setValue(session_name);
                    // now the session is created set sessionISActive=> true
                    sessionIsActive=true;
                    buttonStartSession.setText("END SESSION");
                }
            }
            //This is a default function created when object was created but of no use
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(ProfessorMainActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    // This function retrieves data entered in AddCourse dialog and saves it to database
    public void saveData(final String course, final int row, final int column) {
        mAllCourseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data_snapshot_course_list) {
                // check if course with same name already exists in database
                if(data_snapshot_course_list.hasChild(course)){
                    Toast.makeText(ProfessorMainActivity.this,"Course Already Exists",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ProfessorMainActivity.this,"Course Created Successfully",Toast.LENGTH_SHORT).show();
                    mCourseDatabaseReference.child(mFirebaseAuthenticationDatabase.getCurrentUser().getUid().toString()).push().setValue(new CourseDetail(course,row,column));
                    mAllCourseReference.child(course).setValue(course);
                }
            }
            //This is a default function created when object was created but of no use
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(ProfessorMainActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    // getter and setter method for sessionName
    public String getSessionName() {
        return sessionName;
    }
    // setter function for session name
    public void setSessionName(String session_name) {
        this.sessionName = session_name;
    }
    // this function created a dialog to let user select the course for session
    public void showDialogForStartSession(){
        String items[] =new String [courseListOfProfessor.size()];
        for (int i = 0; i< courseListOfProfessor.size(); i++){
            items[i]= courseListOfProfessor.get(i).getCourseName();
        }
        // show dialog with course nama as option to start a session
        AlertDialog.Builder alert_dialog_builder_course_selection = new AlertDialog.Builder(
                this);
        alert_dialog_builder_course_selection.setTitle("Courses").setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected_index) {
                selectedCourseNo= selected_index;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected_index) {
                // after selecting the course open dialog to enter session password
                openDialogSessionPassword();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected_index) {
                dialog.cancel();
            }
        });
        //create the dialog
        AlertDialog alertDialog = alert_dialog_builder_course_selection.create();
        // show it
        alertDialog.show();
    }

}
