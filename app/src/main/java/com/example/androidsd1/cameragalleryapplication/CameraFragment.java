package com.example.androidsd1.cameragalleryapplication;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.spoyl.android.imagecrop.view.ImageCropView;

public class CameraFragment extends Fragment{
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;


    /** Called when the activity is first created. */
    private Context context;
    private Activity act;


    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putInt("abc", 0);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        act = this.getActivity();
        View visor = inflater.inflate(R.layout.fragment_camera, container, false);
        // Create an instance of Camera
        if (checkCameraHardware(context)) {
            mCamera = getCameraInstance();
        }
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(context, mCamera);
        SquareFrameLayout preview = (SquareFrameLayout) visor.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        //   List<Size> sizes = parameters.getSupportedPreviewSizes();
        //  Size optimalSize = getOptimalPreviewSize(sizes, width, height);
        // parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0)
        {               LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) preview.getLayoutParams();

            //params.setMargins(0, -218, 0, 0);
            //preview.setLayoutParams(new FrameLayout.LayoutParams(300, 49));
            preview.setLayoutParams(params);

        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) preview.getLayoutParams();

            //params.setMargins(0, -218, 0, 0);
            //preview.setLayoutParams(new FrameLayout.LayoutParams(300, 49));params.setMargins(0, -218, 0, 0);
            preview.setLayoutParams(params);
        }

        if(display.getRotation() == Surface.ROTATION_180)
        {           LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) preview.getLayoutParams();

            //params.setMargins(0, -218, 0, 0);
            //preview.setLayoutParams(new FrameLayout.LayoutParams(300, 49));
            preview.setLayoutParams(params);
        }

        if(display.getRotation() == Surface.ROTATION_270)
        {           LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) preview.getLayoutParams();

            //params.setMargins(0, -218, 0, 0);
            //preview.setLayoutParams(new FrameLayout.LayoutParams(300, 49));
            preview.setLayoutParams(params);
        }

        final ImageButton captureButton = (ImageButton) visor.findViewById(R.id.button_capture2);


        // Add a listener to the Capture button
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (isRecording) {
                            // stop recording and release camera
                            mMediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            mCamera.lock();         // take camera access back from MediaRecorder

                            // inform the user that recording has stopped
                            //captureButton.setText("Record Video");
                            isRecording = false;
                        } else {
                            // initialize video camera
                            if (prepareVideoRecorder()) {
                                // Camera is available and unlocked, MediaRecorder is prepared,
                                // now you can start recording
                                mMediaRecorder.start();

                                // inform the user that recording has started
                                //captureButton.setText("Stop");
                                isRecording = true;
                            } else {
                                // prepare didn't work, release the camera
                                releaseMediaRecorder();
                                // inform user
                            }
                        }
                    }
                }
        );
        return visor;
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private boolean prepareVideoRecorder(){

        //mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try
        {
            if (mCamera==null  ) {
                context = getActivity().getApplicationContext();
                act = this.getActivity();
                //act.setContentView(R.layout.camera);
                mCamera = getCameraInstance();
                //mCamera.setPreviewCallback(null);
                mPreview = new CameraPreview(context, mCamera);//set preview
                FrameLayout preview = (FrameLayout) this.act.findViewById(R.id.camera_preview);
                preview.addView(mPreview);
            }

        } catch (Exception e){
        }
    }



    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}