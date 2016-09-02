package com.singala2android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.singala2android.R;
import com.singala2android.bean.MsgBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/2.
 */
public class MsgAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MsgBean> list;

    public MsgAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int paramInt) {
        // TODO Auto-generated method stub
        return list.get(paramInt);
    }

    @Override
    public long getItemId(int paramInt) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup paramViewGroup) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        //
        convertView = initText(convertView, position, inflater);
        //
        return convertView;
    }

    public View initText(View convertView, int position, LayoutInflater inflater) {
        TextView tv_name;
        TextView tv_msg;
        if (list.get(position).getInout().equals("in")) {
            convertView = inflater.inflate(R.layout.item_msg_in, null);
        } else {
            convertView = inflater.inflate(R.layout.item_msg_out, null);
        }
        tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);
        tv_name.setText(list.get(position).getSender_name());
        tv_msg.setText(list.get(position).getMsg());
        System.out.println("++++convertView" + convertView);
        return convertView;
    }


    @SuppressWarnings("unchecked")
    public void setList(ArrayList<MsgBean> list) {
        if (list != null) {
            this.list = (ArrayList<MsgBean>) list.clone();
            notifyDataSetChanged();
        }
    }

}
