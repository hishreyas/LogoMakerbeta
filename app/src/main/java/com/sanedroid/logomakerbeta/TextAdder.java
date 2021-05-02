package com.sanedroid.logomakerbeta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.shashi.mysticker.Sticker;
import com.shashi.mysticker.TextSticker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.core.provider.FontRequest;
import androidx.core.provider.FontsContractCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import static com.sanedroid.logomakerbeta.ImageEditor.*;


public class TextAdder extends Fragment  {
   static EditText mEditText;
    BottomNavigationView mBottomNavigation;
    ImageButton cancelbutton,okbutton;
    FragmentTransaction mFragmentTransction;
    FragmentManager mFragment;
    RecyclerView mRecyclerview;
    FontAdapter mfontAdapter;
    ImageEditor mImageEditor;
    TextAdderCallBack mCallback;
    TextSticker mTtextsticker;
    HandlerThread mHandlerThread;
    Handler mHandler;
    AdView mAdview;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
     View v=   inflater.inflate(R.layout.layout_editor,container,false);
     mEditText=v.findViewById(R.id.edittextadder);
     mBottomNavigation=v.findViewById(R.id.navigation_addtext);
     cancelbutton=v.findViewById(R.id.cancel);
     okbutton=v.findViewById(R.id.ok);
     mRecyclerview=v.findViewById(R.id.recycler_view);
     mAdview=v.findViewById(R.id.adView);
     mImageEditor=(ImageEditor)getActivity();
     if(mImageEditor.flag==true){

         mEditText.setText(mImageEditor.getText());
         mEditText.setTextColor(mImageEditor.getColor());
         mEditText.setTypeface(mImageEditor.getTypeface());
     }
loadbanner();
     mTtextsticker=new TextSticker(getContext());
      LinearLayoutManager mLayoutmanager=new LinearLayoutManager(getContext(),(int)OrientationHelper.HORIZONTAL,false);
     mRecyclerview.setLayoutManager(mLayoutmanager );
     mfontAdapter=new FontAdapter(getContext(), new FontAdapter.callbackfont() {
         @Override
         public void onfontclick(Typeface typeface) {
             mEditText.setTypeface(typeface,mEditText.getTypeface().getStyle());
         }
     });
     mRecyclerview.setAdapter(mfontAdapter);
     mRecyclerview.setVisibility(View.GONE);
     oncancel();
     onok();
     mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
         @Override
         public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
             switch (menuItem.getItemId()){

                case R.id.show_font:
                    if(!(mRecyclerview.getVisibility()==View.GONE)) {
                        mRecyclerview.setVisibility(View.GONE);
                    }
                        else {
                        mRecyclerview.setVisibility(View.VISIBLE);
                    }
                 return true;
                 case R.id.boldtext:
mEditText.setTypeface(mEditText.getTypeface(), Typeface.BOLD);
                     return true;
                 case R.id.italictexr:
mEditText.setTypeface(mEditText.getTypeface(),Typeface.ITALIC);
                     return true;
                 case R.id.colortext:
diacolorpickert();
                     return true;
             }
             return false;
         }
     });

        return v;
    }
    public void oncancel(){

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mCallback.onTextAdded(mEditText.getText().toString(),mEditText.getTypeface().getStyle(),mEditText.getCurrentTextColor(),null);
getFragmentManager().popBackStack();
            }
        });


    }
    public  void  onok()
    {
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText.getText().toString().trim().length()==0){
                    Toast.makeText(getContext(), "Enter Text ", Toast.LENGTH_SHORT).show();
                }
else {
    if(mImageEditor.flag)
    {
        mCallback.onUpdateText((mEditText.getText().toString()), mEditText.getTypeface().getStyle(), mEditText.getCurrentTextColor(), mEditText.getTypeface());
        getFragmentManager().popBackStack();
    }
    else
        {
        mCallback.onTextAdded(mEditText.getText().toString(), mEditText.getTypeface().getStyle(), mEditText.getCurrentTextColor(), mEditText.getTypeface());
        getFragmentManager().popBackStack();

    }
                }
            }

        });


    }

    public  void diacolorpickert(){
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowCustom(false)
                .setDialogId(1)
                .setColor(Color.BLACK)
                .setShowAlphaSlider(true)
                .setShowColorShades(true)
                .show(getActivity());
    }

    public interface TextAdderCallBack{
        public void onTextAdded(String text,int style,int color,Typeface typeface);
        public void onUpdateText(String text,int style,int color,Typeface typeface);
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
if(context instanceof  TextAdderCallBack){
    mCallback=(TextAdderCallBack)context;
}

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback=null;

    }

public  void loadbanner(){
    AdRequest mAdrequest=new AdRequest.Builder().build();
    mAdview.loadAd(mAdrequest);
}

}
