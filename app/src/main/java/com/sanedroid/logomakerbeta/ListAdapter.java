package com.sanedroid.logomakerbeta;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter
{

	Context mContext;
	ArrayList<Items> list=new ArrayList();
	
	public static class Items
	{
		Integer img_id;
		String collection_name;
        Uri downloadlink;
		public Items(Integer img_id, String collection_name,Uri downloadlink)
		{
			this.img_id = img_id;
			this.collection_name = collection_name;
			this.downloadlink=downloadlink;
		}

		public void setImg_id(Integer img_id)
		{
			this.img_id = img_id;
		}

		public Integer getImg_id()
		{
			return img_id;
		}

		public void setCollection_name(String collection_name)
		{
			this.collection_name = collection_name;
		}

		public String getCollection_name()
		{
			return collection_name;
		}
		public void setDownloadlink(){
			this.downloadlink=downloadlink;
		}
		public  Uri getDownloadlink(){
			return downloadlink;
		}
	}
	
	
	public ListAdapter(Context mContext, ArrayList<Items> list)
	{
		this.mContext =  mContext;
		this.list = list;
	}
	@Override
	public int getCount()
	{
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO: Implement this method
		return list.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO: Implement this method
		return position;
	}
	class ViewHolder{
		ImageView mImageView;
		TextView mTextView;
		ViewHolder(View v){
			mImageView=(ImageView) v.findViewById(R.id.main_itemImageView);
		    mTextView=(TextView)v.findViewById(R.id.main_itemTextView);
		}
	}
	
	
	
	@Override
	public View getView(int position, View convertview, ViewGroup viewgrop)
	{
		View row=convertview;
		ViewHolder mViewHolder=null;

		
		
		if(row==null){
			LayoutInflater mLayoutInflator=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row=mLayoutInflator.inflate(R.layout.main_item,viewgrop,false);
			mViewHolder=new ViewHolder(row);
			row.setTag(mViewHolder);
		}
		else{
			mViewHolder=(ViewHolder)row.getTag();
		}
		Items mItems=list.get(position);
			mViewHolder.mImageView.setImageResource(mItems.img_id);

		Typeface mTypeface= Typeface.createFromAsset(mContext.getAssets(),"QueerStreet.ttf");
		mViewHolder.mTextView.setTypeface(mTypeface);
		mViewHolder.mTextView.setText(mItems.collection_name);
		// TODO: Implement this method
		return row;
	}
	
}
