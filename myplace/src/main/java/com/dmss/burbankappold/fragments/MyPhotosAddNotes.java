package com.dmss.burbankappold.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import common.AppController;

import com.dmss.burbankappold.R;

/**
 * Created by jaya.krishna on 06-06-2017.
 */

public class MyPhotosAddNotes extends Fragment {

    View rootView;
    EditText addNotesEditText;
    AppController controller;
    Activity activity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.photos_add_notes, null);
        initializeUIElements();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    public void initializeUIElements() {
        ImageView backImage = rootView.findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        TextView firstText = rootView.findViewById(R.id.firstText);
        firstText.setText("Add Notes");
        TextView secondText = rootView.findViewById(R.id.secondText);
        secondText.setVisibility(View.GONE);
        controller = (AppController) getActivity().getApplicationContext();
        addNotesEditText = (EditText) rootView.findViewById(R.id.addNotesEditText);
        //addNotesEditText.requestFocus();
        openKeyboard(activity, addNotesEditText);


        if (controller.getSelectedPhotoNotes() != null && controller.getSelectedPhotoNotes().length() > 0) {
            addNotesEditText.setText(controller.getSelectedPhotoNotes());
            addNotesEditText.setSelection(addNotesEditText.getText().length());
            controller.setTempPhotoNotes(controller.getSelectedPhotoNotes());
        } else {
            controller.setTempPhotoNotes("");
        }

        addNotesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence values, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence values, int start, int before, int count) {
                //Method to get the search results based on the text entered and populates the data to listview
                controller.setTempPhotoNotes(addNotesEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public static void openKeyboard(Activity activity, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);/*
        inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);*/
    }


}