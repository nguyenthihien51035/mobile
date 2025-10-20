package com.example.btl_nhom1.app.presentation.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl_nhom1.R;

public class FilterFragment extends Fragment {

    private ImageView btnClose;
    private TextView btnReset, btnApply;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        // Khởi tạo views
        btnClose = view.findViewById(R.id.btnClose);
        btnReset = view.findViewById(R.id.btnReset);
        btnApply = view.findViewById(R.id.btnApply);

        // Set click listener cho nút đóng
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        // Set click listener cho nút Đặt lại
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý đặt lại bộ lọc
                resetFilters();
            }
        });

        // Set click listener cho nút Áp dụng
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý áp dụng bộ lọc
                applyFilters();
            }
        });

        return view;
    }

    private void closeFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void resetFilters() {
        // Logic để reset các bộ lọc về mặc định
        // TODO: Implement reset logic
    }

    private void applyFilters() {
        // Logic để áp dụng bộ lọc và đóng fragment
        // TODO: Implement apply logic
        closeFragment();
    }
}