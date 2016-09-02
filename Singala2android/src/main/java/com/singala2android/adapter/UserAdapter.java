package com.singala2android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.singala2android.R;
import com.singala2android.bean.UserBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/2.
 */
public class UserAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<UserBean> list;

    public UserAdapter(Context context) {
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
    public View getView(final int position, View convertView,
                        ViewGroup paramViewGroup) {
        // TODO Auto-generated method stub
        View v = null;
        try {
            if (convertView != null) {
                v = convertView;
            } else {
                LayoutInflater inflater = LayoutInflater.from(context);
                v = inflater.inflate(R.layout.item_user, null);
                ItemControls ic = new ItemControls();
                ic.tv_name = (TextView) v.findViewById(R.id.tv_name);
                v.setTag(ic);
            }
            ItemControls ic = (ItemControls) v.getTag();
            ic.tv_name.setText(list.get(position).getUserName());

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return v;
    }

    class ItemControls {
        TextView tv_name;
    }

    @SuppressWarnings("unchecked")
    public void setList(ArrayList<UserBean> list) {
        if (list != null) {
            this.list = (ArrayList<UserBean>) list.clone();
            notifyDataSetChanged();
        }
    }

}
