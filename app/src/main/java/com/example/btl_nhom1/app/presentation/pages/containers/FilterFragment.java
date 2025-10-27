package com.example.btl_nhom1.app.presentation.pages.containers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

public class FilterFragment extends Fragment {
    private ImageView btnClose;
    private TextView btnReset, btnApply;
    private RelativeLayout goldTypeDropdown, priceDropdown;
    private TextView goldTypeSelectedText, priceSelectedText;
    private String selectedGoldType = null;
    private Integer selectedFromPrice = null;
    private Integer selectedToPrice = null;
    private List<String> goldTypesList = new ArrayList<>();
    private ProductRepository repository;
    private OnFilterAppliedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFilterAppliedListener) {
            listener = (OnFilterAppliedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnFilterAppliedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        repository = new ProductRepository(getContext());

        // Khởi tạo views
        btnClose = view.findViewById(R.id.btnClose);
        btnReset = view.findViewById(R.id.btnReset);
        btnApply = view.findViewById(R.id.btnApply);

        goldTypeDropdown = view.findViewById(R.id.goldTypeDropdown);
        priceDropdown = view.findViewById(R.id.priceDropdown);

        goldTypeSelectedText = view.findViewById(R.id.goldTypeSelectedText);
        priceSelectedText = view.findViewById(R.id.priceSelectedText);

        // Load danh sách loại vàng từ API
        loadGoldTypes();

        // Click listeners
        btnClose.setOnClickListener(v -> closeFragment());
        goldTypeDropdown.setOnClickListener(v -> showGoldTypeDropdown(v));
        priceDropdown.setOnClickListener(v -> showPriceDropdown(v));
        btnReset.setOnClickListener(v -> resetFilters());
        btnApply.setOnClickListener(v -> applyFilters());

        return view;
    }

    private void loadGoldTypes() {
        repository.fetchGoldTypes(new ProductRepository.GoldTypesCallback() {
            @Override
            public void onSuccess(List<String> goldTypes) {
                goldTypesList = goldTypes;
                Log.i("FilterFragment", "Loaded " + goldTypes.size() + " gold types");
                for (String type : goldTypes) {
                    Log.d("FilterFragment", "  • " + type);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("FilterFragment", "Lỗi load gold types: " + errorMessage);
                Toast.makeText(getContext(), "Lỗi tải loại vàng: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showGoldTypeDropdown(View anchor) {
        if (goldTypesList.isEmpty()) {
            Toast.makeText(getContext(), "Đang tải danh sách loại vàng...", Toast.LENGTH_SHORT).show();
            return;
        }

        PopupMenu popup = new PopupMenu(getContext(), anchor);

        for (String goldType : goldTypesList) {
            popup.getMenu().add(formatGoldType(goldType));
        }

        popup.setOnMenuItemClickListener(item -> {
            String displayText = item.getTitle().toString();
            selectedGoldType = getOriginalGoldType(displayText);

            goldTypeSelectedText.setText(displayText);
            goldTypeSelectedText.setTextColor(getResources().getColor(android.R.color.black));

            Log.d("FilterFragment", "Selected gold type: " + selectedGoldType);
            return true;
        });

        popup.show();
    }

    private void showPriceDropdown(View anchor) {
        PopupMenu popup = new PopupMenu(getContext(), anchor);

        // Các khoảng giá
        popup.getMenu().add("Dưới 2,000,000₫");
        popup.getMenu().add("2,000,000₫ - 5,000,000₫");
        popup.getMenu().add("5,000,000₫ - 10,000,000₫");
        popup.getMenu().add("Trên 10,000,000₫");

        popup.setOnMenuItemClickListener(item -> {
            String priceRange = item.getTitle().toString();
            priceSelectedText.setText(priceRange);
            priceSelectedText.setTextColor(getResources().getColor(android.R.color.black));

            // Parse khoảng giá
            parsePriceRange(priceRange);

            Log.d("FilterFragment", "Selected price: " + selectedFromPrice + " - " + selectedToPrice);
            return true;
        });

        popup.show();
    }

    private String formatGoldType(String goldType) {
        switch (goldType) {
            case "GOLD_10K":
                return "Vàng 10K";
            case "GOLD_14K":
                return "Vàng 14K";
            case "GOLD_18K":
                return "Vàng 18K";
            case "GOLD_24K":
                return "Vàng 24K";
            default:
                return goldType;
        }
    }

    private String getOriginalGoldType(String displayText) {
        switch (displayText) {
            case "Vàng 10K":
                return "GOLD_10K";
            case "Vàng 14K":
                return "GOLD_14K";
            case "Vàng 18K":
                return "GOLD_18K";
            case "Vàng 24K":
                return "GOLD_24K";
            default:
                return displayText;
        }
    }

    private void parsePriceRange(String priceRange) {
        if (priceRange.equals("Dưới 2,000,000₫")) {
            selectedFromPrice = 0;
            selectedToPrice = 2000000;
        } else if (priceRange.equals("2,000,000₫ - 5,000,000₫")) {
            selectedFromPrice = 2000000;
            selectedToPrice = 5000000;
        } else if (priceRange.equals("5,000,000₫ - 10,000,000₫")) {
            selectedFromPrice = 5000000;
            selectedToPrice = 10000000;
        } else if (priceRange.equals("Trên 10,000,000₫")) {
            selectedFromPrice = 10000000;
            selectedToPrice = null; // Không giới hạn trên
        }
    }

    private void closeFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void resetFilters() {
        selectedGoldType = null;
        selectedFromPrice = null;
        selectedToPrice = null;

        goldTypeSelectedText.setText("Chọn loại vàng");
        goldTypeSelectedText.setTextColor(getResources().getColor(android.R.color.darker_gray));

        priceSelectedText.setText("Chọn khoảng giá");
        priceSelectedText.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Thông báo về Activity
        if (listener != null) {
            listener.onFilterReset();
        }

        Toast.makeText(getContext(), "Đã đặt lại bộ lọc", Toast.LENGTH_SHORT).show();
    }

    private void applyFilters() {
        if (selectedGoldType == null && selectedFromPrice == null) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất 1 bộ lọc", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truyền data về Activity
        if (listener != null) {
            listener.onFilterApplied(selectedGoldType, selectedFromPrice, selectedToPrice);
        }

        String filterInfo = "";
        if (selectedGoldType != null) {
            filterInfo += formatGoldType(selectedGoldType);
        }
        if (selectedFromPrice != null) {
            if (!filterInfo.isEmpty()) filterInfo += ", ";
            filterInfo += priceSelectedText.getText().toString();
        }

        Toast.makeText(getContext(), "Đã áp dụng: " + filterInfo, Toast.LENGTH_SHORT).show();
        closeFragment();
    }

    // Interface để truyền data về Activity
    public interface OnFilterAppliedListener {
        void onFilterApplied(String goldType, Integer fromPrice, Integer toPrice);

        void onFilterReset();
    }
}