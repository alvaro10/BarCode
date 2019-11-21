package com.example.barcode;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends Activity implements Detector.Processor {


    private TextView textView;
    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) this.findViewById(R.id.txtContent);
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);



        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
        if(!barcodeDetector.isOperational()){
            TextView txtView = (TextView) findViewById(R.id.txtContent);
            txtView.setText("Could not set up the detector!");
            return;
        }else{
            barcodeDetector.setProcessor(this);
        }

        cameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector).
                setRequestedPreviewSize(1024,768).setAutoFocusEnabled(true)
                .build();


        final Activity activity = this;

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},1024);
                        return;
                    }
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException ie){
                    Log.e("Camera start problem", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });






//        Button btn = (Button) findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Load the Image
//                Bitmap myBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.puppy);
//                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
//                myImageView.setImageBitmap(myBitmap);
//
//                // Setup the Barcode Detector
//                BarcodeDetector detector =
//                        new BarcodeDetector.Builder(getApplicationContext())
//                                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE | Barcode.CODE_128 )
//                                .build();
//                if(!detector.isOperational()){
////            Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
//                    TextView txtView = (TextView) findViewById(R.id.txtContent);
//                    txtView.setText("Could not set up the detector!");
//                    return;
//                }
//
//                // Detect the Barcode
//                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
//                SparseArray<Barcode> barcodes = detector.detect(frame);
//
//                // Decode the Barcode
//                Barcode thisCode = barcodes.valueAt(0);
//                TextView txtView = (TextView) findViewById(R.id.txtContent);
//                txtView.setText(thisCode.rawValue);
//
//            }
//        });

    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections detections) {
        final SparseArray<Barcode> barcodes = detections.getDetectedItems();

        if(barcodes.size() != 0){
            final StringBuilder sb = new StringBuilder();
            for(int i =0 ; i<barcodes.size(); ++i){
                sb.append(barcodes.valueAt(i).rawValue).append("\n");
            }
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(sb.toString());
                    Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
