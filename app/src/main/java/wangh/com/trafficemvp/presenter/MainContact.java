package wangh.com.trafficemvp.presenter;

import java.util.List;

import io.reactivex.Observable;
import wangh.com.trafficemvp.bean.AppTrafficInfo;
import wangh.com.trafficemvp.model.TrafficInfo;

/**
 * 作者：wangheng_a on 2018/4/19 17:39
 * <p>
 * 邮箱：wangheng155@163.com
 */

public interface MainContact {
    Observable<List<AppTrafficInfo>> getData();
}
