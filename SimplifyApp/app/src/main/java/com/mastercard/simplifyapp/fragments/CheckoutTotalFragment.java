package com.mastercard.simplifyapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.interfaces.UpdateableFragment;
import com.mastercard.simplifyapp.objects.UpdateData;
import com.mastercard.simplifyapp.widgets.SimplifyTextView;

/**
 * Created by e069278 on 28/06/2017.
 */

public class CheckoutTotalFragment extends Fragment implements UpdateableFragment {

    SimplifyTextView priceView;
    ProgressBar checkoutCircle;
    double total;

    public CheckoutTotalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout_total, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        total = bundle.getDouble("total");
        priceView = (SimplifyTextView) view.findViewById(R.id.total);
        checkoutCircle = (ProgressBar) view.findViewById(R.id.checkoutCircle);
        checkoutCircle.setProgress((int) (total % 100));
        String currency = getResources().getString(R.string.euro);
        String price = currency + String.format("%.2f",total);

        priceView.setText(price);

    }

    @Override
    public void update(UpdateData object) {
        total = object.getTotal();
        checkoutCircle.setProgress((int) (total % 100));
        String currency = getResources().getString(R.string.euro);
        String price = currency + String.format("%.2f",total);
        priceView.setText(price);
    }


}
