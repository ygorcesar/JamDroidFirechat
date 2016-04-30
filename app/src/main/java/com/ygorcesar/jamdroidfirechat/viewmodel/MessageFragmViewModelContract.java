package com.ygorcesar.jamdroidfirechat.viewmodel;

public interface MessageFragmViewModelContract {

    void showToastMessage(int string_res);

    void setEditTextMessage(String msg);

    String getEditTextMessage();
}
