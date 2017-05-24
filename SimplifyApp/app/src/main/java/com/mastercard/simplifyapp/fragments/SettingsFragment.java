package com.mastercard.simplifyapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.R;
import com.simplify.android.sdk.Customer;

import java.util.ArrayList;

/**
 * Created by e069278 on 23/05/2017.
 */

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }
}
