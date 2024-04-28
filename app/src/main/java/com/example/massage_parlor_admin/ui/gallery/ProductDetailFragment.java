package com.example.massage_parlor_admin.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.massage_parlor_admin.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailFragment extends BottomSheetDialogFragment {

    private String user_id;
    private String service_id;
    private String title;
    private String name;
    private String surname;
    private String phone;
    private Context mContext;
    private ImageAdapter adapter;
    private int position;

    public ProductDetailFragment(Context context, String userId, String serviceId, String titles, String names, String surnames, String phones) {
        user_id = userId;
        service_id = serviceId;
        this.title = titles;
        this.name = names;
        this.surname = surnames;
        this.phone = phones;
        this.mContext = context;
    }

    public void setAdapter(ImageAdapter adapter) {
        this.adapter = adapter;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Заполнение полей информацией о товаре
        TextView user_idTextView = view.findViewById(R.id.user_id);
        TextView service_idTextView = view.findViewById(R.id.service_id);
        TextView titleTextView = view.findViewById(R.id.title);
        TextView nameTextView = view.findViewById(R.id.name);
        TextView surnameTextView = view.findViewById(R.id.surname);
        TextView phoneTextView = view.findViewById(R.id.phone);
        Button send_data  = view.findViewById(R.id.button_appointment);


        user_idTextView.setText(user_id);
        service_idTextView.setText(service_id);
        titleTextView.setText(title);
        nameTextView.setText(name);
        surnameTextView.setText(surname);
        phoneTextView.setText(phone);

        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("service_id", service_id);

        send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpRequestTask(mContext, "https://claimbes.store/massage_parlor/api/add_application/delete.php", params).execute();
                dismiss();
                adapter.removeItem(position);
            }
        });

        return view;
    }

}

