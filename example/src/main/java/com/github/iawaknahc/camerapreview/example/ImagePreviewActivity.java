package com.github.iawaknahc.camerapreview.example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.iawaknahc.camerapreview.ExifOrientation;

import java.io.File;
import java.io.IOException;

public class ImagePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ImagePreviewActivity";

    private ImageView imageView;
    private TextView textView;
    private Button buttonCorrectImage;
    private ExifOrientation exifOrientation;
    private boolean shouldShowOriginal = true;
    private Bitmap cachedCorrectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_imagepreview);

        imageView = (ImageView) findViewById(R.id.image_view);
        textView = (TextView) findViewById(R.id.text_view);

        buttonCorrectImage = (Button) findViewById(R.id.button_correct_image);
        buttonCorrectImage.setOnClickListener(this);

        initExifOrientation();
        renderImageView();
        renderTextView();
        renderButtonCorrectImage();
    }

    private Uri getImageUri() {
        return getIntent().getData();
    }

    private void renderImageView() {
        buildCorrectedBitmap();
        if (shouldShowOriginal) {
            imageView.setImageURI(getImageUri());
        } else {
            imageView.setImageBitmap(cachedCorrectedBitmap);
        }
    }

    private void initExifOrientation() {
        try {
            Uri uri = getImageUri();
            File file = new File(uri.getPath());
            ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            exifOrientation = ExifOrientation.fromExifValue(orientation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderTextView() {
        textView.setText("detected exif orientation: " + exifOrientation.toString());
    }

    private void renderButtonCorrectImage() {
        buttonCorrectImage.setText(getButtonText());
    }

    private String getButtonText() {
        if (shouldShowOriginal) {
            return "this is raw";
        }
        return "this is corrected";
    }

    private void buildCorrectedBitmap() {
        if (cachedCorrectedBitmap != null) {
            return;
        }
        cachedCorrectedBitmap = BitmapFactory.decodeFile(getImageUri().getPath());
        cachedCorrectedBitmap = Bitmap.createBitmap(
                cachedCorrectedBitmap,
                0,
                0,
                cachedCorrectedBitmap.getWidth(),
                cachedCorrectedBitmap.getHeight(),
                exifOrientation.createTransformationMatrix(),
                true
        );
    }

    private void handleButtonCorrectImageClick() {
        shouldShowOriginal = !shouldShowOriginal;
        renderImageView();
        renderButtonCorrectImage();
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        if (viewId == R.id.button_correct_image) {
            handleButtonCorrectImageClick();
        }
    }
}
