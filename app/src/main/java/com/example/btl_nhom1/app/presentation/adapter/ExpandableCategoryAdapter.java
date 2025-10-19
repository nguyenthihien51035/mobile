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
    private final List<CategoryItem> categories; // danh sách parent
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
            count++; // parent
            if (Boolean.TRUE.equals(expandedMap.getOrDefault(i, false))) {
                List<CategoryItem> subs = categories.get(i).getChildren();
                if (subs != null) count += subs.size();
            }
        }
        return count;
    }

    /**
     * Trả về object tương ứng: nếu là parent -> CategoryItem (parent)
     * nếu là child -> CategoryItem (child)
     */
    @Override
    public Object getItem(int position) {
        int index = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (index == position) return categories.get(i); // parent
            index++;
            if (Boolean.TRUE.equals(expandedMap.getOrDefault(i, false))) {
                List<CategoryItem> subs = categories.get(i).getChildren();
                if (subs != null) {
                    for (CategoryItem sub : subs) {
                        if (index == position) return sub; // child
                        index++;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // Không có id toàn cục, trả về position
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int index = 0;
        for (int i = 0; i < categories.size(); i++) {
            CategoryItem parentCat = categories.get(i);
            if (index == position) {
                // parent view
                View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                TextView text = view.findViewById(android.R.id.text1);
                text.setText(parentCat.getName() + (hasChildren(parentCat) ? " +" : ""));
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
                List<CategoryItem> subs = parentCat.getChildren();
                if (subs != null) {
                    for (CategoryItem sub : subs) {
                        if (index == position) {
                            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                            TextView text = view.findViewById(android.R.id.text1);
                            text.setText(sub.getName());
                            text.setPadding(80, 15, 40, 15);
                            text.setTextColor(Color.DKGRAY);
                            view.setOnClickListener(v -> {
                                // Ví dụ: show id khi click. Bạn có thể thay bằng callback để Activity xử lý
                                Toast.makeText(context, "Chọn: " + sub.getName() + " (id=" + sub.getId() + ")", Toast.LENGTH_SHORT).show();
                            });
                            return view;
                        }
                        index++;
                    }
                }
            }
        }
        // Không nên tới đây, nhưng trả view rỗng để tránh crash
        View fallback = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        ((TextView) fallback.findViewById(android.R.id.text1)).setText("");
        return fallback;
    }

    private boolean hasChildren(CategoryItem item) {
        return item.getChildren() != null && !item.getChildren().isEmpty();
    }
}
