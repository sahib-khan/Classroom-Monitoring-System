/*
 Name of Class: PixelGridView
 Date of creation : 16-04-2018
 Author's Name: Shivam Kumar
 Modification History:  16-04-2018:class created
                        18-04-2018:comments added
 Synopsis of class:    The instance of this class represents a 2d View of class with student mapped to their position in
                        classroom with their state augmented on it.
 Different functions :
                        getCellWidth()
                        getCellHeight()
                        setListOfStudents()
                        PixelGridView()
                        setNumberOfColumns()
                        getNumberOfColumns()
                        getNumberOfRows()
                        setNumberOfRows()
                        onSizeChanged()
                        calculateDimensions()
                        onDraw()
                        augment()


Global variables accessed/modified by the module : none

*/
package com.example.sahib.student;
//this class creates the canvas for ClassView;
//imports required packages
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
//
public class PixelGridView extends View {
    //variables to calculate class dimension
    private int numberOfColumns, numberOfRows;
    private int cellWidth, cellHeight;

    // variables for canvas paint object
    private Paint blackPaint = new Paint();
    private Paint redPaint = new Paint();
    private Paint greenPaint = new Paint();
    private Paint bluePaint = new Paint();

    // array to store students position
    private ArrayList<Position> listOfStudents;
    // array to store roll number of students
    private ArrayList<Integer> listOfStudentStates;

    //constructor
    public PixelGridView(Context context) {
        this(context, null);
    }
    // returns the cell width of 2d view
    public int getCellWidth() {
        return cellWidth;
    }
    //returns the cell height of 2d view
    public int getCellHeight() {
        return cellHeight;
    }
    //initialise list of student
    public void setListOfStudents(ArrayList<Position> list_of_student, ArrayList<Integer> list_of_student_states) {
        this.listOfStudents = list_of_student;
        this.listOfStudentStates = list_of_student_states;
    }
    // assigning the values to attributes of  blackpaint object
    public PixelGridView(Context context, AttributeSet attributes_set) {
        super(context, attributes_set);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }
    //sets the number of columns in the grid view
    public void setNumberOfColumns(int number_of_columns) {
        this.numberOfColumns = number_of_columns;
        calculateDimensions();

    }
    // returns the number of columns in grid view
    public int getNumberOfColumns() {
        return numberOfColumns;
    }
    //sets the number of rows in the grid view
    public void setNumberOfRows(int number_of_rows) {
        this.numberOfRows = number_of_rows;
        calculateDimensions();

    }
    // return the number of rows in grid view
    public int getNumberOfRows() {
        return numberOfRows;
    }


    @Override
    protected void onSizeChanged(int width, int height, int old_width, int old_height) {
        super.onSizeChanged(width, height, old_width, old_height);
        calculateDimensions();
    }
    // calculate the cell dimension of 2d view
    private void calculateDimensions() {
        if (numberOfColumns < 1 || numberOfRows < 1) {
            return;
        }
        cellWidth = getWidth() / numberOfColumns;
        cellHeight = getHeight() / numberOfRows;
        invalidate();
    }
    // draws the grid view
    @Override
    protected void onDraw(Canvas canvas) {

        // if students list is not null then augments the student's details at his position in grid view
        if(listOfStudents !=null){
            for (int i = 0; i< listOfStudents.size(); i++){
                augment(canvas, listOfStudents.get(i).getColumn()-1, listOfStudents.get(i).getRow()-1, listOfStudentStates.get(i));
            }
        }
        if (numberOfColumns == 0 || numberOfRows == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();
        // sets the width of line in table
        blackPaint.setStrokeWidth(10);
        for (int i = 1; i < numberOfColumns; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, blackPaint);
        }

        for (int i = 1; i < numberOfRows; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, blackPaint);
        }
        // assigning the color to the paint object
        blackPaint.setARGB(100,144,144,144);
        for (int i = 0; i < numberOfColumns; i++) {
            for (int j = 0; j < numberOfRows; j++) {

                //draws rectangle at seat
                canvas.drawRect(i * cellWidth, j * cellHeight,
                        (i + 1) * cellWidth, (j + 1) * cellHeight,
                        blackPaint);
            }
        }
    }
    // augments the symbol to corresponding states
    public void augment(Canvas canvas,int row,int col,int state){
        String symbol_for_state;
        // if state is greater than 7 then it assigns tick mark at that position
        if(state >7 ) {
            symbol_for_state ="\u2713";
            greenPaint.setTextSize(cellWidth);
            greenPaint.setARGB(200, 0, 255, 0);
            canvas.drawText(symbol_for_state, row * cellWidth+cellWidth/10, col * cellHeight + cellHeight-cellHeight/5, greenPaint);
        }
        // if state is less than 5 then it assigns x symbol at that position
        if (state <5 ){
            symbol_for_state ="x";
            redPaint.setTextSize(cellWidth);
            redPaint.setARGB (200,255,0,0);
            canvas.drawText(symbol_for_state,row*cellWidth+cellWidth/5,col*cellHeight+cellHeight-cellHeight/4,redPaint);
        }
        // if state is btn 5 and 7 then it assigns "?" symbol at that position
        if(state <8 && state >4) {
            bluePaint.setTextSize(cellWidth);
            bluePaint.setARGB(200, 0, 0, 255);
            symbol_for_state = "?";
            canvas.drawText(symbol_for_state, row * cellWidth +cellWidth/4, col * cellHeight + cellHeight-cellHeight/4, bluePaint);
        }
    }
}