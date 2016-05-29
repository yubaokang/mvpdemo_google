package com.hank.mvpdemo.googlemvpdemo.contract;

import com.hank.mvpdemo.googlemvpdemo.presenter.BasePresenter;
import com.hank.mvpdemo.googlemvpdemo.model.UserInfoModel;

/**
 * Created by ybk on 2016/5/29.
 */
public interface UserInfoContract {

    interface View extends BaseView<Presenter>{

        void showLoading();//展示加载框

        void dismissLoading();//取消加载框展示

        void showUserInfo(UserInfoModel userInfoModel);//将网络请求得到的用户信息回调

        String loadUserId();//假设接口请求需要一个userId
    }

    interface Presenter extends BasePresenter {
        void loadUserInfo();
    }

}
