package com.example.androidsd1.cameragalleryapplication;


import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";

    private Context mContext;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mContext = context;
        mCamera = camera;

        // supported preview sizes
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        for(Camera.Size str: mSupportedPreviewSizes)

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // empty. surfaceChanged will take care of stuff}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

//        // set preview size and make any resize, rotate or reformatting changes here
//        // start preview with new settings
//        try {
//            Camera.Parameters parameters = mCamera.getParameters();
//            //parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//            //mCamera.setDisplayOrientation(90);
//            Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//
//            if(display.getRotation() == Surface.ROTATION_0)
//            {
//                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//                mCamera.setDisplayOrientation(90);
//            }
//            if(display.getRotation() == Surface.ROTATION_90)
//            {
//                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//                mCamera.setDisplayOrientation(0);
//            }
//
//            if(display.getRotation() == Surface.ROTATION_180)
//            {
//                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//            }
//
//            if(display.getRotation() == Surface.ROTATION_270)
//            {
//                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//                mCamera.setDisplayOrientation(180);
//            }
//            mCamera.setParameters(parameters);
//            mCamera.setPreviewDisplay(mHolder);
//            mCamera.startPreview();
//
//        } catch (Exception e){
//        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }

        if (mPreviewSize!=null) {
            float ratio;
            if(mPreviewSize.height >= mPreviewSize.width)
                ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
            else
                ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

            // One of these methods should be used, second method squishes preview slightly
            setMeasuredDimension(width, (int) (width * ratio));
            //        setMeasuredDimension((int) (width * ratio), height);
        }
    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//        if (height > width ) {
//            setMeasuredDimension(width, height);
//            if (mSupportedPreviewSizes != null) {
//                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
//            }
//
//            float ratio;
//            if(mPreviewSize.height >= mPreviewSize.width)
//                ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
//            else
//                ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;
//
//            float camHeight = (int) (width * ratio);
//            float newCamHeight;
//            float newHeightRatio;
//
//            if (camHeight < height) {
//                newHeightRatio = (float) height / (float) mPreviewSize.height;
//                newCamHeight = (newHeightRatio * camHeight);
//                setMeasuredDimension((int) (width * newHeightRatio), (int) newCamHeight);
//            } else {
//                newCamHeight = camHeight;
//                setMeasuredDimension(width, (int) newCamHeight);
//            }
//        }
//        else {
//            setMeasuredDimension(width, height);
//            if (mSupportedPreviewSizes != null) {
//                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
//            }
//
//            float ratio;
//            if(mPreviewSize.width >= mPreviewSize.height)
//                ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;
//            else
//                ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
//
//            float camWidth = (int) (height * ratio);
//            float newCamWidth;
//            float newWidthRatio;
//
//            if (camWidth < width) {
//                newWidthRatio = (float) width / (float) mPreviewSize.width;
//                newCamWidth = (newWidthRatio * camWidth);
//                setMeasuredDimension( (int) newCamWidth,(int) (height * newWidthRatio));
//            } else {
//                newCamWidth = camWidth;
//                setMeasuredDimension((int) newCamWidth,height);
//            }
//        }
//
//    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }
}