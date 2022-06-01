package com.example.caisse.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.caisse.R;
import com.example.caisse.activities.PdfViewerActivity;
import com.example.caisse.activities.WelcomeActivity;
import com.example.caisse.interfaces.VolleyResponseListener;
import com.example.caisse.models.Cart;
import com.example.caisse.requests.AuthRequest;
import com.example.caisse.services.PaymentService;
import com.example.caisse.singletons.CartSingleton;
import com.example.caisse.singletons.SharedAuthSingleton;
import com.example.caisse.utils.InvoiceGeneratorUtils;
import com.example.caisse.utils.MapUtils;

import org.json.JSONException;

import java.util.Map;


public class TicketFragment extends Fragment {

    /* */
    private final Cart cart = CartSingleton.getInstance(getContext()).getCart();
    private final float total = cart.calculateTotal();
    private float paid = 0f;

    /* */
    private Button generatePdfButton;
    private ImageView bill_5;
    private ImageView bill_10;
    private ImageView bill_20;
    private ImageView bill_30;
    private ImageView bill_50;
    private TextView paidTextView;
    private TextView restMoneyTextView;
    private TextView totalItemsTextView;
    private TextView totalPriceTextView;


    public TicketFragment() {
    }

    public static TicketFragment newInstance() {
        TicketFragment fragment = new TicketFragment();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    @SuppressLint("SetTextI18n")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        boolean isPaidCart = isPaidCart();
        boolean isEmptyCart = cart.isEmptyCart();
        boolean isEnabledGenerateTicketButton = !isEmptyCart && isPaidCart;

        bill_5 = view.findViewById(R.id.img_5_d);
        bill_10 = view.findViewById(R.id.img_10_d);
        bill_20 = view.findViewById(R.id.img_20_d);
        bill_30 = view.findViewById(R.id.img_30_d);
        bill_50 = view.findViewById(R.id.img_50_d);

        generatePdfButton = view.findViewById(R.id.generate_ticket);

        totalItemsTextView = view.findViewById(R.id.total_items_label);
        totalPriceTextView = view.findViewById(R.id.price_items_price);
        paidTextView = view.findViewById(R.id.price_paid_amount);
        restMoneyTextView = view.findViewById(R.id.price_rest_amount);

        /* Disable clicking event is the total items is equal to zero */
        bill_5 = view.findViewById(R.id.img_5_d);
        bill_10 = view.findViewById(R.id.img_10_d);
        bill_20 = view.findViewById(R.id.img_20_d);
        bill_30 = view.findViewById(R.id.img_30_d);
        bill_50 = view.findViewById(R.id.img_50_d);

        bill_5.setEnabled(!isPaidCart);
        bill_10.setEnabled(!isPaidCart);
        bill_20.setEnabled(!isPaidCart);
        bill_30.setEnabled(!isPaidCart);
        bill_50.setEnabled(!isPaidCart);

        /* Disable clicking event when the cart list is not paid */
        generatePdfButton.setEnabled(isEnabledGenerateTicketButton);

        /* Handle add money */
        bill_5.setOnClickListener(this::handleAdd5d);
        bill_10.setOnClickListener(this::handleAdd10d);
        bill_20.setOnClickListener(this::handleAdd20d);
        bill_30.setOnClickListener(this::handleAdd30d);
        bill_50.setOnClickListener(this::handleAdd50d);

        /* Handle generate ticket pdf */
        generatePdfButton.setOnClickListener(myView -> {
            try {
                handleGenerateTicket();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        /* Initialize first numbers in ticket */
        setTicketText();

        return view;
    }

    /* */
    @SuppressLint("SetTextI18n")
    private void setTicketText() {
        totalItemsTextView.setText("Items(" + cart.getSize() + ")");
        totalPriceTextView.setText(cart.calculateTotal() + " TND");
        paidTextView.setText("0.0 TND");
        restMoneyTextView.setText("Not Paid");
    }

    /* Add 5 dinars */
    private void handleAdd5d(View view) {
        addPaidAmount(5);
    }

    /* Add 10 dinars */
    private void handleAdd10d(View view) {
        addPaidAmount(10);
    }

    /* Add 20 dinars */
    private void handleAdd20d(View view) {
        addPaidAmount(20);
    }

    /* Add 30 dinars */
    private void handleAdd30d(View view) {
        addPaidAmount(30);
    }

    /* Add 50 dinars */
    private void handleAdd50d(View view) {
        addPaidAmount(50);
    }

    /* Handle generate ticket */
    private void handleGenerateTicket() throws JSONException {
        /* Get user from cache */
        final Context context = getContext();
        AuthRequest authRequest = SharedAuthSingleton.getInstance(context).getUser();
        PaymentService paymentService = new PaymentService(context);

        paymentService.paymentProcess(authRequest, cart, new VolleyResponseListener() {
            /* Handle success response */
            @Override
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onResponse(Map<String, String> response) {
                // Generate ticket
                String invoiceNumber = response.get("name");
                InvoiceGeneratorUtils invoiceGeneratorUtils = new InvoiceGeneratorUtils(invoiceNumber);
                invoiceGeneratorUtils.generateInvoice(context, cart);

                // Clear form data
                resetForm();

                // Start new activity to display pdf
                Intent intent = new Intent(getContext(), PdfViewerActivity.class);
                startActivity(intent);
            }

            /* Handle error response */
            @Override
            public void onError(Map<String, String> response) {
                resetForm();
                MapUtils.handleErrorResponse(response, context);
            }
        });
    }

    private void resetForm() {
        cart.clearItems();
        setTicketText();
        generatePdfButton.setEnabled(false);
        ((WelcomeActivity) requireActivity()).createCartBadge(0);
    }

    /* Add money to total paid  */
    @SuppressLint("SetTextI18n")
    private void addPaidAmount(float amount) {
        if (amount <= 0) return;

        this.paid += amount;
        boolean isPaidCart = isPaidCart();

        activatePaymentProcess(isPaidCart);
        paidTextView.setText(paid + " TND");

        if (this.paid >= total) restMoneyTextView.setText("Paid(" + (paid - total) + " TND)");
    }

    /* Disable money bill image view when the ticket is payed*/
    @SuppressLint("SetTextI18n")
    private void activatePaymentProcess(boolean isPaidCart) {
        if (!isPaidCart) return;

        bill_5.setEnabled(false);
        bill_10.setEnabled(false);
        bill_20.setEnabled(false);
        bill_30.setEnabled(false);
        bill_50.setEnabled(false);
        generatePdfButton.setEnabled(true);
    }

    /* check if the ticket is paid */
    private boolean isPaidCart() {
        return paid >= total;
    }

}