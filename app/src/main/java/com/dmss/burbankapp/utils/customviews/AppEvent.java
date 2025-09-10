package com.dmss.burbankapp.utils.customviews;

import com.dmss.burbankapp.data.model.HnLQuizPackageModel;
import com.dmss.burbankapp.data.model.NewHomeListModel;

public class AppEvent {
    public static int UNAUTHARIZED = 1;
    public static int SIGNUP = 2;
    public int type;
    public String data;
    public HnLQuizPackageModel hnLQuizPackageModel;
    public NewHomeListModel newHomeListModel;
    public static int PUSHNOTIFICATION = 4;
    public static int UPDATE_FAVORITES = 5;
    public static int UPDATE_FAVORITES_HOMEDESIGN = 6;
    public static int UPDATE_HOMELANDFAVORITE = 7;
    public static int UPDATE_HOMEDESIGNFAVORITE = 8;
    public boolean isFavorite;
    public static int UPDATE_PROFILE_PIC = 9;
    public static int DISPLAYHOMES_EVENT = 22;

    public AppEvent(int type, String data) {
        this.type = type;
        this.data = data;
    }

    public AppEvent(int type, HnLQuizPackageModel hnlQuizModel, Boolean isFavorite) {
        this.isFavorite = isFavorite;
        this.type = type;
        this.hnLQuizPackageModel = hnlQuizModel;

    }

    public AppEvent(int type, NewHomeListModel newHomeListModel, Boolean isFavorite,boolean fromDesign) {
        this.isFavorite = isFavorite;
        this.type = type;
        this.newHomeListModel = newHomeListModel;

    }

    @Override
    public String toString() {
        return "AppEvent{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}
