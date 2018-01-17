package com.bmw.dinosaursmall.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bmw.dinosaursmall.R;
import com.bmw.dinosaursmall.utils.BitmapUtil;
import com.bmw.dinosaursmall.view.ui.PicShowActivity;
import com.bmw.dinosaursmall.view.ui.PlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/2.
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private List<File> files;
    private Context context;
    private boolean isPicture;
    private static final String TAG = "FileListAdapter";
    private BitmapUtil bitmapUtil;
    Handler handler = new Handler();


    public FileListAdapter(Context context, boolean isPicture) {
        this.context = context;
        this.isPicture = isPicture;
        files = new ArrayList<>();
        bitmapUtil = new BitmapUtil();

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file, null);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.img.setTag(files.get(position).getName());

        holder.bg.setBackgroundColor(context.getResources().getColor(R.color.bg_base));
        Drawable mDrawableDefault = context.getResources().getDrawable(R.color.bg_base);
        Drawable mDrawablePressed = context.getResources().getDrawable(R.color.btn_press);
        StateListDrawable drawable = new StateListDrawable();
        //按下状态
        drawable.addState(new int[]{android.R.attr.state_pressed}, mDrawablePressed);
        //普通状态
        drawable.addState(new int[]{-android.R.attr.state_focused, -android.R.attr.state_selected,
                -android.R.attr.state_pressed}, mDrawableDefault);
        holder.bg.setBackgroundDrawable(drawable);

        if (isPicture) {
            Bitmap bitmap = bitmapUtil.getBitmapFromMemCache(files.get(position).getAbsolutePath());
            if (bitmap == null) {
//                bitmap = BitmapFactory.decodeFile(files.get(position).getAbsolutePath());
//                bitmapUtil.addBitmapToMemoryCache(files.get(position).getAbsolutePath(), bitmap);
                holder.img.setImageResource(R.mipmap.picture_one);
            } else
                holder.img.setImageBitmap(bitmap);
            holder.text.setText(files.get(position).getName());
        } else {
//            Bitmap bitmap = bitmapUtil.getBitmapFromMemCache(files.get(position).getAbsolutePath());
//            if (bitmap == null) {
//                bitmap = ThumbnailUtils.createVideoThumbnail(files.get(position).getAbsolutePath(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
//                bitmapUtil.addBitmapToMemoryCache(files.get(position).getAbsolutePath(),bitmap);
//            }
            holder.img.setImageResource(R.mipmap.video_large);
            holder.text.setText(files.get(position).getName());
        }


        if (isPicture) {
            holder.bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: ");
                    Intent intent = new Intent(context, PicShowActivity.class);
                    intent.putExtra("bitmap", files.get(position).getAbsolutePath());
                    context.startActivity(intent);


                }
            });
        } else {
            holder.bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;
//                        try {
//                            Uri uri = Uri.parse(files.get(position).getAbsolutePath());
//
//                            //调用系统自带的播放器
//                            intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setDataAndType(uri, "video/mp4");
//                            context.startActivity(intent);
//                        }catch (Exception e){
                    intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("path", files.get(position).getAbsolutePath());
                    context.startActivity(intent);
//                        }

                }
            });

        }

        holder.bg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
               /* Log.d(TAG, "onLongClick: ");
                if (listener != null)
                    listener.longClick(position);*/
                new AlertDialog.Builder(context).setTitle("删除!")
                        .setMessage("确认删除文件：" + files.get(position).getName() + " ?").
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                files.get(position).delete();
                                files.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", null).show();

                return true;
            }
        });


    }


    public void setFiles(final List<File> files) {
        if (files != null) {
            this.files = files;
            handler.post(new Runnable() {
                @Override
                public void run() {

                    notifyDataSetChanged();
                }
            });
        } else {
            this.files = null;
            this.files = new ArrayList<>();
            handler.post(new Runnable() {
                @Override
                public void run() {

                    notifyDataSetChanged();
                }
            });
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (File f : files) {
                    if (bitmapUtil.getBitmapFromMemCache(f.getAbsolutePath()) == null)
                        bitmapUtil.addBitmapToMemoryCache(f.getAbsolutePath(), BitmapFactory.decodeFile(f.getAbsolutePath()));
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }


    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView text;
        private LinearLayout bg;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.item_img);
            text = (TextView) itemView.findViewById(R.id.item_text);
            bg = (LinearLayout) itemView.findViewById(R.id.item);
        }
    }

    public void release() {
        bitmapUtil.clearCache();
    }


}
