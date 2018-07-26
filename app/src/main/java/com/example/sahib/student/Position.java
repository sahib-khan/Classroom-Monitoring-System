/*
 Name of Class: Position
 Date of creation : 15-04-2018
 Author's Name: Sahib Khan
 Modification History:  15-04-2018:class created
                        17-04-2018:comments added
 Synopsis of class:    This class encapsulates the student position information from QR code like
                       roll no,row no,column no,name of student
 Different functions :
                        getRoll()
                        setRoll()
                        getColumn()
                        setColumn()
                        getRow()
                        setRow()
                        getName()
                        setName()

 Global variables accessed/modified by the module : none
*/

//package declaration
package com.example.sahib.student;

public class Position {

    private int row;
    private int column;
    private int roll_no;
    private String name;

    public Position() {
    }

    public Position(int row, int column, int roll_no, String name) {
        this.roll_no = roll_no;
        this.row=row;
        this.column=column;
        this.name=name;
    }

    // getter methods
    public int getRoll() {
        return roll_no;
    }
    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
    public String getName() {
        return name;
    }

    // setter methods
    public void setRoll(int roll) {
        this.roll_no = roll;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public void setColumn(int column) {
        this.column = column;
    }
    public void setName(String name) {
        this.name = name;
    }
}


