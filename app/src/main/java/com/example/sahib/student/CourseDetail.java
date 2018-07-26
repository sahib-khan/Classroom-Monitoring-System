/*
 Name of Class: CourseDetail
 Date of creation : 15-04-2018
 Author's Name: Shivam Kumar
 Modification History:  15-04-2018:class created
                        17-04-2018:comments added
 Synopsis of class:    This class encapsulates the course information course name ,row ,column
 Different functions :
                    getCourseName()
                    getRow()
                    getColumn()
                    setCourseName()
                    setRow()
                    setColumn()
Global variables accessed/modified by the module : none
*/

//package name
package com.example.sahib.student;

public class CourseDetail {
    //stores the name of course
    private String courseName	;
    // variables to store the dimension of class for the course
    private int row;
    private int column;

    //constructor
    public CourseDetail(){
    }
    // constructor
    public CourseDetail(String course, int row, int column) {
        this.courseName = course;
        this.row = row;
        this.column = column;
    }
    // returns the course name
    public String getCourseName() {
        return courseName;
    }
    // returns the number of row
    public int getRow() {
        return row;
    }
    // return the nmber of column
    public int getColumn() {
        return column;
    }
    //sets the course name of  object
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    // sets the number of rows of  object
    public void setRow(int row) {
        this.row = row;
    }
    // sets the number of column of object
    public void setColumn(int column) {
        this.column = column;
    }
}