package com.zckj.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linux.vshow.R;


/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private ImageView imgFilePathArrow;
    private View layoutPathItem;
    private OnTreeNodeClickListener listener;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    public IconTreeItemHolder(Context context, OnTreeNodeClickListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    public View createNodeView(final TreeNode node,final IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.adapter_exset_updatepath, null, false);

        layoutPathItem = view.findViewById(R.id.id_layout_path_item);
        imgFilePathArrow = (ImageView) view.findViewById(R.id.id_exset_img_filepath_arrow);
        tvValue = (TextView) view.findViewById(R.id.id_exset_text_file_name);
        tvValue.setText(value.fileNote.fileFolderName.get(value.index));

//        context.getResources().getDimension(R.dimen.ds_padding_3);
        int level = getPadding(value.fileNote.currentFilePath);
        int padd = (int) context.getResources().getDimension(R.dimen.ds_padding_3);
        int paddLevel =(int) context.getResources().getDimension(R.dimen.ds_path_level_padding);
        if (level == 0) {
            layoutPathItem.setPadding(padd, padd, padd, padd);
        } else {
            layoutPathItem.setPadding(padd + (level * paddLevel) - paddLevel , padd, padd, padd);
        }

//        Logger.e("当前路径--" + );
//        Logger.e("分段--" + value.fileNote.currentFilePath.split("/").length);


        if (value.hasChildFolder) {
            imgFilePathArrow.setVisibility(View.VISIBLE);
        } else {
            imgFilePathArrow.setVisibility(View.INVISIBLE);
        }

        layoutPathItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTreeItemClick(node, value);
                }
            }
        });


        return view;
    }

    @Override
    public void toggle(boolean active) {
        imgFilePathArrow.setImageResource(active ? R.drawable.filepath_arrow_open : R.drawable.filepath_arrow_close);
    }

    //根据路径的深度,返回需要填充的padding
    int getPadding(String path) {
        if (!Util.isEmpty(path) && path.length() > 1){
            return  path.split("/").length;
        }
        return  0;
    }

    public static class IconTreeItem {
        public int index;//位置信息
        public FileNote fileNote;//数据
        public boolean isLoad = false;//item是否加载过子node了
        public boolean hasChildFolder = false;//是否有子文件夹

        public IconTreeItem(int index, FileNote node) {
            this.index = index;
            this.fileNote = node;
        }
    }
}
//修改于:2015年5月15日,星期五
