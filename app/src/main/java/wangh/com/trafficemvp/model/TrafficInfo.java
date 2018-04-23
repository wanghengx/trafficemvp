package wangh.com.trafficemvp.model;

import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import wangh.com.trafficemvp.bean.AppTrafficInfo;
import wangh.com.trafficemvp.util.NetworkStatsHelper;

/**
 * 作者：wangheng_a on 2018/4/19 09:56
 * <p>
 * 邮箱：wangheng155@163.com
 */

public class TrafficInfo implements ITrafficInfo {
    private final String TAG="TrafficInfo";
    @Override
    public List<AppTrafficInfo> getAppTrafficInfos(Context context) {
        List<AppTrafficInfo> infos=new ArrayList<>();
        PackageManager pm =context.getPackageManager();
        TrafficInfo info =new TrafficInfo();
        List<AppTrafficInfo> list;
        Log.i(TAG,"开始获取总流量数据");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SparseArray<AppTrafficInfo> map=new SparseArray<>();
            Log.i(TAG,"开始获取总流量数据1");
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
            Log.i(TAG,"开始获取总流量数据1.1");
            NetworkStatsHelper.getByMobile(networkStatsManager,context,map);
            Log.i(TAG,"开始获取总流量数据2");
            NetworkStatsHelper.getByWifi(networkStatsManager,context,map);
            Log.i(TAG,"开始获取总流量数据3");
            list=getResult(pm, map);
            Log.i(TAG,"开始获取总流量数据4");
        }else {
            list=getResult(pm,null);
        }
        //按流量多少排序
        Log.i(TAG,"sort");
        Collections.sort(list);
        return list;
    }

    private List<AppTrafficInfo> getResult(PackageManager pm,SparseArray<AppTrafficInfo> map) {
        List<AppTrafficInfo> list=new ArrayList<>();
        List<PackageInfo> packInfoList = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES
                |PackageManager.GET_PERMISSIONS);
        for (PackageInfo packageInfo : packInfoList) {
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null && permissions.length > 0) {

                for (String permission : permissions) {
                    if ("android.permission.INTERNET".equals(permission)) {
                        int uid = packageInfo.applicationInfo.uid;
                        if (map==null){
                            AppTrafficInfo appTrafficInfo = new AppTrafficInfo();
                            appTrafficInfo.setPackageName(packageInfo.applicationInfo.packageName);
                            appTrafficInfo.setName(packageInfo.applicationInfo.loadLabel(pm).toString());
                            appTrafficInfo.setAppRx(TrafficStats.getUidRxBytes(uid));
                            appTrafficInfo.setAppTx(TrafficStats.getUidTxBytes(uid));
                            list.add(appTrafficInfo);
                        }else{
                            if (map.get(uid)!=null) {
                                AppTrafficInfo trafficInfo = map.get(uid);
                                if (!list.contains(trafficInfo)){
                                    trafficInfo.setPackageName(packageInfo.applicationInfo.packageName);
                                    trafficInfo.setName(packageInfo.applicationInfo.loadLabel(pm).toString());
                                    list.add(trafficInfo);
                                }
                            }
                        }
                        break;
                    }

                }
            }
        }
        return list;
    }
}
