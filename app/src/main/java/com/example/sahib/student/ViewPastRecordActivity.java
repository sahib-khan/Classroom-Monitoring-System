/*
 Name of Module: ViewPastRecordActivity
 Date of creation : 16-04-2018
 Author's Name: Sahib Khan
 Modification History: 17-04-2018 :Created module
                        18-04-2018:Added comments
 Synopsis of module: This activity is responsible for displaying the attendance of the students on any particular conducted
                        session hosted by the professor user displaying present students in green absent in red
 Different functions supported :
                        onCreate() on activity creation for initialization
                        selectCourse() takes in course name from user in form of radio button dialog for which
                            past record is to be seen
                        selectSession() if course is selected it initialises the array list containing all session of that course
                            and takes input from the user in form of radio buttons and takes the information about student present
                            in that session
                        getAllStudentList() when course is known it gets the list of all registered students in that course
                        getSessions() to get all session of selected course
                        setAdapter()
                        showDialogForSelectCourse()
                        showDialogForSelectSession()
 Global variables accessed/modified by the module : none


*/

//package name
package com.example.sahib.student;

// import required packages
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.Toast;

// firebase packages
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewPastRecordActivity extends AppCompatActivity {

    // variable initialization for views and data
    private boolean isCourseSelected;
    private Button buttonSelectCourse;
    private Button buttonSelectSession;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCurrentUserCourseDatabaseReference;
    private DatabaseReference mSessionDatabaseListener;
    private DatabaseReference mRegisteredStudentDatabase;
    private FirebaseAuth mFirebaseAuthenticationDatabase;
    private DatabaseReference mCourseDatabaseReference;
    private DatabaseReference mAttendanceDatabase;
    private ArrayList<CourseDetail> courseListOfProfessor = new ArrayList<>();
    private ArrayList<String> sessionList = new ArrayList<>();
    private ArrayList<String> allStudentList = new ArrayList<>();
    private String items[];
    private String selectedSession;
    private int selectedCourseNo;
    private int selectedSessionNo;
    private List<Student> studentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StudentAdapter mAdapter;


    //function called when activity created responsible for initialization of views
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_past_record);
        //initialize isCourseSelected to false as no course is selected till now
        isCourseSelected =false;
        // initialise recycler view
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new StudentAdapter(studentList);
        RecyclerView.LayoutManager mlayoutmanager_attendance_display = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mlayoutmanager_attendance_display);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //initialise onclick listener on buttons
        buttonSelectCourse = findViewById(R.id.button_select_course);
        buttonSelectSession = findViewById(R.id.button_select_session);
        buttonSelectCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            //call function for selecting courses
            public void onClick(View button_select_course) {
                selectCourse(button_select_course);
            }
        });
        buttonSelectSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button_select_session) {
                if (isCourseSelected){
                    //if course has been selected allow to select session
                    selectSession(button_select_session);
                }
                else {
                    //if no course selected prompt to select course
                    Toast.makeText(ViewPastRecordActivity.this,"First Select Course",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //databases references and intent
        Bundle bundle_courselist = this.getIntent().getExtras();
        //items contains the name of courses that professor takes
        items = bundle_courselist.getStringArray("courses");
        // initialise database references
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuthenticationDatabase = FirebaseAuth.getInstance();
        mCourseDatabaseReference = mFirebaseDatabase.getReference().child("Courses").child("Professor");
        mSessionDatabaseListener = mFirebaseDatabase.getReference().child("Sessions");
        mAttendanceDatabase= mFirebaseDatabase.getReference().child("Attendance");
        // user id is unique key assigned to each user
        String user_id=mFirebaseAuthenticationDatabase.getCurrentUser().getUid().toString();
        mCurrentUserCourseDatabaseReference =mCourseDatabaseReference.child(user_id);
        mRegisteredStudentDatabase = mFirebaseDatabase.getReference().child("RegisteredStudent");
        mCurrentUserCourseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot datasnapshot_course, String prevChildKey) {
                // read all courses for current professor from the database
                CourseDetail newCourse = datasnapshot_course.getValue(CourseDetail.class);
                courseListOfProfessor.add(newCourse);
            }
            //default functions defined when object created but of no use
            @Override
            public void onChildChanged(DataSnapshot datasnapshot_course, String prevChildKey) {

            }
            @Override
            public void onChildRemoved(DataSnapshot datasnapshot_course){

            }
            @Override
            public void onChildMoved(DataSnapshot datasnapshot_course, String prevChildKey){

            }
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(ViewPastRecordActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    //function responsible to get all the existing session of the current selected course from the database
    public void getSessions(){
        isCourseSelected =true;
        //save it in session list to allow user to choose
        //if some course exist add listener
        if(courseListOfProfessor.size()!=0) {
            mSessionDatabaseListener.child(courseListOfProfessor.get(selectedCourseNo).getCourseName()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot datasnapshot_session, String prevChildKey) {
                    if (datasnapshot_session != null) {
                        //add all session in the list of session
                        sessionList.add(datasnapshot_session.getValue().toString());
                    }
                }
                //default functions defined when object created but of no use
                @Override
                public void onChildChanged(DataSnapshot datasnapshot_session, String prevChildKey) {

                }
                @Override
                public void onChildRemoved(DataSnapshot datasnapshot_session) {

                }
                @Override
                public void onChildMoved(DataSnapshot datasnapshot_session, String prevChildKey) {

                }
                @Override
                public void onCancelled(DatabaseError database_error) {
                    // Display the error message to user which caused error
                    Toast.makeText(ViewPastRecordActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this,"No Course Found",Toast.LENGTH_SHORT);
        }

    }

    //function to read all the registered student in the current course
    public void getAllStudentList(){
        mRegisteredStudentDatabase.child(courseListOfProfessor.get(selectedCourseNo).getCourseName()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot datasnapshot_student_in_course, String prevChildKey) {
                allStudentList.add(datasnapshot_student_in_course.getValue().toString());
                Log.d("All list", datasnapshot_student_in_course.getValue().toString());
            }
            //default functions defined when object created but of no use
            @Override
            public void onChildChanged(DataSnapshot datasnapshot_student_in_course, String prevChildKey) {

            }
            @Override
            public void onChildRemoved(DataSnapshot datasnapshot_student_in_course) {

            }
            @Override
            public void onChildMoved(DataSnapshot datasnapshot_student_in_course, String prevChildKey) {

            }
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(ViewPastRecordActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    //function to open dialog to select course
    public void selectCourse(View button_select_course){
        showDialogForSelectCourse();;
    }

    //function to open dialog for session
    public void selectSession(View button_select_session){
        showDialogForSelectSession();
    }

    //function checks among the registered student who were present and who were absent
    public void setAdapter(){
        //from the database get the list get students who were present on that particular session
        mAttendanceDatabase.child(courseListOfProfessor.get(selectedCourseNo).getCourseName()).child(selectedSession).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot_present_student_list) {
                for (int i = 0; i< allStudentList.size(); i++){
                    // check if student exists in present student list in database by getting details of student
                    String detail_of_student[]= allStudentList.get(i).split("_");
                    //check if present
                    if(datasnapshot_present_student_list !=null && datasnapshot_present_student_list.hasChild(detail_of_student[0])){
                        Student student_instance = new Student(allStudentList.get(i), "Present");
                        studentList.add(student_instance);
                    }
                    //else mark absent
                    else {
                        Student student_instance = new Student(allStudentList.get(i), "Absent");
                        studentList.add(student_instance);
                    }
                    // modify the list in adapter to display changes on screen
                    mAdapter.notifyDataSetChanged();
                }
            }
            //default functions defined when object created but of no use
            @Override
            public void onCancelled(DatabaseError database_error) {
                // Display the error message to user which caused error
                Toast.makeText(ViewPastRecordActivity.this,database_error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    //convert array list into array to use it in list
    public void showDialogForSelectCourse(){
        //convert arraylist in array to be used in adapter
        String courses_list[] =new String [courseListOfProfessor.size()];
        for (int i = 0; i< courseListOfProfessor.size(); i++){
            courses_list[i]= courseListOfProfessor.get(i).getCourseName();
        }
        // create dialog to display courses to select from
        AlertDialog.Builder alert_dialog_builder_course_selection = new AlertDialog.Builder(
                this);
        alert_dialog_builder_course_selection.setTitle("Courses").setSingleChoiceItems(courses_list, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected_index) {
                selectedCourseNo= selected_index;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected_index) {
                sessionList.clear();
                getSessions();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected_index) {
                dialog.cancel();
            }
        });
        AlertDialog alertdialog_course_selection = alert_dialog_builder_course_selection.create();
        // show it
        alertdialog_course_selection.show();
    }

    //show dialog for session selection
    public void showDialogForSelectSession(){
        //convert arraylist in array to be used in adapter
        String session_list[] =new String [sessionList.size()];
        for (int i = 0; i< sessionList.size(); i++){
            session_list[i]= sessionList.get(i);
        }
        // create dialog to display all session taken till now to get past record of that session
        AlertDialog.Builder alert_dialog_builder_session_selection = new AlertDialog.Builder(
                this);
        alert_dialog_builder_session_selection.setTitle("Sessions").setSingleChoiceItems(session_list, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected_index) {
                selectedSessionNo= selected_index;
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected_index) {
                if(sessionList.size()!=0){
                    //get the session selected and clear adapter for new
                    selectedSession = sessionList.get(selectedSessionNo);
                    allStudentList.clear();
                    getAllStudentList();
                    studentList.clear();
                    mAdapter.notifyDataSetChanged();
                    setAdapter();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            //if cancel is clicked
            public void onClick(DialogInterface dialog, int selected_index) {
                dialog.cancel();
            }
        });
        AlertDialog alertdialog_session_selection = alert_dialog_builder_session_selection.create();
        // show it
        alertdialog_session_selection.show();
    }
}
