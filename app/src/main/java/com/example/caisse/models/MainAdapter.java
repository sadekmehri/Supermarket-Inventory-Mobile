package com.example.caisse.models;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caisse.R;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private final Cart cart;

    public MainAdapter(Cart cart) {
        this.cart = cart;
    }

    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.cart_list_item, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        /* Get product items from cart  */
        ArrayList<Product> lstProducts = cart.getCart();
        Product product = lstProducts.get(position);

        /* Display single item product */
        holder.productTitle.setText(product.getName());
        holder.productQty.setText(String.valueOf(product.getQuantity()));
        holder.productPrice.setText(product.getTotal() + " TND");


        /* Delete product */
        holder.productDeleteBtn.setOnClickListener(view -> {
            lstProducts.remove(position);
            notifyDataSetChanged();
        });

        /* Decrease product quantity */
        holder.priceMinusBtn.setOnClickListener(view -> {
            int quantity = product.getQuantity();
            quantity = (quantity > 0) ? quantity - 1 : 0;
            product.setQuantity(quantity);
            product.calculateTotal();

            /* If quantity is equal to zero then delete the product item from cart */
            if (quantity == 0)
                lstProducts.remove(position);


            notifyDataSetChanged();
        });

        /* Increase product quantity */
        holder.pricePlusBtn.setOnClickListener(view -> {
            int quantity = product.getQuantity();
            quantity = (quantity < 50) ? quantity + 1 : 50;

            product.setQuantity(quantity);
            product.calculateTotal();
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return cart.getSize();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView productTitle;
        private final TextView productPrice;
        private final TextView productQty;
        private final ImageButton productDeleteBtn;
        private final ImageButton priceMinusBtn;
        private final ImageButton pricePlusBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productTitle = itemView.findViewById(R.id.cart_product_title);
            productPrice = itemView.findViewById(R.id.cart_product_price);
            productQty = itemView.findViewById(R.id.cart_product_quantity_text_View);
            productDeleteBtn = itemView.findViewById(R.id.cart_product_delete_btn);
            priceMinusBtn = itemView.findViewById(R.id.cart_product_minus_btn);
            pricePlusBtn = itemView.findViewById(R.id.cart_product_plus_btn);
        }

    }
}
