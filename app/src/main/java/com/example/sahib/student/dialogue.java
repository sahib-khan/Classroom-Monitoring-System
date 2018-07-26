/*
 Name of Class: dialogue
 Date of creation : 15-04-2018
 Author's Name: Abhinav Mishra
 Modification History:  15-04-2018:class created
                        17-04-2018:comments added
 Synopsis of class:    This class creates a custom dialog for session password
 Different functions :
                        onCreateDialog()
                        onAttach()
 Global variables accessed/modified by the module : None

*/

//package name
package com.example.sahib.student;

// import statement
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class dialogue extends AppCompatDialogFragment {
    //variable for views
    private EditText sessionPassword;
    private dialogueListner listener;

    // function called when object is created responsible for initialisation
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog_builder_for_session_name = new AlertDialog.Builder(getActivity());
        //instantiate the dialog with password_prompt xml file
        LayoutInflater layout_inflater = getActivity().getLayoutInflater();
        View view = layout_inflater.inflate(R.layout.dialogue_password,null);
        dialog_builder_for_session_name.setView(view)
                .setTitle("Session Password")
                // on negative response
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selected_id) {
                    }
                })
                // on positive response
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selected_id) {
                        String password = sessionPassword.getText().toString();
                        listener.applyTextsSessionName(password);
                    }
                });
        sessionPassword = view.findViewById(R.id.edit_password);
        // return the created dialog
        return dialog_builder_for_session_name.create();
    }
    // add listener to the object
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (dialogueListner) context;
        }
        catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()+"must imlement dialogue listener");
        }
    }
    // stores coursename
    public interface dialogueListner{
        void applyTextsSessionName(String password);
    }
}
