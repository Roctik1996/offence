package com.matichuk.offense.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.matichuk.offense.ui.ImagePickerActivity;
import com.matichuk.offense.ui.LoginActivity;
import com.rm.rmswitch.RMSwitch;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_IMAGE;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_MAIL;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_NAME;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_PASS;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_PHONE;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_VISIBLE_NAME;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_VISIBLE_PHONE;

public class SettingFragment extends Fragment {
    private static final int REQUEST_IMAGE = 100;
    private ImageView photo;
    private ImageView backgroundPhoto;
    private SharedPreferences mSettings;
    private AppCompatEditText newPass;
    private AppCompatEditText inputName, inputEmail, inputPhone, inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPhone, inputLayoutPassword, inputLayoutNewPassword;
    private Button save;
    private String name = "", mail = "", phone = "", pass = "";
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference tableUser;
    private RMSwitch visibleEmail,visiblePhone;
    private Button logOut;
    private boolean checkPassUpdate = false, checkProfileUpdate = false;

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        photo = view.findViewById(R.id.photo);
        backgroundPhoto = view.findViewById(R.id.background_photo);

        inputLayoutName = view.findViewById(R.id.input_layout_name);
        inputLayoutEmail = view.findViewById(R.id.input_layout_mail);
        inputLayoutPhone = view.findViewById(R.id.input_layout_phone);
        inputLayoutPassword = view.findViewById(R.id.input_layout_password);
        inputLayoutNewPassword = view.findViewById(R.id.input_layout_new_password);
        inputName = view.findViewById(R.id.edt_name);
        inputEmail = view.findViewById(R.id.edt_mail);
        inputPhone = view.findViewById(R.id.edt_phone);
        inputPassword = view.findViewById(R.id.edt_pass);
        newPass = view.findViewById(R.id.edt_new_pass);
        logOut = view.findViewById(R.id.log_out);
        save = view.findViewById(R.id.btn_save);

        visibleEmail = view.findViewById(R.id.show_mail);
        visiblePhone = view.findViewById(R.id.show_number);

