package com.example.btl_nhom1.app.presentation.common;


import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.btl_nhom1.R;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomPagination {
    private Context context;
    private LinearLayout paginationContainer;
    private List<?> allItems;
    private int itemsPerPage;
    private int currentPage = 1;
    private int totalPages;
    private PaginationListener listener;

    public CustomPagination(Context context, LinearLayout paginationContainer, List<?> allItems, int itemsPerPage, PaginationListener listener) {
        this.context = context;
        this.paginationContainer = paginationContainer;
        this.allItems = allItems;
        this.itemsPerPage = itemsPerPage;
        this.listener = listener;
    }

    private void calculateTotalPages() {
        totalPages = (int) Math.ceil((double) allItems.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;
    }

    public void initialize() {
        calculateTotalPages();
        createPaginationButtons();
        loadPage(1);
    }

    private void createPaginationButtons() {
        paginationContainer.removeAllViews();

        // Nút Previous
        Button btnPrev = createButton("‹", v -> {
            if (currentPage > 1) {
                loadPage(currentPage - 1);
            }
        });
        paginationContainer.addView(btnPrev);

        // Nút số trang
        for (int i = 1; i <= totalPages; i++) {
            int pageNum = i;
            Button btnPage = createButton(String.valueOf(i), v -> loadPage(pageNum));
            btnPage.setTag(i);
            paginationContainer.addView(btnPage);
        }

        // Nút Next
        Button btnNext = createButton("›", v -> {
            if (currentPage < totalPages) {
                loadPage(currentPage + 1);
            }
        });
        paginationContainer.addView(btnNext);
    }

    private void loadPage(int page) {
        currentPage = page;

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allItems.size());

        List<?> pageItems = allItems.subList(startIndex, endIndex);

        updatePaginationUI();

        if (listener != null) {
            listener.onPageChanged(pageItems, page);
        }
    }

    private void updatePaginationUI() {
        for (int i = 0; i < paginationContainer.getChildCount(); i++) {
            Button btn = (Button) paginationContainer.getChildAt(i);
            Object tag = btn.getTag();

            if (tag != null && (int) tag == currentPage) {
                btn.setBackgroundResource(R.drawable.pagination_active_bg);
                btn.setTextColor(Color.WHITE);
            } else if (tag != null) {
                btn.setBackgroundResource(R.drawable.pagination_button_bg);
                btn.setTextColor(Color.parseColor("#666666"));
            }
        }
    }

    private Button createButton(String text, android.view.View.OnClickListener listener) {
        Button button = new Button(context);
        button.setText(text);
        button.setTextSize(14);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
        );
        params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        button.setLayoutParams(params);
        button.setOnClickListener(listener);
        button.setBackgroundResource(R.drawable.pagination_button_bg);
        button.setTextColor(Color.parseColor("#666666"));

        return button;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    // Phương thức công khai
    public void updateItems(List<?> newItems) {
        this.allItems = newItems;
        calculateTotalPages();
        createPaginationButtons();
        loadPage(1);
    }

    public interface PaginationListener {
        void onPageChanged(List<?> pageItems, int pageNumber);
    }
}