package wangh.com.trafficemvp.view;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wangh.com.trafficemvp.R;
import wangh.com.trafficemvp.bean.AppTrafficInfo;
import wangh.com.trafficemvp.view.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 *
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private final List<AppTrafficInfo> mValues;
    private Context context;


    public MyRecyclerViewAdapter(List<AppTrafficInfo> items, Context context) {
        mValues = items;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.info = mValues.get(position);
        //TODO
        //holder.icon.setText(mValues.get(position).id);

        holder.icon.setImageDrawable(getIcon(mValues.get(position).getPackageName()));
        holder.appname.setText(mValues.get(position).getName());
        holder.gprs_down.setText(mValues.get(position).getAppRx()+"");
        holder.gprs_up.setText(mValues.get(position).getAppTx()+"");
        holder.wifi_down.setText(mValues.get(position).getAppWifiRx()+"");
        holder.wifi_up.setText(mValues.get(position).getAppWifiTx()+"");
    }


    private Drawable getIcon(String pakgename) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(pakgename, PackageManager.GET_META_DATA);
//
//
// 应用名称
// pm.getApplicationLabel(appInfo)

//应用图标
            Drawable appIcon = pm.getApplicationIcon(appinfo);
            return appIcon;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView icon;

        @BindView(R.id.tv_appname)
        TextView appname;

        @BindView(R.id.tv_gprs_down)
        TextView gprs_down;

        @BindView(R.id.tv_gprs_up)
        TextView gprs_up;

        @BindView(R.id.tv_wifi_up)
        TextView wifi_up;

        @BindView(R.id.tv_wifi_down)
        TextView wifi_down;

        public AppTrafficInfo info;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }


    }
}
