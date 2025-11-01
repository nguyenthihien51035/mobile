package com.example.btl_nhom1.app.presentation.adapter;

import android.content.Context;
import android.util.Log;
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
    private final Context context;
    private final OnProductClickListener listener;
    private List<Product> productList = new ArrayList<>();

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

    public interface OnProductClickListener {
        void onProductClick(Product product);
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
            String displayText = product.getName();
            if (product.getSku() != null && !product.getSku().isEmpty()) {
                displayText += " " + product.getSku();
            }
            productName.setText(displayText);

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            productPrice.setText(formatter.format(product.getPrice()) + " đ");

            int sold = product.getSoldQuantity();
            if (sold > 0) {
                productSold.setText("Đã bán: " + formatter.format(sold));
            } else {
                productSold.setText("");
            }

            loadProductImage(product);
        }

        private void loadProductImage(Product product) {
            String imageUrl = product.getPrimaryImageUrl();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                int imageResId = context.getResources().getIdentifier(
                        imageUrl,
                        "drawable",
                        context.getPackageName()
                );

                if (imageResId != 0) {
                    productImage.setImageResource(imageResId);
                    Log.d("ProductAdapter", "Loaded image: " + imageUrl);
                } else {
                    productImage.setImageResource(R.drawable.ic_search_empty);
                    Log.w("ProductAdapter", "Image not found: " + imageUrl);
                }
            } else {
                productImage.setImageResource(R.drawable.ic_search_empty);
                Log.d("ProductAdapter", "No image URL for product: " + product.getName());
            }
        }
    }
}