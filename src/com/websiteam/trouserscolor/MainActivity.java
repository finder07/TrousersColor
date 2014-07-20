package com.websiteam.trouserscolor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MainActivity extends Activity implements CvCameraViewListener2 {

	private static final String TAG = "myLOG";
	private CameraBridgeViewBase mOpenCvCameraView; 
	
	public static final String CASCADE_FULLBODY 		= "haarcascade_fullbody.xml";
	public static final String CASCADE_LOWERBODY 		= "haarcascade_lowerbody.xml";
	
	public static final String CASCADE_PATH 			= "cascade";
	
	static {
		
	    Log.i(TAG, "-1");
		
	    if (!OpenCVLoader.initDebug()) {
	    	//Log.i(TAG,"not static init ");
	    } else {
	    	//Log.i(TAG, "0"); 
	    }
	    
 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.main); 
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surface_view); 
		
		copyCascadeFile();
	}
	
	private void copyCascadeFile(){
		if (!hasExternalStoragePrivateFile(CASCADE_PATH, CASCADE_FULLBODY)){
			File myDir = new File(getExternalFilesDir(null), CASCADE_PATH);    
		    myDir.mkdirs();
			File file = new File(getExternalFilesDir(CASCADE_PATH), CASCADE_FULLBODY);
		    try {
		    	InputStream is = getResources().openRawResource(R.raw.haarcascade_fullbody);
		        OutputStream os = new FileOutputStream(file);
		        byte[] buffer = new byte[4096];
	            int bytesRead;
	            while ((bytesRead = is.read(buffer)) != -1) {
	                os.write(buffer, 0, bytesRead);
	            }
	            is.close();
	            os.close(); 
		    } catch (IOException e) {
		        Log.w("ExternalStorage", "Error writing " + file, e);
		    }
		}
		if (!hasExternalStoragePrivateFile("cascade", CASCADE_LOWERBODY)){
			File myDir = new File(getExternalFilesDir(null), CASCADE_PATH);    
		    myDir.mkdirs();
			File file = new File(getExternalFilesDir(CASCADE_PATH), CASCADE_LOWERBODY);
		    try {
		    	InputStream is = getResources().openRawResource(R.raw.haarcascade_lowerbody);
		        OutputStream os = new FileOutputStream(file);
		        byte[] buffer = new byte[4096];
	            int bytesRead;
	            while ((bytesRead = is.read(buffer)) != -1) {
	                os.write(buffer, 0, bytesRead);
	            }
	            is.close();
	            os.close(); 
		    } catch (IOException e) {
		        Log.w("ExternalStorage", "Error writing " + file, e);
		    }
		}
	}
	
	
	private boolean hasExternalStoragePrivateFile(String path, String filename) {
		if (!sdAvailable()) return false;
	    File file = new File(getExternalFilesDir(path), filename);
	    return file.exists();
	}
	
	// Есть ли SD - карта
	private boolean sdAvailable(){
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) { // We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) { // We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else { // Something else is wrong. It may be one of many other states, but all we need to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return (mExternalStorageAvailable && mExternalStorageWriteable);
	}
	
	@Override
    public void onResume()
    {
        super.onResume();
        
        mOpenCvCameraView.enableView(); 
    	mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
         
    } 

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		// TODO Auto-generated method stub
		return inputFrame.rgba();
	}
	
	@Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    } 
	
	public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    } 
	
}
