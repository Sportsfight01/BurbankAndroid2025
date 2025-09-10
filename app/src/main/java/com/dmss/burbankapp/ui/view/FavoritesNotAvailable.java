package com.dmss.burbankapp.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dmss.burbankapp.R;

public class FavoritesNotAvailable extends Dialog {
    public FavoritesNotAvailable(@NonNull Context context) {
        super(context);
    }

    public FavoritesNotAvailable(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected FavoritesNotAvailable(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.layout_favorites);
        this.setCanceledOnTouchOutside(false);
        TextView okText = findViewById(R.id.ok);
        okText.setOnClickListener(v -> {
            dismiss();
        });
    }
}