package com.ygorcesar.jamdroidfirechat.viewmodel;

import android.view.View;

import com.ygorcesar.jamdroidfirechat.model.MapLocation;
import com.ygorcesar.jamdroidfirechat.model.User;

public interface MessageAdapterViewModelContract {
    void onMessageItemClick(User user, int viewId);

    void onMessageItemClick(String uri, View view);

    void onMessageLocationClick(MapLocation mapLocation);
}
