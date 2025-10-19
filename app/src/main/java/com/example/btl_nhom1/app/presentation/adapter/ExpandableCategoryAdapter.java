package com.example.btl_nhom1.app.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableCategoryAdapter extends BaseAdapter {
    private final Context context;
    private final LayoutInflater inflater;
    private final Map<Integer, Boolean> expandedMap = new HashMap<>();
    private List<Category> categories;

    public ExpandableCategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
        this.inflater = LayoutInflater.from(context);
    }

    // Phương thức để cập nhật dữ liệu từ API
    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        this.expandedMap.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = 0;
        for (int i = 0; i < categories.size(); i++) {
            count++; // parent
            if (Boolean.TRUE.equals(expandedMap.getOrDefault(i, false))) {
                List<Category> subs = categories.get(i).getChildren();
                if (subs != null) count += subs.size();
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
                List<Category> subs = categories.get(i).getChildren();
                if (subs != null) {
                    for (Category sub : subs) {
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

    private boolean hasChildren(Category item) {
        return item.getChildren() != null && !item.getChildren().isEmpty();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int index = 0;
        for (int i = 0; i < categories.size(); i++) {
            Category parentCat = categories.get(i);
            if (index == position) {
                // Danh mục cha
                View view = inflater.inflate(R.layout.parent_category_item, parent, false);
                TextView categoryName = view.findViewById(R.id.category_name);
                TextView expandIcon = view.findViewById(R.id.expand_icon);

                boolean isExpanded = Boolean.TRUE.equals(expandedMap.getOrDefault(i, false));
                categoryName.setText(parentCat.getName());

                if (hasChildren(parentCat)) {
                    expandIcon.setVisibility(View.VISIBLE);
                    expandIcon.setText(isExpanded ? "⌄" : "›");
                } else {
                    expandIcon.setVisibility(View.GONE);
                }

                view.setBackgroundColor(Color.parseColor("#ffffff"));

                int finalI = i;
                view.setOnClickListener(v -> {
                    if (hasChildren(parentCat)) {
                        boolean expanded = Boolean.TRUE.equals(expandedMap.getOrDefault(finalI, false));
                        expandedMap.put(finalI, !expanded);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Chọn: " + parentCat.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                return view;
            }
            index++;

            if (Boolean.TRUE.equals(expandedMap.getOrDefault(i, false))) {
                List<Category> subs = parentCat.getChildren();
                if (subs != null) {
                    for (Category sub : subs) {
                        if (index == position) {
                            // Danh mục con
                            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                            TextView text = view.findViewById(android.R.id.text1);
                            text.setText("   " + sub.getName());
                            text.setPadding(80, 15, 40, 15);
                            text.setTextColor(Color.DKGRAY);
                            text.setTextSize(14);
                            view.setBackgroundColor(Color.parseColor("#ffffff"));

                            view.setOnClickListener(v -> {
                                Toast.makeText(context, "Chọn: " + sub.getName(), Toast.LENGTH_SHORT).show();
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