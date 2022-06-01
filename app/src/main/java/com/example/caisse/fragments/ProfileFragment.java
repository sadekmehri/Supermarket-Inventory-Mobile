package com.example.caisse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.caisse.R;
import com.example.caisse.models.Account;
import com.example.caisse.singletons.SharedStaffSingleton;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONException;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShimmerFrameLayout container = view.findViewById(R.id.shimmer_view_container);
        LinearLayout linearLayout = view.findViewById(R.id.fragmentLayout);

        /* Start the animation */
        linearLayout.setVisibility(View.INVISIBLE);
        container.startShimmer();

        /* Get user from cache */
        try {
            Account account = SharedStaffSingleton.getInstance(getContext()).getAccount();
            displayProfile(view, account);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* Finish the animation */
        linearLayout.setVisibility(View.VISIBLE);
        container.stopShimmer();
        container.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    /* Update profile after calling the api */
    private void displayProfile(View view, Account account) {
        TextView textViewRoleProfile = view.findViewById(R.id.roleProfileView);
        TextView textViewNameProfile = view.findViewById(R.id.nameProfileView);
        TextView textViewMobileProfile = view.findViewById(R.id.mobileProfileView);
        TextView textViewEmailProfile = view.findViewById(R.id.emailProfileView);
        TextView textViewDobProfile = view.findViewById(R.id.dobProfileView);
        TextView textViewStoreProfile = view.findViewById(R.id.storeProfileView);
        TextView textViewLocationProfile = view.findViewById(R.id.locationProfileView);
        TextView textViewCountryStoreProfile = view.findViewById(R.id.countryProfileView);


        textViewRoleProfile.setText(account.getRole());
        textViewNameProfile.setText(account.getStaff().getFullName());
        textViewMobileProfile.setText(account.getStaff().getPhone());
        textViewEmailProfile.setText(account.getEmail());
        textViewDobProfile.setText(account.getStaff().getDob());
        textViewStoreProfile.setText(account.getStaff().getStore().getName());
        textViewLocationProfile.setText(account.getStaff().getStore().getAddress());
        textViewCountryStoreProfile.setText(account.getStaff().getStore().getCity());
    }

}