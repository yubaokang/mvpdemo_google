package com.hank.mvpdemo.googlemvpdemo.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.hank.mvpdemo.R;
import com.hank.mvpdemo.googlemvpdemo.contract.UserInfoContract;
import com.hank.mvpdemo.googlemvpdemo.model.UserInfoModel;
import com.hank.mvpdemo.googlemvpdemo.presenter.UserInfoPresenter;

public class UserInfoActivity extends AppCompatActivity implements UserInfoContract.View {

    private TextView tv_name;
    private TextView tv_age;
    private TextView tv_address;

    private UserInfoContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_address = (TextView) findViewById(R.id.tv_address);
        new UserInfoPresenter(this);
        presenter.start();
    }

    @Override
    public void showLoading() {
        Toast.makeText(this, "正在加载", Toast.LENGTH_LONG).show();
    }

    @Override
    public void dismissLoading() {
        Toast.makeText(this, "加载完成", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showUserInfo(UserInfoModel userInfoModel) {
        if (userInfoModel != null) {
            tv_name.setText(userInfoModel.getName());
            tv_age.setText(String.valueOf(userInfoModel.getAge()));
            tv_address.setText(userInfoModel.getAddress());
        }
    }

    @Override
    public String loadUserId() {
        return "1000";//假设需要查询的用户信息的userId是1000
    }

    @Override
    public void setPresenter(UserInfoContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
