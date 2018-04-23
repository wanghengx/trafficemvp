package wangh.com.trafficemvp.presenter;



import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wangh.com.trafficemvp.bean.AppTrafficInfo;
import wangh.com.trafficemvp.model.TrafficInfo;
import wangh.com.trafficemvp.view.IMainView;

/**
 * 作者：wangheng_a on 2018/4/19 10:02
 * <p>
 * 邮箱：wangheng155@163.com
 */

public class MainPresenter implements MainContact {
    private IMainView iMainView;

    private TrafficInfo info;

    public MainPresenter(IMainView iMainView) {
        this.iMainView = iMainView;
        info=new TrafficInfo();
    }



    @Override
    public Observable<List<AppTrafficInfo>> getData() {
        iMainView.showDialog();
        return Observable.fromCallable(new Callable<List<AppTrafficInfo>>() {

            @Override
            public List<AppTrafficInfo> call() throws Exception {
                //校验权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (!hasPermissionToReadNetworkHistory()) {
                        throw new RuntimeException("权限异常");
                    }
                }
                return info.getAppTrafficInfos(iMainView.getContext());
            }
        }).subscribeOn(Schedulers.io());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasPermissionToReadNetworkHistory() {
        final AppOpsManager appOps = (AppOpsManager) iMainView.getContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),iMainView.getContext().getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        return false;
    }
}
