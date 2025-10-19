package com.example.btl_nhom1.app.presentation.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.CategoryItem;
import com.example.btl_nhom1.app.presentation.adapter.ExpandableCategoryAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomCategoryDrawer extends RelativeLayout {
    private LinearLayout menuPanel;
    private View overlay;
    private ListView listCategory;
    // Chỉnh URL tương ứng PHP endpoint của bạn
    private static final String API_URL = "http://192.168.1.78/api/catetree.php?action=latest";
    private ExpandableCategoryAdapter adapter;
    private RequestQueue requestQueue;

    public CustomCategoryDrawer(Context context) {
        super(context);
        init(context);
    }

    public CustomCategoryDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomCategoryDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_category_drawer, this, true);

        menuPanel = findViewById(R.id.menuPanel);
        overlay = findViewById(R.id.overlayBackground);
        listCategory = findViewById(R.id.listCategory);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuPanel.getLayoutParams();
        params.width = (int) (screenWidth * 0.7);
        menuPanel.setLayoutParams(params);

        requestQueue = Volley.newRequestQueue(context);

        // Adapter placeholder nhỏ để tránh list trống (nếu muốn)
        List<CategoryItem> placeholder = new ArrayList<>();
        placeholder.add(new CategoryItem(0, "Đang tải...", null, null));
        adapter = new ExpandableCategoryAdapter(context, placeholder);
        listCategory.setAdapter(adapter);

        fetchCategoriesFromServer();

        overlay.setOnClickListener(v -> closeDrawer());
    }

    private void fetchCategoriesFromServer() {
        StringRequest request = new StringRequest(Request.Method.GET, API_URL,
                response -> {
                    try {
                        JSONObject root = new JSONObject(response);
                        JSONArray data = root.optJSONArray("data");
                        if (data == null) {
                            post(() -> Toast.makeText(getContext(), "Server không trả data danh mục", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        List<CategoryItem> parents = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject p = data.getJSONObject(i);
                            int pid = p.optInt("id", 0);
                            String pname = p.optString("name", "Không tên");
                            String pbanner = p.optString("bannerUrl", null);

                            JSONArray children = p.optJSONArray("children");
                            List<CategoryItem> childList = null;
                            if (children != null && children.length() > 0) {
                                childList = new ArrayList<>();
                                for (int j = 0; j < children.length(); j++) {
                                    JSONObject c = children.getJSONObject(j);
                                    int cid = c.optInt("id", 0);
                                    String cname = c.optString("name", "Không tên");
                                    String cbanner = c.optString("bannerUrl", null);
                                    // children của child trong JSON có thể rỗng, ta không tiếp tục lặp sâu hơn
                                    childList.add(new CategoryItem(cid, cname, cbanner, null));
                                }
                            }
                            parents.add(new CategoryItem(pid, pname, pbanner, childList));
                        }

                        // Cập nhật adapter trên UI thread
                        post(() -> {
                            adapter = new ExpandableCategoryAdapter(getContext(), parents);
                            listCategory.setAdapter(adapter);
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        post(() -> Toast.makeText(getContext(), "Lỗi parse danh mục", Toast.LENGTH_SHORT).show());
                    }
                },
                error -> {
                    error.printStackTrace();
                    post(() -> Toast.makeText(getContext(), "Không thể kết nối server danh mục", Toast.LENGTH_SHORT).show());
                });

        requestQueue.add(request);
    }

    // Phần mở / đóng drawer giữ nguyên
    public void openDrawer() {
        menuPanel.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        menuPanel.post(() -> {
            menuPanel.setTranslationX(menuPanel.getWidth());
            menuPanel.animate().translationX(0).setDuration(300).start();
        });
    }

    public void closeDrawer() {
        menuPanel.post(() -> {
            menuPanel.animate()
                    .translationX(menuPanel.getWidth())
                    .setDuration(300)
                    .withEndAction(() -> {
                        menuPanel.setVisibility(View.GONE);
                        overlay.setVisibility(View.GONE);
                    })
                    .start();
        });
    }

    public boolean isDrawerOpen() {
        return menuPanel.getVisibility() == View.VISIBLE;
    }

    public boolean handleBackPressed() {
        if (isDrawerOpen()) {
            closeDrawer();
            return true;
        }
        return false;
    }
}
