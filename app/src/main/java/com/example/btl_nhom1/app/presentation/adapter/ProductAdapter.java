package com.example.btl_nhom1.app.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_nhom1.R;
import com.example.btl_nhom1.app.domain.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final Context context;
    private List<Product> productList;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public void updateProducts(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set dữ liệu
        holder.tvProductName.setText(product.getName());
        holder.tvPrice.setText(product.getFormattedPrice());
        holder.tvSold.setText("Đã bán: " + product.getTotalQuantitySold());

        String imageName = product.getPrimaryImageUrl();

        if ((imageName == null || imageName.isEmpty()) && product.getImages() != null && !product.getImages().isEmpty()) {
            imageName = product.getImages().get(0).getImageUrl();
        }

        int resId = 0;
        if (imageName != null && !imageName.isEmpty()) {
            resId = context.getResources().getIdentifier(
                    imageName,
                    "drawable",
                    context.getPackageName()
            );
        }

        if (resId != 0) {
            holder.imgProduct.setImageResource(resId);
        } else {
            holder.imgProduct.setImageResource(R.drawable.nullimage);
        }

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Chọn: " + product.getDisplayName(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, iconTopRight;
        TextView tvProductName, tvPrice, tvSold;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            iconTopRight = itemView.findViewById(R.id.icon_top_right);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSold = itemView.findViewById(R.id.tvSold);
        }
    }
}