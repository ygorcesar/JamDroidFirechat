package com.ygorcesar.jamdroidfirechat.viewmodel;

import com.ygorcesar.jamdroidfirechat.model.User;

public interface MessageAdapterViewModelContract {
    void onMessageItemClick(User user);
}
