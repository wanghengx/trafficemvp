package wangh.com.trafficemvp.model;

import android.content.Context;

import java.util.List;

import wangh.com.trafficemvp.bean.AppTrafficInfo;

/**
 * 作者：wangheng_a on 2018/4/19 09:51
 * <p>
 * 邮箱：wangheng155@163.com
 */

public interface ITrafficInfo {
    List<AppTrafficInfo> getAppTrafficInfos(Context context);
}
