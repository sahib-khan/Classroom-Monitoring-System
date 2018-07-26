/*
 Name of Class: SignUpDialog
 Date of creation : 15-04-2018
 Author's Name: Shivam Kumar
 Modification History:  15-04-2018:class created
                        17-04-2018:comments added
 Synopsis of class:    This class creates a custom dialog that takes in the user name and roll id and also saves the user profile
 Different functions supported :
                        onCreateDialog()
                        onAttach()
                        setPermission()
 Global variables accessed/modified by the module : none
*/

//package name
package com.example.sahib.student;
// imports required packages
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


// class declaration
public class SignupDialog extends AppCompatDialogFragment {
    // variable declaration
    private boolean permission;
    private EditText name;
    private EditText rollNo;
    private signUpDialogListener listener;

    // function is called on object creation
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // creates object
        AlertDialog.Builder dialog_builder_sign_up = new AlertDialog.Builder(getActivity());
        //instantiate the dialog with password_prompt xml file
        LayoutInflater layout_inflater = getActivity().getLayoutInflater();
        final View view = layout_inflater.inflate(R.layout.dialoge_sign_up,null);
        dialog_builder_sign_up.setView(view)
                .setTitle("User Details")
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
                        String user_name = name.getText().toString();
                        String roll = rollNo.getText().toString();
                        listener.applyTextUserDetails(user_name,roll);
                    }
                });
        // assigning the input text in dialog box to name and rollNo
        name = view.findViewById(R.id.edit_name);
        rollNo = view.findViewById(R.id.edit_roll);
        if(permission){rollNo.setHint("ProfId");}
        return dialog_builder_sign_up.create();
    }
    // add listener to object
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {// Exception handling for unknown events
            listener = (signUpDialogListener) context;
        }
        catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()+ "must implement dialogue listener");
        }
    }
    // stores roll number and password
    public interface signUpDialogListener {
        void applyTextUserDetails(String name, String roll);
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }
}

