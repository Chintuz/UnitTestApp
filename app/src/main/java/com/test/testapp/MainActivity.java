package com.test.testapp;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import com.test.testapp.databinding.ActivityMainBinding;
import com.test.testapp.model.Data;
import com.test.testapp.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Uri photoURI;
    private File photoFile;

    private static final int ACTION_REQUEST_CAMERA = 100;
    private static final int ACTION_REQUEST_GALLERY = 200;
    public static ImageView imageView;
    private MainViewModel viewModel;
    private ActivityMainBinding binding;
    private MyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getData();

        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        viewModel.liveData.observe(this, list -> {
            if (list != null && list.size() > 0) {
                adapter = new MyAdapter(list, MainActivity.this, viewModel);
                binding.setMyAdapter(adapter);
            }
        });

        viewModel.chhoseImage.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.chhoseImage.get()) {
                    requestSmsPermission();
                }
                viewModel.chhoseImage.set(false);
            }
        });

        viewModel.notifyList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                try {
                    if (viewModel.notifyList.get() && adapter != null) {
                        adapter.notifyDataSetChanged();
//                        binding.invalidateAll();
                    }
                    viewModel.notifyList.set(false);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    public static final int PERMISSION_STORAGE = 99;

    public void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
        } else {
            fetchImage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                printLogs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void printLogs() {
        if (viewModel.liveData.getValue() != null && viewModel.liveData.getValue().size() > 0) {
            List<Data> list = viewModel.liveData.getValue();
            Toast.makeText(MainActivity.this, "Please check logs..", Toast.LENGTH_LONG).show();

            Logger.v("Here is the required content of all the items..");

            for (Data data : list) {

                Logger.v("id - " + data.getId());

                if (data.getType().equalsIgnoreCase("PHOTO")) {
                    Logger.v(data.getBitmap() != null ? "Image Captured" : "Image not captured");

                } else if (data.getType().equalsIgnoreCase("SINGLE_CHOICE")) {
                    Logger.v(data.getSelectedChoice() != null ? data.getSelectedChoice() : "");

                } else if (data.getType().equalsIgnoreCase("COMMENT")) {
                    Logger.v(data.getComment() != null ? data.getComment() : "");

                }
                Logger.v("    ");
            }


        } else {
            Toast.makeText(MainActivity.this, "There is no data in list, please add some and check!!", Toast.LENGTH_LONG).show();
            Logger.v("There is no data in list, please add some and check!!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchImage();
                } else {
                    // permission denied
                    Toast.makeText(this, "Permission denied, Please try again or enable from app settings.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case ACTION_REQUEST_GALLERY:
                    if (viewModel.selectedModel != null) {
                        try {
                            viewModel.selectedModel.getValue().setBitmap(MediaStore.Images.Media.getBitmap(
                                    MainActivity.this.getContentResolver(), data.getData()));
                            binding.getMyAdapter().notifyDataSetChanged();
//                            binding.invalidateAll();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    break;

                case ACTION_REQUEST_CAMERA:
                    try {
                        Bitmap bitmap = getImageBitmap(photoFile.getPath(), BitmapFactory.decodeFile(photoFile.getPath()));
                        if (viewModel.selectedModel.getValue() != null) {
                            try {
                                viewModel.selectedModel.getValue().setBitmap(bitmap);
                                binding.getMyAdapter().notifyDataSetChanged();
//                                binding.invalidateAll();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (viewModel.showImage.get()){
            viewModel.showImage.set(false);
        } else {
            super.onBackPressed();
        }
    }

    private void fetchImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:

                            // GET IMAGE FROM THE GALLERY
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");

                            Intent chooser = Intent.createChooser(intent, "Choose a Picture");
                            startActivityForResult(chooser, ACTION_REQUEST_GALLERY);
                            break;

                        case 1:
                            // GET IMAGE FROM THE CAMERA

                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                try {

                                    File cameraFolder;

                                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                                        cameraFolder = new File(Environment.getExternalStorageDirectory(), "test_app/");
                                    else
                                        cameraFolder = getCacheDir();
                                    if (!cameraFolder.exists())
                                        cameraFolder.mkdirs();

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
                                    String timeStamp = dateFormat.format(new Date());
                                    String imageFileName = "picture_" + timeStamp + ".jpg";

                                    photoFile = new File(Environment.getExternalStorageDirectory(), "test_app/" + imageFileName);

                                    photoURI = FileProvider.getUriForFile(this, getString(R.string.file_provider_authority), photoFile);

                                } catch (Exception ex) {
                                    Log.e("TakePicture", ex.getMessage());
                                }
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, ACTION_REQUEST_CAMERA);
                            }
                            break;
                        default:
                            break;
                    }
                });

        builder.show();

    }

    private Bitmap getImageBitmap(String photoPath, Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
