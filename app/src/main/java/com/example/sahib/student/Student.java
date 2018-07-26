/*
 Name of Class: Student
 Date of creation : 15-04-2018
 Author's Name: Abhinav Mishra
 Modification History:  15-04-2018:class created
                        17-04-2018:comments added
 Synopsis of class:    This class encapsulates the student attendance in a session with variables like roll no and attendance
 Different functions :
                    getRoll()
                    setRoll()
                    getAttendance()
                    setAttendance()
 Global variables accessed/modified by the module : none
*/

//package name
package com.example.sahib.student;

public class Student {
    private String roll,attendance;

    public Student() {
    }

    //constructor
    public Student(String roll, String attendance) {
        this.roll = roll;
        this.attendance = attendance;
    }

    //getter method
    public String getRoll() {
        return roll;
    }
    //getter method
    public String getAttendance() {
        return attendance;
    }

    //setter method
    public void setRoll(String roll) {
        this.roll = roll;
    }
    //setter method
    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}