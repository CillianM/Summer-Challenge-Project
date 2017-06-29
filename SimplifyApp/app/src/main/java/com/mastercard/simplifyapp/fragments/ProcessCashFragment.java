package com.mastercard.simplifyapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mastercard.simplifyapp.R;

public class ProcessCashFragment extends DialogFragment {

    private EditText amountEntered;
    private TextView totalDue;
    private TextView changeDue;
    private Button submitButton;
    private double total;
    private boolean valid;

    public ProcessCashFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ProcessCashFragment newInstance(String title, double total) {
        ProcessCashFragment frag = new ProcessCashFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putDouble("total", total);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_process_cash, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title

        String title = getArguments().getString("title");
        total = getArguments().getInt("total");
        getDialog().setTitle(title);

        amountEntered = (EditText) view.findViewById(R.id.entered_amount);
        totalDue = (TextView) view.findViewById(R.id.total_due);
        totalDue.setText("Total" + total);
        changeDue= (TextView) view.findViewById(R.id.change_due);
        submitButton = (Button) view.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyTransaction();
            }
        });


    }

    private void verifyTransaction() {
        if (submitButton.getText().equals("Verify")) {
            String amount = amountEntered.getText().toString();
            Double enteredAmount;
            valid = false;
            try {
                enteredAmount = Double.parseDouble(amount);
                if (enteredAmount < total) {
                    double tmp = total - enteredAmount;
                    changeDue.setText("Still owes: " + tmp);
                } else if (enteredAmount > total) {
                    double tmp = enteredAmount -total;
                    changeDue.setText("We owe: " + tmp);
                    submitButton.setText("Process");
                    valid = true;
                } else {
                    valid = true;
                    submitButton.setText("Process");
                }
            } catch (NumberFormatException e) {
                //not a double
            }
        }
        else
        {
            dismiss();
        }
    }

}
