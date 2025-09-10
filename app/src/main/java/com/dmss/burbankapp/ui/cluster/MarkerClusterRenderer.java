package com.dmss.burbankapp.ui.cluster;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dmss.burbankapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

@SuppressLint("InflateParams")
public class MarkerClusterRenderer extends DefaultClusterRenderer<HnlClusterItem> implements ClusterManager.OnClusterClickListener<HnlClusterItem>, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap googleMap;
    private LayoutInflater layoutInflater;
    private final IconGenerator clusterIconGenerator;
    private final View clusterItemView;
    private Context mContext;

    public MarkerClusterRenderer(@NonNull Context context, GoogleMap map, ClusterManager<HnlClusterItem> clusterManager) {
        super(context, map, clusterManager);

        this.googleMap = map;
        this.mContext = context;

        final Drawable clusterIcon = ContextCompat.getDrawable(context, R.drawable.circle_map_icon);
        clusterIcon.setColorFilter(ContextCompat.getColor(context, R.color.appColor), PorterDuff.Mode.SRC_ATOP);

        layoutInflater = LayoutInflater.from(context);

        clusterItemView = layoutInflater.inflate(R.layout.single_cluster_marker_view, null);

        clusterIconGenerator = new IconGenerator(context);
        clusterIconGenerator.setColor(ContextCompat.getColor(context, R.color.appColor));
        clusterIconGenerator.setBackground(clusterIcon);
        clusterIconGenerator.setContentView(clusterItemView);

        clusterManager.setOnClusterClickListener(this);

        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());

        googleMap.setOnInfoWindowClickListener(this);

        // clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomClusterItemInfoView());

        googleMap.setOnCameraIdleListener(clusterManager);

        googleMap.setOnMarkerClickListener(clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(HnlClusterItem item, MarkerOptions markerOptions) {
        // markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
        markerOptions.title(item.getTitle());
    }


    @Override
    protected void onClusterItemRendered(HnlClusterItem clusterItem, Marker marker) {
        marker.setTag(clusterItem);
    }

    @Override
    public boolean onClusterClick(Cluster<HnlClusterItem> cluster) {
        if (cluster == null) return false;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (HnlClusterItem user : cluster.getItems())
            builder.include(user.getPosition());
        LatLngBounds bounds = builder.build();
        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Context context = clusterItemView.getContext();
        HnlClusterItem user = (HnlClusterItem) marker.getTag(); //  handle the clicked marker object
        if (context != null && user != null)
            Toast.makeText(context, user.getTitle(), Toast.LENGTH_SHORT).show();
    }

    /*private class MyCustomClusterItemInfoView implements GoogleMap.InfoWindowAdapter {

        private final View clusterItemView;

        MyCustomClusterItemInfoView() {
            clusterItemView = layoutInflater.inflate(R.layout.marker_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            HnlClusterItem user = (HnlClusterItem) marker.getTag();
            if (user == null) return clusterItemView;
            TextView itemNameTextView = clusterItemView.findViewById(R.id.itemNameTextView);
            TextView itemAddressTextView = clusterItemView.findViewById(R.id.itemAddressTextView);
            itemNameTextView.setText(marker.getTitle());
            itemAddressTextView.setText(user.getAddress());
            return clusterItemView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }*/

    @Override
    protected void onBeforeClusterRendered(Cluster<HnlClusterItem> cluster, MarkerOptions markerOptions) {

        final Drawable clusterIcon = ContextCompat.getDrawable(mContext, R.mipmap.cluster_icon);
        //  clusterIcon.setColorFilter(m, PorterDuff.Mode.SRC_ATOP);

        clusterIconGenerator.setBackground(clusterIcon);

        //modify padding for one or two digit numbers
        if (cluster.getSize() < 10) {
            clusterIconGenerator.setContentPadding(40, 20, 0, 0);
        } else {
            clusterIconGenerator.setContentPadding(30, 20, 0, 0);
        }

        Bitmap icon = clusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

   /* protected void onBeforeClusterRendered(Cluster<T> cluster, MarkerOptions markerOptions) {
        int bucket = getBucket(cluster);
        BitmapDescriptor descriptor = mIcons.get(bucket);
        if (descriptor == null) {
            mColoredCircleBackground.getPaint().setColor(getColor(bucket));
            // you can edit/replace getClusterText to change text of cluster icon
            descriptor = BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(getClusterText(bucket)));
            mIcons.put(bucket, descriptor);
        }
        // TODO: consider adding anchor(.5, .5) (Individual markers will overlap more often)
        markerOptions.icon(descriptor);
    }*/
}
