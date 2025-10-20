package com.example.btl_nhom1.app.presentation.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl_nhom1.R;

public class SortFragment extends Fragment {

    private ImageView btnClose;
    private TextView btnReset, btnApply;
    private LinearLayout option1, option2, option3;
    private int selectedOption = 1; // Mặc định chọn option 1

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sort, container, false);

        // Khởi tạo views
        btnClose = view.findViewById(R.id.btnClose);
        btnReset = view.findViewById(R.id.btnReset);
        btnApply = view.findViewById(R.id.btnApply);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);

        // Set click listener cho nút đóng
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        // Set click listeners cho các options
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOption(1);
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOption(2);
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOption(3);
            }
        });

        // Set click listener cho nút Đặt lại
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSort();
            }
        });

        // Set click listener cho nút Áp dụng
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applySort();
            }
        });

        return view;
    }

    private void selectOption(int option) {
        selectedOption = option;

        // Reset tất cả options
        option1.setBackgroundResource(R.drawable.bg_option_normal);
        option2.setBackgroundResource(R.drawable.bg_option_normal);
        option3.setBackgroundResource(R.drawable.bg_option_normal);

        // Set background cho option được chọn
        switch (option) {
            case 1:
                option1.setBackgroundResource(R.drawable.bg_option_selected);
                break;
            case 2:
                option2.setBackgroundResource(R.drawable.bg_option_selected);
                break;
            case 3:
                option3.setBackgroundResource(R.drawable.bg_option_selected);
                break;
        }
    }

    private void closeFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void resetSort() {
        // Reset về option 1 (mặc định)
        selectOption(1);
    }

    private void applySort() {
        // Logic để áp dụng sắp xếp
        // TODO: Implement apply logic based on selectedOption
        closeFragment();
    }
}