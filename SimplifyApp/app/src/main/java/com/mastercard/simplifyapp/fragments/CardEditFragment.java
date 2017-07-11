package com.mastercard.simplifyapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mastercard.simplifyapp.R;

/**
 * Created by Cillian on 10/07/2017.
 */

public class CardEditFragment extends Fragment {

    private String text;
    private EditText cardEditField;
    private int type;
    public static final int NUMBER = 0;
    public static final int EXPIRY = 1;
    public static final int CVV = 2;


    public CardEditFragment() {
        // Required empty public constructor
    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.card_edit_field, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        cardEditField = (EditText) view.findViewById(R.id.editText);
        if (type == NUMBER) {
            cardEditField.setHint("Enter Number");
        } else if (type == EXPIRY) {
            cardEditField.setHint("Enter Expiry");
        } else if (type == CVV) {
            cardEditField.setHint("Enter CVV");
        }

    }
}
