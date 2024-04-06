package com.example.massage_parlor_admin.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.massage_parlor_admin.databinding.FragmentHomeBinding;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;

    private Uri selectedImageUri;

private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

    binding = FragmentHomeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

    final TextView title = binding.title;
    final TextView description = binding.description;
    final TextView price = binding.textPrice;
    final Button send = binding.send;
    final Button selectPhoto = binding.selectPhoto;
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Проверяем, было ли разрешение отклонено ранее с выбором "Не спрашивать снова"
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Запрашиваем разрешение
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSION);
                    } else {
                        // Показываем диалоговое окно с объяснением
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Необходимо разрешение")
                                .setMessage("Для загрузки изображений из галереи необходимо предоставить разрешение на чтение внешнего хранилища!")
                                .setPositiveButton("Перейти в настройки", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Перенаправляем пользователя в настройки приложения
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Отмена", null)
                                .show();
                    }
                } else {
                    openGallery();
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titles = title.getText().toString();
                String descriptions = description.getText().toString();
                String prices = price.getText().toString();
                new HttpRequestTask().execute(titles, descriptions, prices);
            }
        });
        return root;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ImageView imageView = binding.imageView2;
            imageView.setImageURI(selectedImageUri);
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String realPath;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            realPath = cursor.getString(column_index);
            cursor.close();
            return realPath;
        } else {
            return uri.getPath(); // Возвращаем исходный путь, если не удалось получить реальный путь
        }
    }



@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private class HttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            String description = params[1];
            String price = params[2]; // Get category from params

            try {
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("title", title)
                        .addFormDataPart("description", description)
                        .addFormDataPart("price", price);

                if (selectedImageUri != null) {
                    String filePath = getRealPathFromUri(selectedImageUri);
                    File file = new File(filePath);
                    builder.addFormDataPart("photo", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                }

                RequestBody requestBody = builder.build();
                Request request = new Request.Builder()
                        .url("https://claimbes.store/massage_parlor/admin_api/add.php")
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(getContext(), "Ошибка: " + result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Данные успешно записаны", Toast.LENGTH_SHORT).show();
            }
        }
    }
}