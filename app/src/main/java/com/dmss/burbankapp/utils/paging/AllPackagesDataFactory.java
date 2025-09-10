package com.dmss.burbankapp.utils.paging;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import io.reactivex.disposables.CompositeDisposable;

public class AllPackagesDataFactory extends DataSource.Factory {

    private MutableLiveData<PackagesDataSource> mutableLiveData;
    private PackagesDataSource feedDataSource;
    CompositeDisposable disposable;
    int categoryId;


    public AllPackagesDataFactory() {


    }


    @Override
    public DataSource create() {
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }


    public MutableLiveData<PackagesDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
