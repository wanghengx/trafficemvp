package wangh.com.trafficemvp.util;

import android.annotation.TargetApi;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.SparseArray;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Stack;

import wangh.com.trafficemvp.bean.AppTrafficInfo;

/**
 * Created by Robert Zagórski on 2016-09-09.
 */
@TargetApi(Build.VERSION_CODES.M)
public class NetworkStatsHelper {
    static long SEVEN_DAYS_IN_MS = 7*1000* 60*60*24L;


    public static long getAllRxBytesMobile(Context context,NetworkStatsManager networkStatsManager) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    getTimesMonthMorning(),
                    System.currentTimeMillis()+SEVEN_DAYS_IN_MS);
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getRxBytes();
    }

    public static long getAllTxBytesMobile(Context context,NetworkStatsManager networkStatsManager) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    getTimesMonthMorning(),
                    System.currentTimeMillis()+SEVEN_DAYS_IN_MS);
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getTxBytes();
    }

    public static long getAllRxBytesWifi(NetworkStatsManager networkStatsManager) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    getTimesMonthMorning(),
                    System.currentTimeMillis()+SEVEN_DAYS_IN_MS);
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getRxBytes();
    }

    public static long getAllTxBytesWifi(NetworkStatsManager networkStatsManager) {
        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    getTimesMonthMorning(),
                    System.currentTimeMillis()+SEVEN_DAYS_IN_MS);
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getTxBytes();
    }

    public static long getPackageRxBytesMobile(Context context,NetworkStatsManager networkStatsManager,int packageUid) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    0,
                    System.currentTimeMillis()+SEVEN_DAYS_IN_MS,
                    packageUid);
        } catch (RemoteException e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
        networkStats.getNextBucket(bucket);
        return bucket.getRxBytes();
    }

    public static long getPackageTxBytesMobile(Context context,NetworkStatsManager networkStatsManager,int packageUid) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    0,
                    System.currentTimeMillis()+SEVEN_DAYS_IN_MS,
                    packageUid);
        } catch (RemoteException e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
        return bucket.getTxBytes();
    }

    public static long getPackageRxBytesWifi(NetworkStatsManager networkStatsManager,int packageUid) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis()+SEVEN_DAYS_IN_MS,
                    packageUid);
        } catch (RemoteException e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
        return bucket.getRxBytes();
    }

    public static void getByMobile(NetworkStatsManager networkStatsManager, Context ctx, SparseArray<AppTrafficInfo> map) {
        NetworkStats summaryStats = null;
        try {
            summaryStats = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(ctx),
                    getTimesMonthMorning(),
                    System.currentTimeMillis());

            NetworkStats.Bucket summaryBucket = new NetworkStats.Bucket();
            do {
                summaryStats.getNextBucket(summaryBucket);
                int summaryUid = summaryBucket.getUid();
                             //uid处理
                AppTrafficInfo trafficInfo ;
                if (map.get(summaryUid)!=null){
                    trafficInfo = map.get(summaryUid);
                    trafficInfo.setAppRx(trafficInfo.getAppRx()+summaryBucket.getRxBytes());
                    trafficInfo.setAppTx(trafficInfo.getAppTx()+summaryBucket.getTxBytes());
                }else{
                    trafficInfo=new AppTrafficInfo();
                    trafficInfo.setAppRx(summaryBucket.getRxBytes());
                    trafficInfo.setAppTx(summaryBucket.getTxBytes());
                }
                map.put(summaryUid,trafficInfo);
            } while (summaryStats.hasNextBucket());
        } catch (RemoteException e) {
            return ;
        }
    }


    public static void getByWifi(NetworkStatsManager networkStatsManager, Context ctx,SparseArray<AppTrafficInfo> map) {
        NetworkStats summaryStats = null;
        try {
            summaryStats = networkStatsManager.querySummary(
                    ConnectivityManager.TYPE_WIFI,
                    getSubscriberId(ctx),
                    getTimesMonthMorning(),
                    System.currentTimeMillis());

            NetworkStats.Bucket summaryBucket = new NetworkStats.Bucket();
            do {
                summaryStats.getNextBucket(summaryBucket);
                int summaryUid = summaryBucket.getUid();
                              //uid处理
                AppTrafficInfo trafficInfo ;
                if (map.get(summaryUid)!=null){
                    trafficInfo = map.get(summaryUid);
                    trafficInfo.setAppWifiRx(trafficInfo.getAppWifiRx()+summaryBucket.getRxBytes());
                    trafficInfo.setAppWifiTx(trafficInfo.getAppWifiTx()+summaryBucket.getTxBytes());
                }else{
                    trafficInfo=new AppTrafficInfo();
                    trafficInfo.setAppWifiRx(summaryBucket.getRxBytes());
                    trafficInfo.setAppWifiTx(summaryBucket.getTxBytes());
                }
                map.put(summaryUid,trafficInfo);
            } while (summaryStats.hasNextBucket());
        } catch (RemoteException e) {
            return ;
        }
    }


    public static long getTimesMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    public static long getPackageTxBytesWifi(NetworkStatsManager networkStatsManager,int packageUid) {
        NetworkStats networkStats = null;
        try {
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis()+SEVEN_DAYS_IN_MS,
                    packageUid);
        } catch (RemoteException e) {
            return -1;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        networkStats.getNextBucket(bucket);
        return bucket.getTxBytes();
    }

    private static String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        }
        return "";
    }

    private static String getSubscriberId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId();

    }
}
