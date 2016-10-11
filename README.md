# mvpdemo_google
the mvp demo reference google 

15年年底本人公司新开的一个项目，用上了mvp模式开发，那个时候还没发现google出了mvp的demo。
##首先什么是MVP：
* M-model，即javaBean 数据模型层；
* V-view，视图层，常用的即Activity Fragment,这里是定义一个接口IView,Activity去实现IView的写法；
* P-presenter，数据处理层，所有的数据逻辑，业务逻辑都在这里处理；

##原来我的写法
#####而当时我在写mvp时只是简单的写成了：以下几个类：
* UserInfoModel-model;
* IUserInfoView-IView;
* UserInfoActivity-Activity;
* UserInfoPresenter-Presenter;

######接下来假设业务是这样的：网络请求用户信息接口，并将用户信息展现在UserInfoActivity中。
 * 先看目录结构：
![目录.png](http://upload-images.jianshu.io/upload_images/1874706-f55122b964c9bf0a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

* 1：先看Model--UserInfoModel

```java
public class UserInfoModel {
    private String name;
    private int age;
    private String address;
    public UserInfoModel(String name, int age, String address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
```
* 2：View--实现的IUserInfoView:

```java
public interface IUserInfoView {   
          String loadUserId();//假设接口请求需要一个userId
          void showLoading();//展示加载框
          void dismissLoading();//取消加载框展示
          void showUserInfo(UserInfoModel userInfoModel);//将网络请求得到的用户信息回调
}
```
网络接口请求用户信息之前 获得userId,然后展示loading,数据加载成功取消loading,最后将数据展示在Activity上
* 3：Presetner--UserInfoPresenter：这里就实现一个模拟的接口请求

```java
public class UserInfoPresenter {
    private IUserInfoView iUserInfoView;

    public UserInfoPresenter(IUserInfoView iUserInfoView) {
        this.iUserInfoView = iUserInfoView;
    }

    public void loadUserInfo() {
        String userId = iUserInfoView.loadUserId();
        iUserInfoView.showLoading();//接口请求前显示loading
        //这里模拟接口请求回调-
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //模拟接口返回的json，并转换为javaBean
                UserInfoModel userInfoModel = new UserInfoModel("小宝", 1, "杭州");
                iUserInfoView.showUserInfo(userInfoModel);
                iUserInfoView.dismissloading();
            }
        }, 3000);
    }
}
```
* 4：View--UserInfoActivity实现IUserInfoView接口：

```java
public class UserInfoActivity extends AppCompatActivity implements IUserInfoView {
    private TextView tv_name;
    private TextView tv_age;
    private TextView tv_address;
    private UserInfoPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_address = (TextView) findViewById(R.id.tv_address);
        presenter = new UserInfoPresenter(this);
        presenter.loadUserInfo();
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
}
```
####这样写并没有错，只是不能更直观的看到IView中的方法和Presenter中的方法的关联。

##Google demo写法有所不同：[google官方mvp写法demo](https://github.com/googlesamples/android-architecture/tree/todo-mvp/)
######下面用google官方demo的写法实现上面的模拟业务：
* ####首先看下目录结构：
![google demo mvp 目录.png](http://upload-images.jianshu.io/upload_images/1874706-9df6251371f905e6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这里多了一个contract包：里面放的是契约接口。更能直接明了的看到View和Presenter之间的方法。
还多了一个BaseView，BasePresenter：看代码

```java
public interface BasePresenter {
    void start();
}
```
这里的start()方法就相当于约定了所有的Presenter的初始化操作都放在start()方法中；

```java
public interface BaseView<T> {
    void setPresenter(T presenter);
}
```
* 再来看契约类：UserInfoContract

```java
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
```
契约内部有2个接口，分别继承了BaseView和BasePresenter，View和Presenter中实现的方法分别是UI操作，和数据业务逻辑操作，此时是不是看的异常的清晰。

多了一个契约类，契约内部包含了2个接口，一个是Presenter一个是View，就相当于之前的写法中的接口IView和普通类Presenter，只不过现在都将这两个类所需要的业务和UI层的接口直接放在一起展现出来，变得很清晰。在契约接口中的Presenter是一个接口，需要我们去实现，代码如下:

```java
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
```
1：UserInfoPresenter 构造函数中传入UserInfoContract.View，并且调用view的setPresenter()方法；
2：将所有的初始化操作都放在start()方法中（这里demo只有一个：网络请求获取用户信息），这样只要进入界面的时候调用start()方法就可以执行一系列初始化的操作，这就相当于一种约定好的开发。

* 最后看UserInfoActivity如何进行调用

```java
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
```
在onCreate()方法： 

```java
new UserInfoPresenter(this);
presenter.start();
```
而并没有写成

```java
presenter=new UserInfoPresenter(this);
```
因为UserInfoActivity实现了UserInfoContract.View中的setPresenter()方法；而UserInfoPresenter 构造函数中已经调用了UserInfoContract.View中的setPresenter()方法；

两者思想一样，只是写法不同。
