package com.example.btl_nhom1.app.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl_nhom1.app.domain.model.CategoryItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableCategoryAdapter extends BaseAdapter {
    private final Context context;
    private final List<CategoryItem> categories;
    private final LayoutInflater inflater;
    private final Map<Integer, Boolean> expandedMap = new HashMap<>();

    public ExpandableCategoryAdapter(Context context, List<CategoryItem> categories) {
        this.context = context;
        this.categories = categories;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int count = 0;
        for (int i = 0; i < categories.size(); i++) {
            count++; // danh mục cha
            if (Boolean.TRUE.equals(expandedMap.getOrDefault(i, false)) && categories.get(i).getSubCategories() != null) {
                count += categories.get(i).getSubCategories().size();
            }
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        int index = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (index == position) return categories.get(i);
            index++;
            if (Boolean.TRUE.equals(expandedMap.getOrDefault(i, false))) {
                List<String> subs = categories.get(i).getSubCategories();
                if (subs != null) {
                    for (String sub : subs) {
                        if (index == position) return sub;
                        index++;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int index = 0;
        for (int i = 0; i < categories.size(); i++) {
            CategoryItem cat = categories.get(i);
            if (index == position) {
                // danh mục cha
                View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                TextView text = view.findViewById(android.R.id.text1);
                text.setText(cat.getName());
                text.setTextColor(Color.BLACK);
                text.setPadding(40, 20, 40, 20);
                text.setTextSize(16);
                view.setBackgroundColor(Color.parseColor("#ffffff"));
                int finalI = i;
                view.setOnClickListener(v -> {
                    boolean expanded = Boolean.TRUE.equals(expandedMap.getOrDefault(finalI, false));
                    expandedMap.put(finalI, !expanded);
                    notifyDataSetChanged();
                });
                return view;
            }
            index++;
            if (Boolean.TRUE.equals(expandedMap.getOrDefault(i, false))) {
                List<String> subs = cat.getSubCategories();
                if (subs != null) {
                    for (String sub : subs) {
                        if (index == position) {
                            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                            TextView text = view.findViewById(android.R.id.text1);
                            text.setText(sub);
                            text.setPadding(80, 15, 40, 15);
                            text.setTextColor(Color.DKGRAY);
                            view.setOnClickListener(v -> {
                                Toast.makeText(context, "Chọn: " + sub, Toast.LENGTH_SHORT).show();
                            });
                            return view;
                        }
                        index++;
                    }
                }
            }
        }
        return null;
    }
}
