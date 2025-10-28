package com.example.btl_nhom1.app.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchResultViewHolder> {
    private List<Product> productList = new ArrayList<>();
    private Context context;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public SearchAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_product, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productSold;

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productSold = itemView.findViewById(R.id.productSold);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProductClick(productList.get(position));
                }
            });
        }

        public void bind(Product product) {
            productName.setText(product.getName());

            // Format giá
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            productPrice.setText(formatter.format(product.getPrice()) + " đ");

            // Hiển thị số lượng đã bán
            int sold = product.getSoldQuantity();
            productSold.setText(sold + " đã bán");

            // Load hình ảnh từ drawable
            String imageUrl = product.getPrimaryImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Lấy tên ảnh từ product (ví dụ: "kiengsp94_anh5")
                String imageName = imageUrl;

                // Lấy resource ID từ tên ảnh
                int imageResId = context.getResources().getIdentifier(
                        imageName,
                        "drawable",
                        context.getPackageName()
                );

                // Nếu tìm thấy ảnh trong drawable thì hiển thị, không thì dùng ảnh mặc định
                if (imageResId != 0) {
                    productImage.setImageResource(imageResId);
                } else {
                    productImage.setImageResource(R.drawable.ic_search_empty);
                }
            } else {
                productImage.setImageResource(R.drawable.ic_search_empty);
            }
        }
    }
}
