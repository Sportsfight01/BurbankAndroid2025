package com.dmss.burbankapp.utils.customviews;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dmss.burbankapp.databinding.SpinnerViewBinding;

public class SpinnerImage extends FrameLayout {

    private SpinnerViewBinding binding;
    private boolean isRotated = false;

    int rotationAngle = 0;
    private Context mContext;

    public SpinnerImage(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public SpinnerImage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SpinnerImage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = SpinnerViewBinding.inflate(inflater, this, true);
    }

    public void reverse() {
        if (rotationAngle == 90 && isRotated) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(binding.imgSpinner, "rotation", rotationAngle, rotationAngle - 90);
            rotationAngle =rotationAngle - 90;
            anim.setDuration(300);
            anim.start();
            /*rotationAngle -= 90;
            rotationAngle = rotationAngle % 360;*/
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                /*if (rotationAngle == 0) {
                    binding.imgSpinner.setColorFilter(ContextCompat.getColor(mContext, R.color.colorArrowDown), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    binding.imgSpinner.setColorFilter(ContextCompat.getColor(mContext, R.color.colorArrowUp), android.graphics.PorterDuff.Mode.SRC_IN);
                }*/
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    Log.e("TAG", "onAnimationCancel: ");
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    Log.e("TAG", "onAnimationRepeat: ");

                }
            });
        }
    }

    public void rotate() {
        isRotated = true;

        ObjectAnimator anim = ObjectAnimator.ofFloat(binding.imgSpinner, "rotation", rotationAngle, rotationAngle + 90);
        anim.setDuration(300);
        rotationAngle =rotationAngle + 90;
        anim.start();
       /* rotationAngle += 90;
        rotationAngle = rotationAngle % 360;*/
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /*if (rotationAngle == 0) {
                    binding.imgSpinner.setColorFilter(ContextCompat.getColor(mContext, R.color.colorArrowDown), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    binding.imgSpinner.setColorFilter(ContextCompat.getColor(mContext, R.color.colorArrowUp), android.graphics.PorterDuff.Mode.SRC_IN);
                }*/
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e("TAG", "onAnimationCancel: ");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e("TAG", "onAnimationRepeat: ");

            }
        });
    }
}
