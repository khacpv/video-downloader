package com.oic.vdd.common.views.listview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oic.vdd.R;
import com.oic.vdd.common.imageloader.DefaultImageOption;
import com.oic.vdd.models.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khacpham on 1/7/16.
 */
public class FilterAdapter extends ArrayAdapter<Page> {

    public FilterAdapter(Context context, List<Page> pages) {
        super(context, android.R.layout.simple_list_item_1,new ArrayList<>(pages));
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_social_item,parent,false);
        }
        final ImageView thumbImageView = (ImageView)convertView.findViewById(R.id.thumb);
        TextView title = (TextView)convertView.findViewById(R.id.title);
        ProgressBar loading = (ProgressBar)convertView.findViewById(R.id.loading);

        Page page = getItem(position);
        convertView.setTag(page);

        title.setText(page.toString());
        thumbImageView.setImageResource(R.drawable.ic_menu_gallery);
        if(page.cover!=null && !page.cover.source.isEmpty()) {
            DefaultImageOption.loadImage(page.cover.source,thumbImageView,loading);
        }else{
            DefaultImageOption.loadImage(page.picture.data.url,thumbImageView,loading);
        }

        return convertView;
    }

}
