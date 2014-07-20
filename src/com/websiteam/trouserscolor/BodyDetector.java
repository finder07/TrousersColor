package com.websiteam.trouserscolor;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.util.Log;



public class BodyDetector {
		
	//private static final String TAG = "myLOG";
		
	private static final int MAX_BODY_IMG_WIDTH = 320;
	private CascadeClassifier	_mJavaDetector; 
	private Mat 				_srcMat;
	private Mat					_srcMini;
	private Mat 				_srcMatBgr = null;
	private Mat 				_bodyImage = null;
	private Mat 				_lowerbodyImage = null;
	    
	private double _scale = 1.;
	private String _pathToApp = "";
	    
	private Rect _bodyRect;
	    
	    
	BodyDetector(Mat mat, String path){
	   	_pathToApp = path;
	   	_srcMat = new Mat();
	   	_srcMat = mat.clone();
	   	_srcMatBgr = new Mat();
	   	_srcMatBgr = _srcMat.clone();
	   	if (_srcMat.channels() == 3){
	   		Imgproc.cvtColor(_srcMat, _srcMat, Imgproc.COLOR_BGR2GRAY);
	   	} 
	}
	    
	public Rect getBodyRect(){
		Rect rect = new Rect();
		rect = _bodyRect.clone();
		return rect;
	}
	
	public void detectBody(){
    	_srcMini = new Mat();
    	if (_srcMat.cols() > MAX_BODY_IMG_WIDTH) {
    		_scale = _srcMat.cols() / (float) MAX_BODY_IMG_WIDTH;
    	    int scaledHeight = (int) Math.round(_srcMat.rows() / _scale);
    	    Imgproc.resize(_srcMat, _srcMini, new Size(MAX_BODY_IMG_WIDTH, scaledHeight));
    	}
    	else {
    		_srcMini = _srcMat.clone();
    	}
    	
    	_mJavaDetector = new CascadeClassifier(_pathToApp + File.separator + MainActivity.CASCADE_PATH + File.separator + MainActivity.CASCADE_FULLBODY);
        if (_mJavaDetector.empty()) {
            Log.e(TAG, "Failed to load cascade classifier");
            _mJavaDetector = null;
        } else {
            Log.i(TAG, "Loaded cascade classifier from " + _pathToApp + File.separator + MainActivity.CASCADE_PATH + File.separator + MainActivity.CASCADE_FULLBODY);
    	}
        
        if (null == _mJavaDetector) return;
        
        _bodyRect =  detectLargestObject(_srcMini, _mJavaDetector, true, 25);
        
        if (_srcMat.cols() > MAX_BODY_IMG_WIDTH) {
        	_bodyRect.x = (int) Math.round(_bodyRect.x * _scale);
        	_bodyRect.y = (int) Math.round(_bodyRect.y * _scale);
        	_bodyRect.width = (int) Math.round(_bodyRect.width * _scale);
        	_bodyRect.height = (int) Math.round(_bodyRect.height * _scale);
    	}

    	if (_bodyRect.x < 0) _bodyRect.x = 0;
    	if (_bodyRect.y < 0) _bodyRect.y = 0;
    	if (_bodyRect.x + _bodyRect.width > _srcMat.cols()) _bodyRect.width = _srcMat.cols() - _bodyRect.x;
    	if (_bodyRect.y + _bodyRect.height > _srcMat.rows()) _bodyRect.height = _srcMat.rows() - _bodyRect.y;
    	
    	if (_bodyRect.width > 0){
    		Mat face = new Mat(_srcMat, _bodyRect);
    		//detectBothEyes(face); - сделать нижнюю половину тела
    		
    		/*if(_rightEye.x >0 && _leftEye.x > 0){
    			transformFace();
    		}*/
    	}
    }
	
	private Rect detectLargestObject(Mat matSrc, CascadeClassifier detector, boolean eq, int minSize){
    	Mat mat = matSrc.clone();
    	if (eq){
    		Imgproc.equalizeHist(mat, mat);
    	}
    	
    	MatOfRect faces = new MatOfRect(); 
    	detector.detectMultiScale(mat, faces, 1.1, 4, 0, new Size(minSize,minSize), new Size(mat.cols(),mat.rows()));
        
    	Rect[] facesArray = faces.toArray();
        
    	Rect rect = new Rect();
    	rect.width = -1;
    	rect.height = -1;
    	
    	Log.i(TAG, "facesArray.length="+facesArray.length);
        
        if (facesArray.length>0){
        	 int maxFace = 0;
        	 int maxSize = 0;
        	 for (int i = 0; i < facesArray.length; i++){
        		 int size = facesArray[i].width * facesArray[i].height;
        		 if (size > maxSize){
        			 maxFace = i;
        			 maxSize = size;
        		 }
        		 Log.i(TAG, "i="+i+"   facesArray[i].width = "+facesArray[i].width+"    facesArray[i].height = "+facesArray[i].height);
        	 }
        	 rect = facesArray[maxFace];
        }
    	return rect;
    }
	

}
