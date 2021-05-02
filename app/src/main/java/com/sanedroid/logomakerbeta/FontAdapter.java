package com.sanedroid.logomakerbeta;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;
import androidx.recyclerview.widget.RecyclerView;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {
    String[] fontlist=new String[10];
    Context mcontext;
    callbackfont mcallbackfont;
    HandlerThread mHandlerThread;
    Handler mHandler;
    int index=0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    FontAdapter(Context mcontext,callbackfont mcallbackfont){
        this.mcallbackfont=mcallbackfont;
        this.mcontext=mcontext;
        mHandlerThread=new HandlerThread("Font");
        mHandlerThread.start();
        mHandler=new Handler(mHandlerThread.getLooper());
        for (String i: mcontext.getResources().getStringArray(R.array.name_fonts)) {
           fontlist[index]=i;
           index++;

        }

    }
    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mcontext).inflate(R.layout.fonttext,parent,false);
        FontViewHolder mFontViewHolder=new FontViewHolder(view);

        return mFontViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FontViewHolder holder, final int position) {
        loadfonts(fontlist[position],holder.mTextview);

        holder.mTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcallbackfont.onfontclick(holder.mTextview.getTypeface());
            }
        });
    }

    @Override
    public int getItemCount() {
        return fontlist.length;
    }

    public class FontViewHolder extends RecyclerView.ViewHolder {
        TextView mTextview;
        FontViewHolder(View view){
            super(view);
            mTextview= view.findViewById(R.id.textview);
        }
    }
   public interface callbackfont{
        public void onfontclick(Typeface typeface);
    }
    public  void loadfonts(String query, final TextView mTxetView){
        final Typeface[] temp = new Typeface[1];



            androidx.core.provider.FontRequest mFontRequest = new FontRequest(
                    "com.google.android.gms.fonts"
                    , "com.google.android.gms", query, R.array.com_google_android_gms_fonts_certs);

            FontsContractCompat.FontRequestCallback mcallback = new FontsContractCompat.FontRequestCallback() {
                @Override
                public void onTypefaceRetrieved(Typeface typeface) {
        mTxetView.setTypeface(typeface);
                }

                @Override
                public void onTypefaceRequestFailed(int reason) {
                    Toast.makeText(mcontext, ""+reason, Toast.LENGTH_SHORT).show();
                }
            };
            FontsContractCompat.requestFont(mcontext, mFontRequest, mcallback, mHandler);


    }

    public  void loadfonts(String query, final EditText mEditText) {
        final Typeface[] temp = new Typeface[1];


        androidx.core.provider.FontRequest mFontRequest = new FontRequest(
                "com.google.android.gms.fonts"
                , "com.google.android.gms", query, R.array.com_google_android_gms_fonts_certs);

        FontsContractCompat.FontRequestCallback mcallback = new FontsContractCompat.FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                mEditText.setTypeface(typeface);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                Toast.makeText(mcontext, "" + reason, Toast.LENGTH_SHORT).show();
            }
        };
        FontsContractCompat.requestFont(mcontext, mFontRequest, mcallback, mHandler);


    }
}
