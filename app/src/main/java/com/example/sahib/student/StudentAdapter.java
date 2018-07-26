/*
 Name of Class: StudentAdapter
 Date of creation : 15-04-2018
 Author's Name: Sahib Khan
 Modification History:  15-04-2018:class created
                        17-04-2018:comments added
 Synopsis of class:    This class manages the data to be displayed on screen using recycler view.It takes the Student class object and
                            displays it as list view on screen.
 Different functions :
                    onCreateViewHolder()
                    onBindViewHolder()
                    getItemCount()
 Global variables accessed/modified by the module : none
*/

//package name
package com.example.sahib.student;

// import required packages
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {
    // list to save all student in course
    private List<Student> studentsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rollNo,  attendance;
        // constructor for initialisation
        public MyViewHolder(View view) {
            super(view);
            rollNo = (TextView) view.findViewById(R.id.roll);
            attendance = (TextView) view.findViewById(R.id.attendance);

        }
    }
    public StudentAdapter(List<Student> studentsList) {
        // initialise student list
        this.studentsList = studentsList;
    }

    @Override
    // initialise the xml file for recycler view
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    // initialise the text views and set their values
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Student student = studentsList.get(position);
        // split to get name and rollno
        String student_details[]= student.getRoll().split("_");
        holder.rollNo.setText(student_details[0]);
        holder.attendance.setText(student_details[1]);
        if(student.getAttendance().equals("Absent")) {
            // if student is absent change text color to red
            holder.rollNo.setTextColor(Color.RED);
            holder.attendance.setTextColor(Color.RED);
        }
        else{
            // if student is present set text color to green
            holder.rollNo.setTextColor(Color.rgb(0,200,0));
            holder.attendance.setTextColor(Color.rgb(0,200,0));
        }
    }
    @Override
    public int getItemCount() {
        // return size of studentList
        return studentsList.size();
    }
}
