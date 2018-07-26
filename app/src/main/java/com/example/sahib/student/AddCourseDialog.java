/*
 Name of Class: AddCourseDialog
 Date of creation : 15-04-2018
 Author's Name: Sahib Khan
 Modification History:  15-04-2018:class created
                        17-04-2018:comments added
 Synopsis of class:    This class creates a custom dialog that takes in the course name and classroom details
 Different functions supported :
                        onCreateDialog()
                        onAttach()
 Global variables accessed/modified by the module : none

*/

//package name
package com.example.sahib.student;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddCourseDialog extends AppCompatDialogFragment {
    //variable for views
    private EditText editTextCourseName;
    private EditText editTextRow;
    private EditText editTextColumn;
    private AddCourseDialog.dialogeListner listener;

    // function called when object is created responsible for initialisation
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog_builder_for_add_course = new AlertDialog.Builder(getActivity());
        //instantiate the dialog with password_prompt xml file
        LayoutInflater layout_inflater = getActivity().getLayoutInflater();
        View view = layout_inflater.inflate(R.layout.dialog_add_course,null);
        dialog_builder_for_add_course.setView(view)
                .setTitle("Course Details")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    // on negative response
                    public void onClick(DialogInterface dialog, int selected_id) {

                    }
                })
                // on positive response
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selected_id) {
                        String course_name = editTextCourseName.getText().toString();
                        int row_value = Integer.parseInt(editTextRow.getText().toString());
                        int column_value = Integer.parseInt(editTextColumn.getText().toString());
                        listener.saveData(course_name, row_value, column_value);
                    }
                });
        // initialise views by id
        editTextCourseName = view.findViewById(R.id.edit_course);
        editTextRow = view.findViewById(R.id.edit_row);
        editTextColumn = view.findViewById(R.id.edit_column);
        // returns the created dialog
        return dialog_builder_for_add_course.create();
    }
    // add listener to the object
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddCourseDialog.dialogeListner) context;
        }
        catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()+"must implement dialogue listener");
        }
    }
    // stores coursename
    public interface dialogeListner{
        void saveData(String course_name, int row, int column);
    }
}



