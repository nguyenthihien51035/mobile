package com.example.btl_nhom1.app.presentation.pages.containers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl_nhom1.R;

public class FilterFragment extends Fragment {

    private ImageView btnClose;
    private TextView btnReset, btnApply;
    private RelativeLayout ageDropdown, priceDropdown;
    private TextView ageSelectedText, priceSelectedText;

    private String selectedAge = "";
    private String selectedPriceRange = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        // Khởi tạo views
        btnClose = view.findViewById(R.id.btnClose);
        btnReset = view.findViewById(R.id.btnReset);
        btnApply = view.findViewById(R.id.btnApply);

        // Khởi tạo dropdown containers
        ageDropdown = view.findViewById(R.id.ageDropdown);
        priceDropdown = view.findViewById(R.id.priceDropdown);

        // Khởi tạo text hiển thị
        ageSelectedText = view.findViewById(R.id.ageSelectedText);
        priceSelectedText = view.findViewById(R.id.priceSelectedText);

        // Set click listener cho nút đóng
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        // Set click listener cho dropdown tuổi vàng
        ageDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAgeDropdown(v);
            }
        });

        // Set click listener cho dropdown giá
        priceDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriceDropdown(v);
            }
        });

        // Set click listener cho nút Đặt lại
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilters();
            }
        });

        // Set click listener cho nút Áp dụng
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilters();
            }
        });

        return view;
    }

    private void showAgeDropdown(View anchor) {
        PopupMenu popup = new PopupMenu(getContext(), anchor);

        // Thêm các options cho tuổi vàng
        popup.getMenu().add("0-3 tháng");
        popup.getMenu().add("3-6 tháng");
        popup.getMenu().add("6-12 tháng");
        popup.getMenu().add("1-2 tuổi");
        popup.getMenu().add("Trên 2 tuổi");

        popup.setOnMenuItemClickListener(item -> {
            selectedAge = item.getTitle().toString();
            ageSelectedText.setText(selectedAge);
            ageSelectedText.setTextColor(getResources().getColor(android.R.color.black));
            return true;
        });

        popup.show();
    }

    private void showPriceDropdown(View anchor) {
        PopupMenu popup = new PopupMenu(getContext(), anchor);

        // Thêm các options cho khoảng giá
        popup.getMenu().add("Dưới 500,000đ");
        popup.getMenu().add("500,000đ - 1,000,000đ");
        popup.getMenu().add("1,000,000đ - 2,000,000đ");
        popup.getMenu().add("2,000,000đ - 5,000,000đ");
        popup.getMenu().add("Trên 5,000,000đ");

        popup.setOnMenuItemClickListener(item -> {
            selectedPriceRange = item.getTitle().toString();
            priceSelectedText.setText(selectedPriceRange);
            priceSelectedText.setTextColor(getResources().getColor(android.R.color.black));
            return true;
        });

        popup.show();
    }

    private void closeFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void resetFilters() {
        // Reset về trạng thái mặc định
        selectedAge = "";
        selectedPriceRange = "";

        ageSelectedText.setText("Chọn tuổi vàng");
        ageSelectedText.setTextColor(getResources().getColor(android.R.color.darker_gray));

        priceSelectedText.setText("Chọn khoảng giá");
        priceSelectedText.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void applyFilters() {
        // Logic để áp dụng bộ lọc
        // TODO: Gửi selectedAge và selectedPriceRange về activity/fragment cha
        // Có thể dùng Interface callback hoặc ViewModel

        closeFragment();
    }
}