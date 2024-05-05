package com.example.massage_parlor_admin.ui.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.massage_parlor_admin.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter implements Filterable {

    private List<String> mIds;
    private List<String> mServiceIds;
    private List<String> mTitles;
    private List<String> mNames;
    private List<String> mSurnames;
    private List<String> mPhones;
    private List<String> mDates;
    private List<String> mTimes;

    private List<String> mFilteredTitles; // Добавьте отфильтрованные заголовки
    private LayoutInflater mInflater;
    private ItemFilter mItemFilter = new ItemFilter();

    public ImageAdapter(Context mContext, List<String> mIds, List<String> mServiceIds, List<String> mTitles, List<String> mnames, List<String> msurnames, List<String> mphones, List<String> mdates, List<String> mtimes) {
        this.mIds = mIds;
        this.mServiceIds = mServiceIds;
        this.mTitles = mTitles;
        this.mNames = mnames;
        this.mSurnames = msurnames;
        this.mPhones = mphones;
        this.mDates = mdates;
        this.mTimes = mtimes;
        mFilteredTitles = new ArrayList<>(mTitles); // Инициализируйте отфильтрованные заголовки
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return mFilteredTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item_layout, parent, false);
        }

        //TextView user_idTextView = convertView.findViewById(R.id.user_id_text_view);
        //TextView service_idTextView = convertView.findViewById(R.id.service_id_text_view);
        TextView titleTextView = convertView.findViewById(R.id.title_text_view);
        TextView nameTextView = convertView.findViewById(R.id.name_text_view);
        TextView surnameTextView = convertView.findViewById(R.id.surname_text_view);
        TextView phoneTextView = convertView.findViewById(R.id.phone_text_view);
        TextView datesTextView = convertView.findViewById(R.id.dates_text_view);
        TextView timesTextView = convertView.findViewById(R.id.times_text_view);


        // Устанавливаем данные для каждого представления
        String title = mFilteredTitles.get(position);
        int originalPosition = mTitles.indexOf(title); // Получаем позицию в оригинальном списке

        //String user_id = mIds.get(originalPosition);
        //String service_id = mServiceIds.get(originalPosition);
        String name = mNames.get(originalPosition);
        String surname = mSurnames.get(originalPosition);
        String phone = mPhones.get(originalPosition);
        String date = mDates.get(originalPosition);
        String time = mTimes.get(originalPosition);



        // Устанавливаем текст для текстовых представлений
        //user_idTextView.setText("Id юзера " + user_id);
        //service_idTextView.setText("Id услуги " + service_id);
        titleTextView.setText("Услуга: " + title);
        nameTextView.setText("Имя записавшегося " + name);
        surnameTextView.setText("Фамилия " + surname);
        phoneTextView.setText("Телефон" + phone);
        datesTextView.setText("Дата записи " + date);
        timesTextView.setText("Время записи " + time);


        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mItemFilter;
    }
    public void removeItem(int position) {
        mIds.remove(position);
        mServiceIds.remove(position);
        mTitles.remove(position);
        mNames.remove(position);
        mSurnames.remove(position);
        mPhones.remove(position);
        mDates.remove(position);
        mTimes.remove(position);
        mFilteredTitles.remove(position); // Remove from the filtered list as well
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }


    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            List<String> filteredList = new ArrayList<>();

            for (String title : mTitles) {
                if (title.toLowerCase().contains(filterString)) {
                    filteredList.add(title);
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredTitles = (List<String>) results.values;
            notifyDataSetChanged();
        }
    }
}
