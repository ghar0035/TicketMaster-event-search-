package com.cst2335.finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Details extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ID = "ID";

    // TODO: Rename and change types of parameters
    private String id;

    public Details() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id = getArguments().getString(ID);

        }
        getActivity().setTitle("Afsaneh");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View newView = inflater.inflate(R.layout.fragment_details, container, false);
        // Inflate the layout for this fragment
        TextView idTexView = newView.findViewById(R.id.textTest);
        idTexView.setText("ID = " + id);
  return newView;
    }
}