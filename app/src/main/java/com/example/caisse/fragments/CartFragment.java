package com.example.caisse.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caisse.R;
import com.example.caisse.activities.CustomScannerActivity;
import com.example.caisse.activities.WelcomeActivity;
import com.example.caisse.interfaces.VolleyResponseListener;
import com.example.caisse.models.Cart;
import com.example.caisse.models.MainAdapter;
import com.example.caisse.models.Product;
import com.example.caisse.requests.AuthRequest;
import com.example.caisse.services.CartService;
import com.example.caisse.singletons.CartSingleton;
import com.example.caisse.singletons.SharedAuthSingleton;
import com.example.caisse.singletons.ToastSingleton;
import com.example.caisse.utils.MapUtils;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Map;


public class CartFragment extends Fragment {

    private final Cart cart = CartSingleton.getInstance(getContext()).getCart();
    private final RecyclerView.Adapter<MainAdapter.ViewHolder> mAdapter = new MainAdapter(cart);
    private final ActivityResultLauncher<ScanOptions> fragmentLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                /* If the user cancel the can process */
                if (result.getContents() == null) {
                    ToastSingleton.getInstance(getContext()).toast("You have canceled the scan", Toast.LENGTH_LONG);
                    return;
                }

                /* Initiate cart service */
                String barCode = result.getContents();
                CartService cartService = new CartService(getContext());
                AuthRequest authRequest = SharedAuthSingleton.getInstance(getContext()).getUser();

                cartService.checkProductAvailability(authRequest, barCode, new VolleyResponseListener() {
                    /* Scan code bar and add it to product cart */
                    @Override
                    @SuppressLint("NotifyDataSetChanged")
                    public void onResponse(Map<String, String> response) {
                        Product product = MapUtils.initializeProduct(response);
                        cart.addItem(product);
                        mAdapter.notifyDataSetChanged();
                    }

                    /* Handle error response */
                    @Override
                    public void onError(Map<String, String> response) {
                        MapUtils.handleErrorResponse(response, getContext());
                    }
                });

                ToastSingleton.getInstance(getContext()).toast("Scanned barcode : " + barCode, Toast.LENGTH_LONG);
            });


    public CartFragment() {
    }

    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        Button scanButton = view.findViewById(R.id.scan_from_fragment);
        Button clearButton = view.findViewById(R.id.delete_all_items);
        ImageView emptyImageView = view.findViewById(R.id.cart_empty_image_view);
        TextView emptyTextView = view.findViewById(R.id.cart_empty_text_view);
        RecyclerView mRecyclerView = view.findViewById(R.id.cart_products_recycler_view);

        /* Initialize the first appearance */
        initializeBackground(clearButton, emptyImageView, emptyTextView, mRecyclerView);

        /* Setting recycler view adapter */
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        scanButton.setOnClickListener(this::scanCustomScanner);
        clearButton.setOnClickListener(this::clearShoppingCart);


        /* Change view depending on the state of recycle view */
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            @SuppressLint("SetTextI18n")
            public void onChanged() {
                super.onChanged();
                /* Setting the cart size badge */
                ((WelcomeActivity) requireActivity()).createCartBadge(cart.getSize());
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            void checkEmpty() {
                initializeBackground(clearButton, emptyImageView, emptyTextView, mRecyclerView);
            }
        });

        return view;
    }

    /* Handle clear all items from cart event  */
    private void clearShoppingCart(View view) {
        int size = cart.getSize();

        cart.clearItems();
        mAdapter.notifyItemRangeRemoved(0, size);
        ((WelcomeActivity) requireActivity()).createCartBadge(0);
        ToastSingleton.getInstance(getContext()).toast("All products are removed", Toast.LENGTH_LONG);
    }


    /* Initialize the first appearance */
    private void initializeBackground(Button clearButton, ImageView emptyImageView, TextView emptyTextView, RecyclerView mRecyclerView) {
        boolean isEmpty = mAdapter.getItemCount() == 0;

        clearButton.setEnabled(!isEmpty);
        mRecyclerView.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);
        emptyImageView.setVisibility(isEmpty ? View.VISIBLE : View.INVISIBLE);
        emptyTextView.setVisibility(isEmpty ? View.VISIBLE : View.INVISIBLE);
    }

    /* Custom scanner initialization */
    public void scanCustomScanner(View view) {
        ScanOptions options = new ScanOptions().setOrientationLocked(true).setCaptureActivity(CustomScannerActivity.class);
        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES);
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        fragmentLauncher.launch(options);
    }

}