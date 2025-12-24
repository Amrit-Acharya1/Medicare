package com.acharyaamrit.medicare.pharmacy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acharyaamrit.medicare.R;


public class PharmacyHomeFragment extends Fragment {



    public PharmacyHomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_pharmacy_home, container, false);
        return view;
    }
}