package com.hank.mvpdemo.googlemvpdemo.presenter;

import android.os.Handler;

import com.hank.mvpdemo.googlemvpdemo.contract.UserInfoContract;
import com.hank.mvpdemo.googlemvpdemo.model.UserInfoModel;

/**
 * Created by ybk on 2016/5/29.
 */
public class UserInfoPresenter implements UserInfoContract.Presenter {
    private UserInfoContract.View view;

    public UserInfoPresenter(UserInfoContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void loadUserInfo() {
        String userId = view.loadUserId();
        view.showLoading();//接口请求前显示loading
        //这里模拟接口请求回调-
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //模拟接口返回的json，并转换为javaBean
                UserInfoModel userInfoModel = new UserInfoModel("小宝", 1, "杭州");
                view.showUserInfo(userInfoModel);
                view.dismissLoading();
            }
        }, 3000);
    }

    @Override
    public void start() {
        loadUserInfo();
    }

}
