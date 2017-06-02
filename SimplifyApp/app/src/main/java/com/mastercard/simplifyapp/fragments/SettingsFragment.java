package com.mastercard.simplifyapp.fragments;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.mastercard.simplifyapp.NavItem;
import com.mastercard.simplifyapp.R;
import com.mastercard.simplifyapp.adapters.DrawerListAdapter;
import com.simplify.android.sdk.Customer;

import java.util.ArrayList;

/**
 * Created by e069278 on 23/05/2017.
 */

public class SettingsFragment extends Fragment {

    ArrayList<NavItem> mNavItems;

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

        mNavItems = new ArrayList<>();
        mNavItems.add(new NavItem("Account", "Change Account Settings", R.drawable.ic_account_circle_black_24dp));
        mNavItems.add(new NavItem("General Settings", "Search For Customer Information", R.drawable.ic_settings_black_24dp));
        mNavItems.add(new NavItem("Security Settings", "App Security Settings", R.drawable.ic_security_black_24dp));
        mNavItems.add(new NavItem("Help", "Get Help", R.drawable.ic_help_black_24dp));

        ListView settingsList = (ListView) this.getActivity().findViewById(R.id.settings_list);
        DrawerListAdapter adapter = new DrawerListAdapter(this.getActivity(), mNavItems);
        settingsList.setAdapter(adapter);

        final RelativeLayout rootview = (RelativeLayout) this.getActivity().findViewById(R.id.profileBox);
        rootview.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        rootview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);

                float finalRadius = (float) Math.hypot(v.getWidth(), v.getHeight());
                int cx1 = (rootview.getLeft() + rootview.getRight()) / 2;
                int cy1 = (rootview.getTop() + rootview.getBottom()) / 2;
                Animator anim = ViewAnimationUtils.createCircularReveal(v, cx1, cy1, 0, finalRadius);
                anim.setDuration(900);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());

                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rootview.setBackgroundResource(R.color.colorAccent);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                anim.start();


            }
        });

    }

}