        mSettings = Objects.requireNonNull(this.getActivity()).getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(APP_PREFERENCES_NAME)) {
            name = mSettings.getString(APP_PREFERENCES_NAME, "");
            inputName.setText(name);
        }

        if (mSettings.contains(APP_PREFERENCES_VISIBLE_PHONE)) {
            if ( mSettings.getString(APP_PREFERENCES_VISIBLE_PHONE, "").equals("true"))
                visiblePhone.setChecked(true);
            else
                visiblePhone.setChecked(false);
        }

        if (mSettings.contains(APP_PREFERENCES_VISIBLE_NAME)) {
            if ( mSettings.getString(APP_PREFERENCES_VISIBLE_NAME, "").equals("true"))
                visibleEmail.setChecked(true);
            else
                visibleEmail.setChecked(false);
        }


        if (mSettings.contains(APP_PREFERENCES_MAIL)) {
            mail = mSettings.getString(APP_PREFERENCES_MAIL, "");
            inputEmail.setText(mail);

        }

        if (mSettings.contains(APP_PREFERENCES_PHONE)) {
            phone = mSettings.getString(APP_PREFERENCES_PHONE, "");
            inputPhone.setText(phone);

        }

        if (mSettings.contains(APP_PREFERENCES_PASS)) {
            pass = mSettings.getString(APP_PREFERENCES_PASS, "");
        }

        if (mSettings.contains(APP_PREFERENCES_IMAGE)) {
            Glide.with(this).load(mSettings.getString(APP_PREFERENCES_IMAGE, ""))
                    .into(photo);
            Glide.with(this).load(mSettings.getString(APP_PREFERENCES_IMAGE, ""))
                    .into(backgroundPhoto);
            photo.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), android.R.color.transparent));
        }

        visibleEmail.addSwitchObserver((switchView, isChecked) -> {
            if (isChecked) {
                tableUser = FirebaseDatabase.getInstance().getReference("User").child(phone);
                HashMap<String, Object> map = new HashMap<>();
                map.put("visibleEmail", "true");
                tableUser.updateChildren(map);
            } else {
                tableUser = FirebaseDatabase.getInstance().getReference("User").child(phone);
                HashMap<String, Object> map = new HashMap<>();
                map.put("visibleEmail", "false");
                tableUser.updateChildren(map);
            }
        });

        visiblePhone.addSwitchObserver((switchView, isChecked) -> {
            if (isChecked) {
                tableUser = FirebaseDatabase.getInstance().getReference("User").child(phone);
                HashMap<String, Object> map = new HashMap<>();
                map.put("visiblePhone", "true");
                tableUser.updateChildren(map);
            } else {
                tableUser = FirebaseDatabase.getInstance().getReference("User").child(phone);
                HashMap<String, Object> map = new HashMap<>();
                map.put("visiblePhone", "true");
                tableUser.updateChildren(map);
            }
        });

        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString();
                visibleSave();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mail = s.toString();
                visibleSave();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                visibleSave();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        newPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                visibleSave();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        save.setOnClickListener(v -> {
            if (checkPassUpdate && checkProfileUpdate) {
                tableUser = FirebaseDatabase.getInstance().getReference("User").child(phone);
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("email", mail);
                map.put("password", newPass.getText().toString());
                tableUser.updateChildren(map);
            }
            if (checkProfileUpdate && !checkPassUpdate) {
                tableUser = FirebaseDatabase.getInstance().getReference("User").child(phone);
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("email", mail);
                tableUser.updateChildren(map);
            }
            if (!checkProfileUpdate && checkPassUpdate) {
                tableUser = FirebaseDatabase.getInstance().getReference("User").child(phone);
                HashMap<String, Object> map = new HashMap<>();
                map.put("password", newPass.getText().toString());
                tableUser.updateChildren(map);
            }
        });

        logOut.setOnClickListener(view1 -> {
            Intent logout = new Intent(getContext(), LoginActivity.class);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREFERENCES_PHONE, "");
            editor.putString(APP_PREFERENCES_PASS, "");
            editor.apply();
            startActivity(logout);
            getActivity().finish();
        });

        database = FirebaseDatabase.getInstance();
        tableUser = database.getReference("User").child(phone);
        storageReference = FirebaseStorage.getInstance().getReference("user_photo");


        photo.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), android.R.color.transparent));

        photo.setOnClickListener(v -> onProfileImageClick());

        return view;
    }

    private void visibleSave() {
        if (name.equals(mSettings.getString(APP_PREFERENCES_NAME, "")) &&
                mail.equals(mSettings.getString(APP_PREFERENCES_MAIL, ""))) {
            save.setVisibility(View.INVISIBLE);
            checkProfileUpdate = false;
        } else {
            checkProfileUpdate = true;
            save.setVisibility(View.VISIBLE);
        }

        if (inputPassword.getText().toString().equals("") && newPass.getText().toString().equals("") && !checkProfileUpdate) {
            checkPassUpdate = false;
            save.setVisibility(View.INVISIBLE);
        } else if (!inputPassword.getText().toString().equals("") && !inputPassword.getText().toString().equals("")) {
            checkPassUpdate = true;
            save.setVisibility(View.VISIBLE);
        }
    }

    private void onProfileImageClick() {
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
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), uri);

                    // loading profile image from local cache
                    assert uri != null;
                    loadProfile(uri.toString());
                    updatePhoto(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updatePhoto(Uri uri) {
        if (uri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            uploadTask = fileReference.putFile(uri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful())
                    throw task.getException();
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(APP_PREFERENCES_IMAGE, mUri);
                    editor.apply();
                    tableUser = FirebaseDatabase.getInstance().getReference("User").child(phone);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("image", mUri);
                    tableUser.updateChildren(map);
                } else
                    Toast.makeText(getContext(), "Не вдалося завантажити фото", Toast.LENGTH_LONG).show();
            });
        }
    }

    private void loadProfile(String url) {
        Glide.with(this).load(url)
                .into(photo);
        Glide.with(this).load(url)
                .into(backgroundPhoto);
        photo.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), android.R.color.transparent));
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Необхідні дозволи");
        builder.setMessage("Для коректної роботи додатку надайте всі необхідні дозволи");
        builder.setPositiveButton("Налаштування", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Відміна", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Objects.requireNonNull(getActivity()).getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
