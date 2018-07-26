/*
 Name of Class: RegisterCourseDialog
 Date of creation : 15-04-2018
 Author's Name: Abhinav Mishra
 Modification History:  15-04-2018:class created
                        17-04-2018:comments added
 Synopsis of class:    This class creates a custom dialog that takes in the course name for registration
 Different functions supported :
                        onCreateDialog()
                        onAttach()
 Global variables accessed/modified by the module : none
*/

//package name
package com.example.sahib.student;

//import statement
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;



public class RegisterCourseDialog extends AppCompatDialogFragment {
    //variables for views
    private EditText editTextCourseName;
    private RegisterCourseDialog.dialogueListner listener;

    //function called when object created responsible for initialization
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //initializion
        AlertDialog.Builder dialog_builder_for_register_course = new AlertDialog.Builder(getActivity());
        //instantiate the dialog with password_prompt xml file
        LayoutInflater layout_inflater = getActivity().getLayoutInflater();
        View view = layout_inflater.inflate(R.layout.dialog_register_course,null);
        editTextCourseName = view.findViewById(R.id.edit_course);
        dialog_builder_for_register_course.setView(view)
                .setTitle("Course Name")
                //on negative response
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selected_id) {

                    }
                })
                //on positive response
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selected_id) {
                        String course_name = editTextCourseName.getText().toString();
                        listener.applyTextRegisterCourse(course_name);
                    }
                });
        //return the created dialog
        return dialog_builder_for_register_course.create();
    }


    //add listener to the object
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (RegisterCourseDialog.dialogueListner) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()+
                    "must implement dialogue listener");
        }
    }


    //stores coursename
    public interface dialogueListner{
        void applyTextRegisterCourse(String coursename);
    }
}


