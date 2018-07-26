/*
 Name of Module: GridActivity
 Date of creation : 16-04-2018
 Author's Name: Shivam Kumar
 Modification History: 16-04-2018 :Created module
                       17-04-2018 : Added refresh every 10 second feature
                        18-04-2018:Added comments
 Synopsis of module: This module is responsible for creating grid view of the class (2D representation)
                       along with the students position and augmented state.
 Different functions supported :
                        onCreate() for initialization
                        callCanvas() is used for creating the 2D view
                        onTouchEvent() identifies which sitting position information is wanted
 Global variables accessed/modified by the module : none


*/

//package name
package com.example.sahib.student;


//import statements
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class GridActivity extends AppCompatActivity {
    //database reference to primary node in database
    private FirebaseDatabase mFirebaseDatabase;
    //database reference to session of the selected course
    private DatabaseReference mCourseSessionDatabaseReference;
    //String variable for storing name of new session hosted
    private String nameOfSession ="";
    //ArrayList storing position of student with attributes row,column and roll no
    ArrayList<Position> studentPositionList = new ArrayList<>();
    //ArrayList storing random states of each present student
    ArrayList<Integer> studentStateList = new ArrayList<>();
    //PixelGrid object declaration
    private PixelGridView pixelGrid;
    //variable for storing width of  each cell representing a student
    private int studentCellWidth;
    //variable for storing height of  each cell representing a student
    private int studentCellHeight;
    //variable for storing current course name
    private String courseName;
    //variable for storing no of rows in class
    private int noOfRowsInClass;
    //variable for storing no of rows in class
    private int noOfColumnsInClass;
    //object declaration for random value generation
    final Random randamValueGeneratorObject = new Random();
    //array storing the roll no of student at respective position
    private String mappedStudentPositionArray[];
    private String mappedStudentNameArray[];

    @Override
    //function responsible for initialization called when activity is created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //object declaration that changes the state every 10 secs
        new CountDownTimer(300000, 1000) {

            public void onTick(long millisecond_until_finished) {
                Long remaining_seconds = millisecond_until_finished /1000;
                if (remaining_seconds % 10 == 0 && studentPositionList.size()>0) {
                    for (int i = 0; i< studentStateList.size(); i++){
                        // generate random values for student state at 10 second
                        studentStateList.set(i, randamValueGeneratorObject.nextInt(10));
                    }
                    //call function for redrawing the 2D class view
                    callCanvas(studentPositionList, studentStateList);
                }
            }
            public void onFinish() {

            }
        }.start();

        //database reference declaration
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //getting extra variables with intent
        Bundle extras_value_variables = getIntent().getExtras();
        if (extras_value_variables != null) {
            // retrieve data passed from previous activity
            nameOfSession = extras_value_variables.getString("sessionname");
            courseName = extras_value_variables.getString("coursename");
            noOfRowsInClass = extras_value_variables.getInt("row");
            noOfColumnsInClass = extras_value_variables.getInt("col");
        }
        //initialization of position array
        // class size means total seats in class
        int class_size = noOfColumnsInClass * noOfRowsInClass;
        mappedStudentPositionArray =new String[class_size];
        mappedStudentNameArray =new String[class_size];
        for (int i = 0; i< class_size; i++){
            mappedStudentPositionArray[i]="Empty";
            mappedStudentNameArray[i]="";
        }
        //set the layout on screen and call function to map students
        setContentView(R.layout.activity_grid);
        callCanvas(studentPositionList, studentStateList);
        //database references
        mCourseSessionDatabaseReference = mFirebaseDatabase.getReference().child(courseName).child(nameOfSession);
        //Child event listener adds new student to the array when a new entry is done
        mCourseSessionDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot data_snapshot_student_position, String prev_child_key) {
                // retrieve data from database
                Position object_student_class = data_snapshot_student_position.getValue(Position.class);
                studentPositionList.add(object_student_class);
                studentStateList.add(randamValueGeneratorObject.nextInt(10) );
                mappedStudentPositionArray[(object_student_class.getRow()-1)* noOfColumnsInClass + object_student_class.getColumn()-1]=Integer.toString(object_student_class.getRoll());
                mappedStudentNameArray[(object_student_class.getRow()-1)* noOfColumnsInClass + object_student_class.getColumn()-1]= object_student_class.getName();
                callCanvas(studentPositionList, studentStateList);
            }
            //default function no use but necessary for object declaration
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot_student_position, String prevChildKey) {

            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot_student_position) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot_student_position, String prevChildKey) {

            }
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(GridActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //function responsible for drawing the class represntation on screen
    public void callCanvas(ArrayList<Position> student_position_list, ArrayList<Integer> student_state_list){
        //new object creation
        pixelGrid = new PixelGridView(this);
        // set no of columns and rows in class and call mapping function
        pixelGrid.setNumberOfColumns(noOfColumnsInClass);
        pixelGrid.setNumberOfRows(noOfRowsInClass);
        pixelGrid.setListOfStudents(student_position_list, student_state_list);
        // initialise view
        setContentView(pixelGrid);
    }

    @Override
    //function responsible for showing information about student sitting in the position of professor's query
    public boolean onTouchEvent(MotionEvent screen_touch_event) {
        //if there is a touch on screen
        if (screen_touch_event.getAction() == MotionEvent.ACTION_DOWN) {
            //get the details of cell size and find the cell which was clicked
            studentCellWidth=pixelGrid.getCellWidth();
            studentCellHeight=pixelGrid.getCellHeight();
            // calculates co ordinates of cell which was touched
            int column = (int)(screen_touch_event.getX() / studentCellWidth)+1;
            int row = (int)((screen_touch_event.getY()-250) /studentCellHeight)+1;
            // stores details of student at particular position
            String roll_no_to_display = mappedStudentPositionArray[(row-1)* noOfColumnsInClass +(column-1)];
            String name_of_student = mappedStudentNameArray[(row-1)* noOfColumnsInClass +(column-1)];
            // create dialog and show student details
            AlertDialog.Builder student_details_builder = new AlertDialog.Builder(this);
            student_details_builder.setTitle("Student Information").setMessage(row +","+column +"\n"+ roll_no_to_display +"\n"+ name_of_student).setCancelable(false).setNegativeButton("Close",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog_student_detail, int dialog_id) {
                            dialog_student_detail.cancel();
                            //if cancel button is pressed
                        }
                    });
            //create an alert dialog and show it to the user
            AlertDialog alert_student_detail = student_details_builder.create();
            alert_student_detail.show();
        }
        return true;
    }
}
