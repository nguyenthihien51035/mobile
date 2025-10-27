package com.example.btl_nhom1.app.presentation.pages.containers;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl_nhom1.R;

public class SortFragment extends Fragment {

    private ImageView btnClose;
    private TextView btnReset, btnApply;
    private LinearLayout option1, option2, option3;
    private int selectedOption = -1; // -1 = chưa chọn gì

    private OnSortAppliedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSortAppliedListener) {
            listener = (OnSortAppliedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnSortAppliedListener");
        }
    }

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

        // Nút đóng
        btnClose.setOnClickListener(v -> closeFragment());

        // Click listeners cho các options
        option1.setOnClickListener(v -> selectOption(1));
        option2.setOnClickListener(v -> selectOption(2));
        option3.setOnClickListener(v -> selectOption(3));

        // Nút Đặt lại
        btnReset.setOnClickListener(v -> resetSort());

        // Nút Áp dụng
        btnApply.setOnClickListener(v -> applySort());

        return view;
    }

    private void selectOption(int option) {
        selectedOption = option;

        // Reset tất cả options
        resetAllOptions();

        // Set background và selected state cho option được chọn
        switch (option) {
            case 1: // Mới nhất
                option1.setBackgroundResource(R.drawable.bg_option_selected);
                option1.findViewById(R.id.radio1).setSelected(true);
                break;
            case 2: // Giá: Cao đến thấp
                option2.setBackgroundResource(R.drawable.bg_option_selected);
                option2.findViewById(R.id.radio2).setSelected(true);
                break;
            case 3: // Giá: Thấp đến cao
                option3.setBackgroundResource(R.drawable.bg_option_selected);
                option3.findViewById(R.id.radio3).setSelected(true);
                break;
        }
    }

    private void resetAllOptions() {
        option1.setBackgroundResource(R.drawable.bg_option_normal);
        option2.setBackgroundResource(R.drawable.bg_option_normal);
        option3.setBackgroundResource(R.drawable.bg_option_normal);

        option1.findViewById(R.id.radio1).setSelected(false);
        option2.findViewById(R.id.radio2).setSelected(false);
        option3.findViewById(R.id.radio3).setSelected(false);
    }

    private void closeFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void resetSort() {
        selectedOption = -1;
        resetAllOptions();

        // Thông báo về Activity để reset sort
        if (listener != null) {
            listener.onSortReset();
        }

        Toast.makeText(getContext(), "Đã đặt lại sắp xếp", Toast.LENGTH_SHORT).show();
    }

    private void applySort() {
        if (selectedOption == -1) {
            Toast.makeText(getContext(), "Vui lòng chọn kiểu sắp xếp", Toast.LENGTH_SHORT).show();
            return;
        }

        String sortBy = "";
        String sortDirection = "";

        switch (selectedOption) {
            case 1: // Mới nhất
                sortBy = "dateOfEntry";
                sortDirection = "desc";
                break;
            case 2: // Giá: Cao đến thấp
                sortBy = "price";
                sortDirection = "desc";
                break;
            case 3: // Giá: Thấp đến cao
                sortBy = "price";
                sortDirection = "asc";
                break;
        }

        // Truyền data về Activity
        if (listener != null) {
            listener.onSortApplied(sortBy, sortDirection);
        }

        Toast.makeText(getContext(), "Đã áp dụng sắp xếp", Toast.LENGTH_SHORT).show();
        closeFragment();
    }

    public interface OnSortAppliedListener {
        void onSortApplied(String sortBy, String sortDirection);

        void onSortReset();
    }
}