package com.example.massage_parlor_admin.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.massage_parlor_admin.databinding.FragmentGalleryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GalleryFragment extends Fragment {

private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

    binding = FragmentGalleryBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

    getPhotoUrlsFromServer();
        return root;
    }

    private void addItemsToList(JSONArray jsonArray, List<String> list) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String item = jsonArray.getString(i);
                list.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPhotoUrlsFromServer() {
        String url = "https://claimbes.store/massage_parlor/api/add_application/return.php"; // Замените на ваш URL-адрес сервера

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray user_idArray = jsonObject.getJSONArray("user_id");
                        JSONArray service_idArray = jsonObject.getJSONArray("id_service");
                        JSONArray titleArray = jsonObject.getJSONArray("title");
                        JSONArray nameArray = jsonObject.getJSONArray("name");
                        JSONArray surnameArray = jsonObject.getJSONArray("surname");
                        JSONArray phoneArray = jsonObject.getJSONArray("phone");

                        Log.d("erf", "ferf" + titleArray);

                        List<String> ids = new ArrayList<>();
                        List<String> service_ids = new ArrayList<>();
                        List<String> titles = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        List<String> surnames = new ArrayList<>();
                        List<String> phones = new ArrayList<>();


                        addItemsToList(user_idArray, ids);
                        addItemsToList(service_idArray, service_ids);
                        addItemsToList(titleArray, titles);
                        addItemsToList(nameArray, names);
                        addItemsToList(surnameArray, surnames);
                        addItemsToList(phoneArray, phones);

                        displayPhotosInGrid(ids, service_ids, titles, names, surnames, phones);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void displayPhotosInGrid(List<String> ids, List<String> service_ids, List<String> titles, List<String> names, List<String> surnames, List<String> phones) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                GridView gridView = binding.gridView;
                ImageAdapter adapter = new ImageAdapter(getContext(), ids , service_ids, titles, names, surnames, phones);
                gridView.setAdapter(adapter);
                SearchView searchView = binding.searchView;

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return true;
                    }

                });

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Получение выбранного товара
                        String selectedUserId = ids.get(position);
                        String selectedServiceId = service_ids.get(position);
                        String selectedTitle = titles.get(position);
                        String selectedNames = names.get(position);
                        String selectedSurnames = surnames.get(position);
                        String selectedPhones = phones.get(position);

                        // Создание экземпляра ProductDetailFragment и его отображение
                        ProductDetailFragment detailFragment = new ProductDetailFragment(getContext(), selectedUserId, selectedServiceId, selectedTitle, selectedNames, selectedSurnames, selectedPhones);
                        // Pass the adapter to the ProductDetailFragment
                        detailFragment.setAdapter(adapter);
                        detailFragment.setPosition(position); // Set the position

                        // Show the ProductDetailFragment
                        detailFragment.show(getFragmentManager(), "product_detail");

                    }
                });

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}