package com.zckj.data;

import com.linux.vshow.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



/**
 * Created by angcyo on 2015-03-19 019.
 */
public class ExSetFilePathAdapter extends BaseAdapter {
    private FileNote fileNote;
    private Context context;

    public ExSetFilePathAdapter(Context context) {
        this.context = context;
    }

    public ExSetFilePathAdapter(Context context, FileNote fileNote) {
        this.fileNote = fileNote;
        this.context = context;
    }

    @Override
    public int getCount() {
        return fileNote == null ? 0 : fileNote.fileFolderName.size();
    }

    @Override
    public Object getItem(int position) {
        return fileNote == null ? 0 : fileNote.fileFolderPath.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_exset_filepath, null);
            holder = new ViewHolder();
            holder.textFileName = (TextView) convertView.findViewById(R.id.id_exset_text_file_name);
            holder.imageFileIco = (ImageView) convertView.findViewById(R.id.id_exset_img_file_ico);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textFileName.setText(fileNote.fileFolderName.get(position));

        return convertView;
    }

    private static class ViewHolder {
        TextView textFileName; //文件名
        ImageView imageFileIco;//文件图标,这里只代表文件夹
    }
}
//修改于:2015年5月15日,星期五
