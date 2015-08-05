package com.example.laboratorio.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends Activity {
    private static final String TAG = "Camera";
    private Camera mCamera = null;
    private CameraView mCameraView = null;
    int stillCount = 0;
    int numero ;
    ImageButton buttonClick = null ;
    private Spinner mSpinner;
    protected static final int MEDIA_TYPE_IMAGE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if(mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }
        //esta es la lista desplegable
        mSpinner = (Spinner) findViewById(R.id.spinner1);
        Integer [] fotos ={5,10,15,20,25,30};
        ArrayAdapter <Integer> adapter = new ArrayAdapter<Integer>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,fotos);
        mSpinner.setAdapter(adapter);

        //btn to close the application
        ImageButton imgClose = (ImageButton)findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        ImageButton take = (ImageButton)findViewById(R.id.take);
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture(v);
            }
        });
    }
    public void capture(View v) {
        final Camera.PictureCallback pictureCB;
        pictureCB = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera cam) {
                File picFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (picFile == null) {
                    Log.e(TAG, "Couldn't create media file; check storage permissions?");
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "Error", Toast.LENGTH_SHORT);

                    toast1.show();
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(picFile);
                    fos.write(data);
                    Toast toast2 =
                            Toast.makeText(getApplicationContext(),
                                    "Archivo Creado ", Toast.LENGTH_SHORT);
                    toast2.show();
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "File not found: " + e.getMessage());
                    e.getStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, "I/O error writing file: " + e.getMessage());
                    e.getStackTrace();
                }
                mCameraView.refreshCamera();//para mantener la vista previa activa
                buttonClick.setEnabled(true);
            }
        };
        //aqui pondre el metodo para poder poner el spinner

        String selec = mSpinner.getSelectedItem().toString();
        if (selec.equals("5")) {
            numero= 5;
        } else if (selec.equals("10")) {
            numero = 10;
        }else if (selec.equals("15")) {
            numero = 15;
        }else if (selec.equals("20")) {
            numero = 20;
        }else if (selec.equals("25")) {
            numero = 25;
        }

        while (stillCount < numero) {
                mCamera.takePicture(null, null, pictureCB);//quitar el while para regresar a lo de antes
            stillCount++;
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

        /*/esto es nuevo tengo que probarlo
        try{
            stillCount ++;
        for (stillCount= 0; stillCount < 10; stillCount++){
            mCamera.startPreview();
            if (stillCount < 10){
                mCamera.takePicture(null,null,pictureCB);
        }else {
                stillCount = 0;
                buttonClick.setEnabled(true);
            }
        }
        }catch (Exception e){
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
            esto lo cambiare de otra manera para ver si funciona
        }*/

    }
    private File getOutputMediaFile(int type){
        File dir = new File(Environment.getExternalStorageDirectory().getPath()+ "/muestra");
        if (!dir.exists())
        {
            if (!dir.mkdirs())
            {
                Log.e(TAG, "Failed to create storage directory.");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.UK).format(new Date());
        if (type == MEDIA_TYPE_IMAGE)
        {
            return new File(dir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        }
        else
        {
            return null;
        }
    }
}
