package wangh.com.trafficemvp.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import wangh.com.trafficemvp.R;
import wangh.com.trafficemvp.bean.AppTrafficInfo;
import wangh.com.trafficemvp.presenter.MainPresenter;
import wangh.com.trafficemvp.util.DividerItemDecoration;
import wangh.com.trafficemvp.view.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements IMainView{
    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    private MyRecyclerViewAdapter viewAdapter;
    private List<AppTrafficInfo> infos;
    private MaterialDialog dialog;
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL_LIST));
        //infos=new ArrayList<>();

        mainPresenter = new MainPresenter(this);
        //校验读取手机状态权限
        showPermissionsDialog(Manifest.permission.READ_PHONE_STATE);
    }


    public void showPermissionsDialog(String permissions) {
        final RxPermissions rxPermissions=new RxPermissions(this);
        rxPermissions.request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (!aBoolean){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.this.startActivity(intent);
                }else{
                    Log.i("Permissions","获取权限成功");
                    //显示加载框,开启线程去加载数据,返回得到数据
                    mainPresenter.getData().observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<AppTrafficInfo>>() {
                                @Override
                                public void accept(List<AppTrafficInfo> appTrafficInfos) throws Exception {
                                    dissmissDialog();
                                    System.out.println("获取数据完成");
                                    viewAdapter = new MyRecyclerViewAdapter(appTrafficInfos,MainActivity.this.getApplicationContext());
                                    recyclerView.setAdapter(viewAdapter);
                                    viewAdapter.notifyDataSetChanged();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    dissmissDialog();
                                    if ("权限异常".equals(throwable.getMessage())){
                                        showtrafficDialog();
                                    }else{
                                        throwable.printStackTrace();
                                    }

                                }
                            });
                }
            }
        });
    }






    @Override
    public void showtrafficDialog() {
        new MaterialDialog.Builder(this)
                .canceledOnTouchOutside(false)
                .content(R.string.traffic_content)
                .positiveText(R.string.traffic_confirm)
                .positiveColor(getResources().getColor(R.color.green_line))
                .negativeText(R.string.cancle)
                .negativeColor(getResources().getColor(R.color.gray_font))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        return;
                    }
                }).onPositive(new MaterialDialog.SingleButtonCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                requestReadNetworkHistoryAccess();
            }
        }).show();
    }

    private void requestReadNetworkHistoryAccess() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        MainActivity.this.startActivity(intent);
    }


    @Override
    public void showDialog() {
        if (dialog==null){
            dialog = new MaterialDialog.Builder(this).progress(false, 100, true).show();
        }else{
            dialog.show();
        }

    }

    @Override
    public void dissmissDialog() {
        if (dialog!=null){
            dialog.dismiss();
        }
    }



    @Override
    public Context getContext() {
        return this.getApplicationContext();
    }
}
