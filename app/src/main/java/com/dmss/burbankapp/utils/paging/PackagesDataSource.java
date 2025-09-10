package com.dmss.burbankapp.utils.paging;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.dmss.burbankapp.data.model.AllPackagesModel;


import io.reactivex.disposables.CompositeDisposable;


public class PackagesDataSource extends PageKeyedDataSource<Integer, AllPackagesModel> {
    public static final String TAG = PackagesDataSource.class.getSimpleName();
    CompositeDisposable disposable;
    int categoryId;
    private static final int FIRST_PAGE = 1;
    private MutableLiveData networkState;

    public PackagesDataSource(int categoryId, CompositeDisposable compositeDisposable) {
        this.disposable = compositeDisposable;
        this.categoryId = categoryId;
        networkState = new MutableLiveData();


    }

    public MutableLiveData getNetworkState() {
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, AllPackagesModel> callback) {


    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, AllPackagesModel> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, AllPackagesModel> callback) {
    }
}
