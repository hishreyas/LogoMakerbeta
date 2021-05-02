package com.sanedroid.logomakerbeta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import androidx.transition.Transition;
import androidx.transition.TransitionSet;


public class ImageAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<ImageItem> imglist=new ArrayList<>();
    ListImages mListImages;
    public ImageAdapter(Context mContext,ArrayList<ImageItem> imglist) {
        this.mContext = mContext;
        this.imglist=imglist;
    }

    public static class ImageItem{
        Uri Downloadlink;
        String Filename;

        public String getFilename() {
            return Filename;
        }

        public Uri getDownloadlink() {
            return Downloadlink;
        }

        public ImageItem(Uri downloadlink,String filename) {

            Downloadlink = downloadlink;
            Filename=filename;
        }
    }
    class ViewHoder{
        ImageView mImagview;

        ViewHoder(View view){
            mImagview=view.findViewById(R.id.main_itemImageView);
        }

    }

    @Override
    public int getCount() {
        return imglist.size();
    }

    @Override
    public Object getItem(int position) {
        return imglist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        ViewHoder mViewHolder=null;
        ImageItem mImageItem;
        Bitmap bitmap;
        if (row == null) {
            LayoutInflater mlayoutInflator=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=mlayoutInflator.inflate(R.layout.main_item,parent,false);
            mViewHolder=new ViewHoder(row);
            row.setTag(mViewHolder);
        }
        else {
            mViewHolder=(ViewHoder) row.getTag();
        }
        mListImages=new ListImages();
        mImageItem=imglist.get(position);
        String fileloc=mListImages.isfileexists(mImageItem.getFilename(),mContext);
        if(fileloc!=String.valueOf(0)){
            File mfilename=new File(fileloc);

    Glide.with(mContext).load(mfilename).transition(DrawableTransitionOptions.withCrossFade(1000)).into(mViewHolder.mImagview).getView();


          //  FileInputStream mFileInputStream=new FileInputStream(fileloc);

            //  bitmap= BitmapFactory.decodeStream(mFileInputStream);
            //mViewHolder.mImagview.setImageBitmap(bitmap);

        }
        else {
            Glide.with(mContext).load(mImageItem.getDownloadlink()).transition(DrawableTransitionOptions.withCrossFade(1000)).into(mViewHolder.mImagview).getView();
        }
        return row;
    }
}
