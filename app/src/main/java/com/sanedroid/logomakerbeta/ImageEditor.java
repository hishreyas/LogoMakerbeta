package com.sanedroid.logomakerbeta;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.service.chooser.ChooserTargetService;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.shashi.mysticker.DrawableSticker;
import com.shashi.mysticker.Sticker;
import com.shashi.mysticker.StickerView;
import com.shashi.mysticker.TextSticker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import static com.sanedroid.logomakerbeta.R.id.framelayout;
import static com.sanedroid.logomakerbeta.R.id.sticker_view;

public class ImageEditor extends AppCompatActivity implements ColorPickerDialogListener, TextAdder.TextAdderCallBack {
MainActivity mainActivity;
   BottomNavigationView mBottomNagivatiom;
   int REQUEST_CODE=1;
   Uri mFileUri=null;
   ListImages mListImage;
   Dialog mDialog ;
   ProgressDialog mProgressDialog;
   ImageAdapter mImageAdaptor;
   StickerView mSticker;
   RelativeLayout relative;
   FrameLayout mFramelayout;
   TextAdder mTextAdder;
   public boolean flag=false;
   static ArrayList<ImageAdapter.ImageItem> frameobject=new ArrayList<>();
   static ImageAdapter frameadapter;
   static TextSticker mTextSticker;
   String mString;
   AdView mAdview;
   String child;
    Typeface mTyface;
    TextView MText;
    Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.img_editor);
        super.onCreate(savedInstanceState);
        mProgressDialog=new ProgressDialog(this);
         mIntent= getIntent();
        mSticker =findViewById(sticker_view);
        relative = findViewById(R.id.frame);
        MText=findViewById(R.id.edit_layoutTextView);
        mAdview=findViewById(R.id.adView_img);
        mBottomNagivatiom=findViewById(R.id.bottomview);
        mFramelayout=findViewById(R.id.framelayout);
        mTyface=Typeface.createFromAsset(getAssets(), "QueerStreet.ttf");
        MText.setTypeface(mTyface);

        mTextAdder=new TextAdder();
        mainActivity=new MainActivity();
        mListImage=new ListImages();
        mDialog=new Dialog(this);

        mString = mIntent.getStringExtra(getResources().getString(R.string.filename_key));
        child=mIntent.getStringExtra(getResources().getString(R.string.child_key));

        //load banner ads
        loadbanner();

     // add selected sticker
        File mfile = new File(Environment.getExternalStorageDirectory().toString() + "/LogoMaker" + "/" + mString);

        try {
            FileInputStream mFileInputStream = new FileInputStream(mfile);

            Bitmap bitmap = BitmapFactory.decodeStream(mFileInputStream);
            Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
            mSticker.addSticker(new DrawableSticker(mDrawable));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSticker.setLocked(!mSticker.isLocked());


            }
        });

        mBottomNagivatiom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.show_logo:

                     displaylogo();
                        return true;
                    case R.id.add_img:
                        show_frame("Frame");
                        return true;
                    case R.id.add_cplor:
                        diacolorpicker();
                        return true;
                    case R.id.add_text:
                        flag=false;
                        getlayouteditor();
                        return true;

                }

                return false;
            }
        });
        mSticker.setOnStickerOperationListener(new StickerView.OnStickerOperationListener()
        {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker)
            {
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker)
            {


            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker)
            {

            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker)
            {

            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker)
            {

            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker)
            {

            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker)
            {

            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker)
            {
                if(mTextSticker instanceof TextSticker)
                {

                    flag = true;
                    getlayouteditor();

                }
                else {

                }

            }
        });


    }

    //function for back icon
    public void onbackpressimg(View v)
    {


            frameobject.clear();
            super.onBackPressed();
            finish();




    }

     // function for showing option save
    public void popupMenuItemImg(View v) {
         mDialog = new Dialog(ImageEditor.this);
        mDialog.setContentView(R.layout.diloag_save);
        mDialog.create();
        mDialog.show();
        mSticker.setLocked(true);
        ImageButton imagedismis=mDialog.findViewById(R.id.dis);
        Button mpng =mDialog.findViewById(R.id.save_png);
        Button mjpg=mDialog.findViewById(R.id.save_jepg);
        ImageButton mshareimg=mDialog.findViewById(R.id.share_img);


        imagedismis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mpng.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                savelogo(".png");

            }
        });
        mjpg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                    savelogo(".jpg");

            }
        });
        mshareimg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
shareimg();
            }
        });






    }

