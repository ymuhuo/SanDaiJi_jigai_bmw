package com.bmw.dinosaursmall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.model.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/2.
 */
public class EnvironmentAdapter extends RecyclerView.Adapter<EnvironmentAdapter.ViewHolder> {


    private List<Environment> list;
    private Context context;
    private AdapterDateChangeListener adapterDateChangeListener;
    private String e_name, e_unit;

    public EnvironmentAdapter(Context context) {
        list = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<Environment> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_environment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Environment environment = list.get(position);
        holder.name.setText(environment.getName());
        holder.min_num.setText(String.valueOf(environment.getMin_num()));
        holder.max_num.setText(String.valueOf(environment.getMax_num()));
        if (environment.getCurrent_num() != -10000.0) {
            holder.current_num.setText(String.valueOf(environment.getCurrent_num()));
            //根据当前值判断状态
            if (environment.getCurrent_num() < environment.getMin_num()) {
                holder.stat_img.setImageResource(R.drawable.bg_dot_red);
                holder.stat.setText("过低");
                holder.stat.setTextColor(Color.RED);
            } else if (environment.getCurrent_num() > environment.getMax_num()) {
                holder.stat_img.setImageResource(R.drawable.bg_dot_red);
                holder.stat.setText("过高");
                holder.stat.setTextColor(Color.RED);
            } else {
                holder.stat_img.setImageResource(R.drawable.bg_dot_green);
                holder.stat.setText("正常");
                holder.stat.setTextColor(Color.GREEN);
            }
        }else {
            holder.current_num.setText("无数值");
            holder.stat_img.setImageResource(R.drawable.bg_dot_red);
            holder.stat.setText("异常");
            holder.stat.setTextColor(Color.RED);
        }

        if(position%2 != 0){
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.bg_base));
        }else{
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.bg_spacerlayer_half));
        }

    }

    private void showError(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView stat_img;
        private TextView name;
        private TextView current_num;
        private TextView min_num;
        private TextView max_num;
        private TextView stat;
        private TextView set;
        private RelativeLayout container;


        public ViewHolder(View itemView) {
            super(itemView);
            stat_img = (ImageView) itemView.findViewById(R.id.stat_img);
            name = (TextView) itemView.findViewById(R.id.name);
            current_num = (TextView) itemView.findViewById(R.id.current_num);
            min_num = (TextView) itemView.findViewById(R.id.min_num);
            max_num = (TextView) itemView.findViewById(R.id.max_num);
            stat = (TextView) itemView.findViewById(R.id.stat);
            container = (RelativeLayout) itemView.findViewById(R.id.environment_container);
//            set = (TextView) itemView.findViewById(R.id.item_set);
        }
    }

    public void setAdapterDateChangeListener(AdapterDateChangeListener adapterDateChangeListener) {
        this.adapterDateChangeListener = adapterDateChangeListener;
    }

    //接口监听，最大最小值发生变化时通知activity
    public interface AdapterDateChangeListener {
        void resetDate();
    }


}
