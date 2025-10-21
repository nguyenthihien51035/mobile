package com.example.btl_nhom1.app.presentation.pages.containers;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.btl_nhom1.R;

public class ContainerActivity extends AppCompatActivity {
    Context context;
    private LinearLayout btnFilter, btnSort;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_containers);

        context = this;

        // Khởi tạo buttons
        btnFilter = findViewById(R.id.btnFilter);
        btnSort = findViewById(R.id.btnSort);
        fragmentContainer = findViewById(R.id.fragment_container);

        // Set click listeners
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new FilterFragment());
            }
        });

        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new SortFragment());
            }
        });
    }

    private void openFragment(Fragment fragment) {
        fragmentContainer.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Set animation trượt từ phải sang trái
        transaction.setCustomAnimations(
                R.anim.slide_in_right,  // enter
                R.anim.slide_out_left,  // exit
                R.anim.slide_in_left,   // popEnter
                R.anim.slide_out_right  // popExit
        );

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    // Thêm một phương thức để ẩn fragmentContainer khi tất cả fragment đã bị đóng
    // hoặc khi popBackStack hoàn tất
    // Đây là một cách để lắng nghe sự kiện popBackStack
    @Override
    protected void onResume() {
        super.onResume();
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    fragmentContainer.setVisibility(View.GONE); // ẨN CONTAINER KHI KHÔNG CÒN FRAGMENT NÀO
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Xóa listener để tránh memory leak nếu không cần thiết
        getSupportFragmentManager().removeOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // Do nothing, just to remove the listener
            }
        });
    }
}