//function for Adding Sticker
      public  void addstc(String filename){

      File mfile = new File(Environment.getExternalStorageDirectory().toString() +getResources().getString(R.string.app_directory) + "/" + filename);

      if(!mfile.exists()){

           mfile = new File(Environment.getExternalStorageDirectory().toString() +getResources().getString(R.string.frame_directory)  + filename);

      }

      try
      {
        FileInputStream mFileInputStream = new FileInputStream(mfile);

        Bitmap bitmap = BitmapFactory.decodeStream(mFileInputStream);
        Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
        mSticker.addSticker(new DrawableSticker(mDrawable));
      } catch (FileNotFoundException e)
      {

        try
        {

            FileInputStream mFileInputStream = new FileInputStream(filename);

            Bitmap bitmap = BitmapFactory.decodeStream(mFileInputStream);
            Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
            mSticker.addSticker(new DrawableSticker(mDrawable));
        }
        catch (FileNotFoundException i)
        {

            Toast.makeText(this, ""+i.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}

  //function for getting file from storage
    public void getfile()

    {

     Intent  get =new Intent();
     get.setType(getResources().getString(R.string.type_intent_image));
     get.setAction(Intent.ACTION_GET_CONTENT);
     startActivityForResult(get,REQUEST_CODE);


  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null &&  data.getData()!=null){

            mFileUri=data.getData();
            try {
                InputStream is=getContentResolver().openInputStream(mFileUri);
                Drawable mDrawable=Drawable.createFromStream(is,mFileUri.toString());
                mSticker.addSticker(new DrawableSticker(mDrawable));
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }





        }
        super.onActivityResult(requestCode, resultCode, data);


    }


    public void displaylogo() {
        mDialog.setContentView(R.layout.dilog_logo);
        mDialog.show();
        mainActivity.progress(mProgressDialog,this);
        if(mListImage.object.isEmpty()){
            mImageAdaptor = new ImageAdapter(this, mListImage.offline);

        }
        else {
            mImageAdaptor = new ImageAdapter(this, mListImage.object);
        }
            final GridView mgridview = mDialog.findViewById(R.id.logoGridview);


                mgridview.setAdapter(mImageAdaptor);
                mProgressDialog.dismiss();

                mgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        ImageAdapter.ImageItem imageItem;
                        if(mListImage.object.isEmpty()){
                             imageItem =mListImage.offline.get(position);
                        }
                        else {
                            imageItem = mListImage.object.get(position);

                        }
                       if (mListImage.isfileexists(imageItem.getFilename(),getApplicationContext())==String.valueOf(0))
                       {
                           StorageReference mStoragereferences=FirebaseStorage.getInstance().getReferenceFromUrl(imageItem.getDownloadlink().toString());

                           try
                           {
                               downloadimg(mStoragereferences,imageItem.getFilename(),false);
                               mDialog.dismiss();
                           } catch (IOException e)
                           {
                               e.printStackTrace();
                           }
                       }
                       else
                           {
                           addstc(imageItem.getFilename());
                           mDialog.dismiss();
                       }
                    }
                });
            }
            public  void diacolorpicker()
            {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowCustom(false)
                        .setDialogId(0)
                        .setColor(Color.BLACK)
                        .setShowAlphaSlider(true)
                        .setShowColorShades(true)
                        .show(this);
            }


    @Override
    public void onColorSelected(int dialogId, int color) {
        if(dialogId==0){
            relative.setBackgroundColor(color);
        }
        else{
            mTextAdder.mEditText.setTextColor(color);
            mTextAdder.mEditText.setHintTextColor(color);
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
    public  void getlayouteditor(){
        FragmentManager mFragmentManager=getSupportFragmentManager();
        FragmentTransaction mFragmentrans=mFragmentManager.beginTransaction();
        TextAdder mTextAdder=new TextAdder();
        mFragmentrans.replace(framelayout,mTextAdder);
        mFragmentrans.addToBackStack(null);
        mFragmentrans.commit();

}

    @Override
    public void onTextAdded(String text, int fontname,int color,Typeface typeface) {


            mTextSticker = new TextSticker(this);
            mTextSticker.setText(text);
            mTextSticker.setTextColor(color);
            mTextSticker.setTypeface(Typeface.create(typeface, fontname));
            mTextSticker.resizeText();

            mSticker.addSticker(mTextSticker);



    }

    @Override
    public void onUpdateText(String text, int style, int color, Typeface typeface) {
        mTextSticker.setTextColor(color);
        mTextSticker.setTypeface(typeface);
        mTextSticker.setText(text);
        mTextSticker.resizeText();
        mSticker.replace(mTextSticker);
        mSticker.invalidate();

    }


        public  void show_frame(final String key){

            mDialog.setContentView(R.layout.dialog_frame);
            mDialog.show();
            final GridView mgridview = mDialog.findViewById(R.id.frameGridview);

            if((frameobject.isEmpty()))
            {


                FirebaseStorage mFireBaseStroage = FirebaseStorage.getInstance();
                StorageReference mStorageReferences = mFireBaseStroage.getReference().child(key);
                mStorageReferences.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {


                        for (final StorageReference item : listResult.getItems())
                        {
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    frameobject.add(new ImageAdapter.ImageItem(uri, item.getName()));
                                    frameadapter = new ImageAdapter(getApplicationContext(), frameobject);
                                    mgridview.setAdapter(frameadapter);
                                    //
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                            // All the prefixes under listRef.
                        }

                        mProgressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
            else{
                mgridview.setAdapter(frameadapter);
            }
            mgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    ImageAdapter.ImageItem imageItem = frameobject.get(position);
                    StorageReference mStoragereferences=FirebaseStorage.getInstance().getReference().child(key+"/"+imageItem.getFilename());
                    try {
                        downloadimg(mStoragereferences,imageItem.getFilename(),true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mDialog.dismiss();
                }
            });
        }
    public void downloadimg(StorageReference refercence, final String name,final boolean key) throws IOException {

        File localFile = null;
        final File mfile;
        final ProgressDialog showprogress = new ProgressDialog(this);
        showprogress.create();

            File mDir = new File(Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.frame_directory));

        if (!mDir.exists()) {
            mDir.mkdirs();
        }
if(key==true){

    mfile = new File(Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.frame_directory) + name);


}else
{
    mDir = new File(Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.app_directory));
    mfile = new File(Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.app_directory)+"/"+ name);

}

        if (mfile.exists()){
            addstc(name);
        } else {
            localFile = new File(mDir, name);
            localFile.createNewFile();


            showprogress.show();
            showprogress.setMessage(getResources().getString(R.string.progress_message_downloading));
            showprogress.setCanceledOnTouchOutside(false);
            refercence.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    showprogress.dismiss();
                    addstc(name);


                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ImageEditor.this, "Faliure"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    //function for saving image
    public void savelogo(String extension){
        Bitmap bitmap=Bitmap.createBitmap(relative.getWidth(),relative.getHeight(),Bitmap.Config.ARGB_8888);

        Canvas mCanvas=new Canvas(bitmap);
        relative.draw(mCanvas);
        File path=new File(Environment.getExternalStorageDirectory().toString()+"/LogoMaker/MyLogos/");
        if (!path.exists())
        {

            path.mkdirs();
        }
        File mfile=new File(path,+System.currentTimeMillis()+extension);
        try {
            mfile.createNewFile();
            FileOutputStream outputstream=new FileOutputStream(mfile);
            if(extension.equals(new String(".png"))) {

                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputstream);
                Toast.makeText(this, "Successfully Save in "+path.toString(), Toast.LENGTH_SHORT).show();
            }
            else{

                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputstream);
                Toast.makeText(this, "Successfully Save in "+path.toString(), Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // function to share image
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void shareimg(){
        Bitmap bitmap= null;

            bitmap = Bitmap.createBitmap(relative.getWidth(),relative.getHeight(),Bitmap.Config.ARGB_8888);


        Canvas mCanvas=new Canvas(bitmap);
        relative.draw(mCanvas);

        File path=new File(Environment.getExternalStorageDirectory().toString()+"/LogoMaker/MyLogos/Shared/");
        if (!path.exists())
        {

            path.mkdirs();
        }
        File mfile=new File(path,+System.currentTimeMillis()+".png");
        FileOutputStream outputstream= null;
        try {
            outputstream = new FileOutputStream(mfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputstream);
            Intent share=new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType(getResources().getString(R.string.type_intent_image));
            share.putExtra(Intent.EXTRA_STREAM,Uri.parse(mfile.toString()));
            share.putExtra(Intent.EXTRA_TEXT,"Created From LogoMaker");
            startActivity(Intent.createChooser(share,"Send Logo"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
public void loadbanner(){
    AdRequest mAdrequest=new AdRequest.Builder().build();
    mAdview.loadAd(mAdrequest);
}

public  String getText(){
        return ((TextSticker)mTextSticker).getText();
}
public  Typeface getTypeface(){
    return ((TextSticker)mTextSticker).getTypeface();
}
public  int getColor(){
    return ((TextSticker)mTextSticker).getColor();
}
    }




