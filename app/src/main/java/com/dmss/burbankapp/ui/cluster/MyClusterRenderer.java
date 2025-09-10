package com.dmss.burbankapp.ui.cluster;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.dmss.burbankapp.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyClusterRenderer extends DefaultClusterRenderer<HnlClusterItem> implements ClusterManager.OnClusterClickListener<HnlClusterItem> {
    ShapeDrawable mColoredCircleBackground;
    ShowBottomSheetDialog showBottomSheetDialog;
    FragmentActivity activity;

    private float mDensity = getApplicationContext().getResources().getDisplayMetrics().density;

    private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
    private static final int[] BUCKETS = {100, 200, 500, 1000};


    public MyClusterRenderer(FragmentActivity context, GoogleMap map, ClusterManager<HnlClusterItem> clusterManager,
                             ShapeDrawable mColoredCircleBackground, ShowBottomSheetDialog bottomSheetDialog) {
        super(context, map, clusterManager);
        this.mColoredCircleBackground = mColoredCircleBackground;
        this.mClusterIconGenerator.setBackground(makeClusterBackground());
        clusterManager.setOnClusterClickListener(this);
        this.activity = context;
        this.showBottomSheetDialog = bottomSheetDialog;
    }

    @Override
    protected void onBeforeClusterItemRendered(HnlClusterItem item,
                                               MarkerOptions markerOptions) {

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
        markerOptions.title(item.getTitle());
    }

    @Override
    protected void onClusterItemRendered(HnlClusterItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }


    @Override
    protected int getColor(int clusterSize) {
        return ContextCompat.getColor(getApplicationContext(), R.color.orange_bg_3_1);
    }


    @NonNull
    protected String getClusterText(int bucket) {
        if (bucket < BUCKETS[0]) {
            return String.valueOf(bucket);
        }
        return bucket + "+";
    }

    @Override
    protected int getBucket(@NonNull Cluster<HnlClusterItem> cluster) {
        return cluster.getSize();
    }
   /* @Override
    protected void onBeforeClusterRendered(Cluster<HnlClusterItem> cluster, MarkerOptions markerOptions){

        //  clusterIcon.setColorFilter(m, PorterDuff.Mode.SRC_ATOP);

        mClusterIconGenerator.setBackground(makeClusterBackground());

       *//* //modify padding for one or two digit numbers
        if (cluster.getSize() < 10) {
            mClusterIconGenerator.setContentPadding(40, 20, 0, 0);
        }
        else {
            mClusterIconGenerator.setContentPadding(30, 20, 0, 0);
        }*//*

        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }*/


    private LayerDrawable makeClusterBackground() {
        mColoredCircleBackground = new ShapeDrawable(new OvalShape());
        ShapeDrawable outline = new ShapeDrawable(new OvalShape());
        outline.getPaint().setColor(ContextCompat.getColor(getApplicationContext(), R.color.black)); // Transparent white.
        LayerDrawable background = new LayerDrawable(new Drawable[]{outline, mColoredCircleBackground});
        int strokeWidth = (int) (mDensity * 3);
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        return background;
    }


    @Override
    public boolean onClusterClick(Cluster<HnlClusterItem> cluster) {
        if (cluster == null) return false;
        showBottomSheetDialog.showBottomSheetClusterDialog((ArrayList<HnlClusterItem>) cluster.getItems());
        return true;
    }



    public interface ShowBottomSheetDialog {
        void showBottomSheetClusterDialog(ArrayList<HnlClusterItem> arrayList);
    }
}
