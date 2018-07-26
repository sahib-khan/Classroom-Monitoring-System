/*
 Name of Module: ScanActivity
 Date of creation : 16-04-2018
 Author's Name: Abhinav Mishra
 Modification History: 16-04-2018 :Created module
                        18-04-2018:Added comments
 Synopsis of module: This module is responsible for the boundary class for scanning the QR code
 Different functions supported : This module uses the google vision api
                            to scan the Qr code on the screen for its value.
                            onCreate()
 Global variables accessed/modified by the module : none


*/

//package name
package com.example.sahib.student;

//import statements
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {
    //variable for camera view
    SurfaceView cameraView;
    //variable for barcode
    BarcodeDetector barcode;
    //variable for camera source
    CameraSource cameraSource;
    //variable for holder
    SurfaceHolder holder;

    //function called on activity creation responsible for variable initialization
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializing variables
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        cameraView= (SurfaceView) findViewById(R.id.camera_view);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();
        barcode = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        //check if successful or not
        if (! barcode.isOperational()){
            Toast.makeText(getApplicationContext(),"Sorry Couldn't scan",Toast.LENGTH_SHORT).show();
            this.finish();
        }
        //initialize camera source
        cameraSource =new  CameraSource.Builder(this,barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920,1024)
                .build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            //if permission given start camera for scanning
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        cameraSource.start(cameraView.getHolder());
                    }
                }
                catch (IOException error){
                    error.printStackTrace();
                }
            }
            //default function on object declaration not used
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        barcode.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }
            //scan the value
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode = detections.getDetectedItems();
                // if barcode size is valid
                if (barcode.size() > 0){
                    Intent intent_result_student_main_activity = new Intent();
                    intent_result_student_main_activity.putExtra("barcode",barcode.valueAt(0));
                    setResult(RESULT_OK, intent_result_student_main_activity);
                    finish();
                }
            }
        });
    }

}
