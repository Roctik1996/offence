package com.matichuk.offense.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.matichuk.offense.R;
import com.matichuk.offense.model.CarData;
import com.matichuk.offense.model.Offense;
import com.matichuk.offense.service.MySingleton;
import com.matichuk.offense.ui.ImagePickerActivity;
import com.matichuk.offense.ui.PriceActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.ganfra.materialspinner.MaterialSpinner;
import timber.log.Timber;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_PHONE;
import static com.matichuk.offense.utils.Const.FCM_API;
import static com.matichuk.offense.utils.Const.contentType;
import static com.matichuk.offense.utils.Const.serverKey;

public class ScanFragment extends Fragment {
    private static final int REQUEST_IMAGE = 100;
    private SurfaceView cameraView;
    private EditText txtScan;
    private CameraSource cameraSource;
    private final int RequestCameraPermissionID = 1001;
    private ProgressBar progressBar;
    private FrameLayout frame;
    private TextView title, year,color, reg,fuel,kind,body,actions, emptyData;
    private Button btnReport, btnAbort,btnPrice;
    private AlertDialog dialog;
    private SharedPreferences mSettings;
    private Uri uri;
    private ImageView photo;
    private FirebaseDatabase database;
    private DatabaseReference tableOffense;
    private StorageReference storageReference;
    private DatabaseReference tableCarData;
    private String key;
    private String NOTIFICATION_TITLE;
    private String NOTIFICATION_MESSAGE;
    private String TOPIC;
    private String carTitle="";

    public ScanFragment() {
    }

    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        cameraView = view.findViewById(R.id.scanner);
        txtScan = view.findViewById(R.id.txt_scan);
        progressBar = view.findViewById(R.id.progress);
        frame = view.findViewById(R.id.frame);
        title = view.findViewById(R.id.txt_title);
        year = view.findViewById(R.id.txt_year);
        color = view.findViewById(R.id.txt_color);
        fuel = view.findViewById(R.id.txt_fuel);
        body = view.findViewById(R.id.txt_body);
        kind = view.findViewById(R.id.txt_kind);
        reg = view.findViewById(R.id.txt_reg);
        actions = view.findViewById(R.id.txt_actions);
        emptyData = view.findViewById(R.id.txt_empty_data);
        btnReport = view.findViewById(R.id.btn_report);
        btnAbort = view.findViewById(R.id.btn_abort);
        btnPrice = view.findViewById(R.id.btn_price);

        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("offense_photo");

