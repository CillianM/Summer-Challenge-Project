package com.mastercard.simplifyapp.interfaces;

/**
 * Created by Cillian on 26/06/2017.
 */

public interface OnTaskCompleted{
    void onTaskCompleted();

    void onTaskInProgress(String info);

    void onErrorOccuring();

}
