/*
 Name of Class: User
 Date of creation : 15-04-2018
 Author's Name: Sahib Khan
 Modification History:  15-04-2018:class created
                        17-04-2018:comments added
 Synopsis of class:    This class encapsulates the user with attributes roll name and permission denoting true if professor
                        false if student.
 Different functions :
                    getRoll()
                    setRoll()
                    setName()
                    getName()
                    isPermission()
                    setPermission()
 Global variables accessed/modified by the module : none

*/

//package name
package com.example.sahib.student;

public class User {

    /**
     * permission defines that user type
     * for Professor permission =True
     * for Student permission = False
     */
    private boolean permission;
    private int roll;
    private String name;
    public User(){

    }

    //constructor
    public User(boolean permission, int roll, String name) {
        this.permission = permission;
        this.roll = roll;
        this.name = name;
    }

    // setter Methods
    public void setPermission(boolean permission) {
        this.permission = permission;
    }
    public void setRoll(int roll) {
        this.roll = roll;
    }
    public void setName(String name) {
        this.name = name;
    }
    // Getter methods
    public int getRoll() {
        return roll;
    }
    public boolean isPermission() {
        return permission;
    }
    public String getName() {
        return name;
    }
}