        mSettings = Objects.requireNonNull(this.getActivity()).getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getContext()).build();
        if (!textRecognizer.isOperational()) {
            Timber.tag("MainActivity").w("Detector dependencies are not yet available");
        } else {

            cameraSource = new CameraSource.Builder(getContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {
                        txtScan.post(new Runnable() {
                            @Override
                            public void run() {
                                txtScan.setText("");
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < items.size(); ++i) {
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                }
                                txtScan.setText(stringBuilder.toString().replaceAll("\\s", ""));
                                cameraSource.stop();
                            }
                        });
                    }
                }
            });
        }

        cameraView.setOnClickListener(v -> {
            try {
                cameraSource.start(cameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnAbort.setOnClickListener(v -> {
            txtScan.setText("");
            clearData();
        });

        btnReport.setOnClickListener(v -> showDialog());
        btnPrice.setOnClickListener(v->{
            Intent priceActivity = new Intent(getContext(), PriceActivity.class);
            priceActivity.putExtra("carName",carTitle);
            startActivity(priceActivity);
        });

        txtScan.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (txtScan.getRight() - txtScan.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (!txtScan.getText().toString().equals("")) {
                        clearData();
                        showProgress(true);
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        tableCarData = database.getReference("CarData").child(txtScan.getText().toString().replaceAll("\\s", ""));
                        tableCarData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                CarData carData = dataSnapshot.getValue(CarData.class);
                                setData(carData);
                                showProgress(false);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //getInfo(txtScan.getText().toString());
                        return true;
                    }
                }
            }
            return false;
        });
        clearData();

        return view;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        key= String.valueOf(System.currentTimeMillis());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_dialog, null);

        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        Button btn_positive = dialogView.findViewById(R.id.btn_send_dialog);
        Button btn_negative = dialogView.findViewById(R.id.btn_cancel);
        MaterialSpinner type = dialogView.findViewById(R.id.spinner_type);
        photo = dialogView.findViewById(R.id.photo);
        // Create the alert dialog
        dialog = builder.create();
        dialog.setCancelable(false);
        photo.setOnClickListener(v -> onImageClick());
        btn_positive.setOnClickListener(v -> {
            if (type.getSelectedItemPosition() != 0) {
                updatePhoto(uri,type.getSelectedItem().toString());
                dialog.cancel();
            } else
                Toast.makeText(getContext(), "Оберіть порушення", Toast.LENGTH_LONG).show();
        });

        btn_negative.setOnClickListener(v -> {
            // Dismiss the alert dialog
            txtScan.setText("");
            clearData();
            dialog.cancel();
        });

        dialog.show();
    }

    private void updatePhoto(Uri uri,String type) {
        if (uri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            StorageTask uploadTask = fileReference.putFile(uri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful())
                    throw task.getException();
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();
                    sendReport(type,key,mUri);
                } else
                    Toast.makeText(getContext(), "Не вдалося завантажити фото", Toast.LENGTH_LONG).show();
            });
        }
        else sendReport(type,key,"");
    }

    private void sendReport(String type,String key,String photo) {

        tableOffense = database.getReference("Offense").child(key);
        tableOffense.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Offense offense = new Offense(mSettings.getString(APP_PREFERENCES_PHONE, ""),
                        txtScan.getText().toString().replaceAll("\\s", ""), type,photo);
                tableOffense.setValue(offense);
                showProgress(false);
                Toast.makeText(getContext(), "Успішно відправлено", Toast.LENGTH_LONG).show();

                TOPIC = "/topics/new_offense"; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = "Порушення";
                NOTIFICATION_MESSAGE = "Отримано нове порушення";

                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                }
                sendNotification(notification);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> {
                },
                error -> {

                }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void showProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        frame.setVisibility(isProgress ? View.VISIBLE : View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void setData(CarData carData) {
        if (carData!=null) {
            title.setVisibility(View.VISIBLE);
            year.setVisibility(View.VISIBLE);
            color.setVisibility(View.VISIBLE);
            fuel.setVisibility(View.VISIBLE);
            body.setVisibility(View.VISIBLE);
            kind.setVisibility(View.VISIBLE);
            reg.setVisibility(View.VISIBLE);
            actions.setVisibility(View.VISIBLE);
            title.setText(carData.getBRAND()+" "+carData.getMODEL());
            carTitle=carData.getBRAND().replaceAll("\\s", "-")+"/"+carData.getMODEL();
            year.setText("Рік випуску: "+ carData.getMAKE_YEAR());
            color.setText("Колір: "+ carData.getCOLOR());
            fuel.setText("Паливо: "+ carData.getFUEL());
            kind.setText("Тип: "+ carData.getKIND());
            body.setText("Кузов: "+ carData.getBODY());
            reg.setText("Дата реєстрації: "+ carData.getD_REG());
            actions.setText("Дії: " + carData.getOPER_NAME());
            btnReport.setVisibility(View.VISIBLE);
            btnAbort.setVisibility(View.VISIBLE);
            btnPrice.setVisibility(View.VISIBLE);
        } else {
            clearData();
            emptyData.setVisibility(View.VISIBLE);
            btnReport.setVisibility(View.VISIBLE);
            btnAbort.setVisibility(View.VISIBLE);
            btnPrice.setVisibility(View.GONE);
        }
    }

    private void clearData() {
        title.setVisibility(View.INVISIBLE);
        year.setVisibility(View.INVISIBLE);
        color.setVisibility(View.INVISIBLE);
        fuel.setVisibility(View.INVISIBLE);
        body.setVisibility(View.INVISIBLE);
        kind.setVisibility(View.INVISIBLE);
        reg.setVisibility(View.INVISIBLE);
        actions.setVisibility(View.INVISIBLE);
        emptyData.setVisibility(View.INVISIBLE);
        btnReport.setVisibility(View.INVISIBLE);
        btnAbort.setVisibility(View.INVISIBLE);
        btnPrice.setVisibility(View.INVISIBLE);
    }

    private void loadPhoto(String url) {
        Glide.with(this).load(url)
                .into(photo);
        photo.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), android.R.color.transparent));
    }

    private void showSettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Необхідні дозволи");
        builder.setMessage("Для коректної роботи додатку надайте всі необхідні дозволи");
        builder.setPositiveButton("Налаштування", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Відміна", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void onImageClick() {
        Dexter.withActivity(getActivity())
                .withPermissions(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        } else {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getContext(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), uri);

                    // loading profile image from local cache
                    assert uri != null;
                    loadPhoto(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Objects.requireNonNull(getActivity()).getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestCameraPermissionID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
