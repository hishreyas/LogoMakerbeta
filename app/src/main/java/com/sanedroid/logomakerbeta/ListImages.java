package com.sanedroid.logomakerbeta;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.sanedroid.logomakerbeta.R.string.signature;

public  class ListImages extends AppCompatActivity {

     GridView mGridView;
     String mString;
     public static ArrayList<ImageAdapter.ImageItem>object=new ArrayList();
     static ArrayList<ImageAdapter.ImageItem> offline;
     public static ImageAdapter mImageAdapter;
     TextView mTextView;
     MainActivity mMainActivity;
     ProgressDialog mProgressDialog;
     SwipeRefreshLayout mSwipeRefersh;
     RelativeLayout imgerrorlayout;
     RelativeLayout no_downloadlayout;
     String check;
    Typeface mTyface;
    Intent mIntent;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.images);
        mGridView=findViewById(R.id.imgGridView);
        mTextView=findViewById(R.id.img_layoutTextView);
        imgerrorlayout=findViewById(R.id.errorimg_layout);
        no_downloadlayout=findViewById(R.id.no_downloads_msg);
        mSwipeRefersh=findViewById(R.id.imgrefersh);
        mTyface= Typeface.createFromAsset(getAssets(),"QueerStreet.ttf");
        mTextView.setTypeface(mTyface);

        mMainActivity=new MainActivity();
        mProgressDialog=new ProgressDialog(this);
        mIntent = getIntent();

        mString= mIntent.getStringExtra(getResources().getString(R.string.key_data));
      check=mIntent.getStringExtra(getResources().getString(R.string.offline_key));

        // load images using key_data or offline_key
        loadlist();
        loadchartboost();

        //click event on images
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                if (object.size() == 0)
                {

                    ImageAdapter.ImageItem mofflineItem=offline.get(position);
                    try
                    {
                        downloadimg(null,mofflineItem.getFilename());
                    } catch (IOException e)
                    {
                        Toast.makeText(ListImages.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }


                }
                else
                    {
                    ImageAdapter.ImageItem mImageItem = object.get(position);
                    StorageReference mStoragereferences = FirebaseStorage.getInstance().getReference().child(mString + "/" + mImageItem.getFilename());
                    try
                    {
                        downloadimg(mStoragereferences, mImageItem.getFilename());
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }

                // this code will be executed when user click on an image
            }

        });

       // event on swipe to refresh
        mSwipeRefersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh()
    {
        if(check!=null)
        {
            mSwipeRefersh.setRefreshing(false);
        }
        else
            {
            object.clear();
            listimg(mString);
        }

            }

            // this code will executed when user load swipe to refresh
     });

    }


    // function to show images

    public void listimg(String child)
    {

        mMainActivity.progress(mProgressDialog,this);


        if(mMainActivity.checknetwork(getApplicationContext()))
        {

                imgerrorlayout.setVisibility(View.GONE);
                FirebaseStorage mFireBaseStroage = FirebaseStorage.getInstance();
                StorageReference mStorageReferences = mFireBaseStroage.getReference().child(child);
                mStorageReferences.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {


                        for (final StorageReference item : listResult.getItems()) {
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    object.add(new ImageAdapter.ImageItem(uri, item.getName()));

                                    mImageAdapter = new ImageAdapter(ListImages.this, object);
                                    mGridView.setAdapter(mImageAdapter);
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
                        mSwipeRefersh.setRefreshing(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListImages.this, e.toString(), Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();

                    }
                });
        }
         else
             {
               mProgressDialog.dismiss();
               mSwipeRefersh.setRefreshing(false);
               imgerrorlayout.setVisibility(View.VISIBLE);
             }

    }

    //function to show options
    @SuppressLint("RestrictedApi")
    public void popupMenuItemImg(View v){

        MenuBuilder menuBuilder =new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_img ,menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, v);
        optionsMenu.setForceShowIcon(true);
        optionsMenu.show();

        for(int i=0;i<menuBuilder.size();i++){

            menuBuilder.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId())
                    {
                        case R.id.about_app:
                            mMainActivity.displaydialog(ListImages.this, R.layout.dialog_about ,null);
                            break;

                        case R.id.privacy:
                            mMainActivity.displaydialog(ListImages.this, R.layout.dilog_privacy,null);
                            break;

                        case R.id.share:
                            Intent share=new Intent();
                           share(share);
                            break;


                    }


                    return true;
                }
            });

        }

    }
    //function to download image

    public void downloadimg(StorageReference refercence, final String name) throws IOException
    {

        final Intent i = new Intent(ListImages.this, ImageEditor.class);
        File localFile = null;

        final ProgressDialog showprogress = new ProgressDialog(this);
        showprogress.create();

        File mDir = new File(Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.app_directory));
        if (!mDir.exists()) {
            mDir.mkdirs();
        }

        final File mfile = new File(Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.app_directory) + "/" + name);
        if (mfile.exists())
        {
            i.putExtra(getResources().getString(R.string.child_key),mString);
            i.putExtra(getResources().getString(R.string.filename_key), name);
            startActivity(i);

        } else
            {
            localFile = new File(mDir, name);
            localFile.createNewFile();


            showprogress.show();
            showprogress.setMessage(getResources().getString(R.string.progress_message_downloading));
            showprogress.setCanceledOnTouchOutside(false);

            refercence.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    showprogress.dismiss();


                    i.putExtra(getResources().getString(R.string.filename_key),name);
                    startActivity(i);

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ListImages.this, "Faliure", Toast.LENGTH_SHORT).show();
                }


            });

        }
    }

    //function to check file exists
     public String isfileexists(String namefile, Context mContext)
     {

        Bitmap mbitmap;
        File Filelocation=new File(Environment.getExternalStorageDirectory().toString()+mContext.getResources().getString(R.string.app_directory)+"/"+namefile);

         if(!Filelocation.exists()) {
             Filelocation=new File(Environment.getExternalStorageDirectory().toString()+mContext.getResources().getString(R.string.frame_directory)+namefile);

             if(!Filelocation.exists()){
                 return  String.valueOf(0);
             }
             else {
                 return Filelocation.toString();
             }
        }
        else{

          return  Filelocation.toString();
         }
     }

// function on back icon press
    public void onbackpressmain(View v)
    {
        if(mString!=null){
            object.clear();
        }
        else {
            offline.clear();
        }

        super.onBackPressed();
        finish();

    }

    @Override
    public void onBackPressed()
    {

        if(mString!=null){
            object.clear();
        }
        else {
            offline.clear();
        }

        super.onBackPressed();
        finish();
        //when user press back button
    }

    //function to load images list
    public void loadlist()
    {

        if(mString!=null)
        {
            listimg(mString);

        }
        else
            {
                show_offline_img();

        }

    }

   // function to share App
    private   void share(Intent share){

        share.setAction(Intent.ACTION_SEND);
        share.setType(getResources().getString(R.string.type_intent_text));
        share.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.share_text));
        startActivity(Intent.createChooser(share,getResources().getString(R.string.file_chooser_title)));
    }

    //function to show offline images
    public void show_offline_img()
    {
        if(offline ==null)
        {

            offline=new ArrayList<>();
        }
        File path =new File(Environment.getExternalStorageDirectory().toString()+getResources().getString(R.string.app_directory));
        if(!path.exists())
        {
            path.mkdirs();
        }
        File[] mfilelist=path.listFiles();
        if(mfilelist.length==0)
        {
            no_downloadlayout.setVisibility(View.VISIBLE);
        }
        for( File mfilename : mfilelist)
        {
          if(mfilename.isDirectory())
          {

                   }

          else
              {
                offline.add(new ImageAdapter.ImageItem(null, mfilename.getName()));
              }
        }
        mImageAdapter=new ImageAdapter(this,offline);
        mGridView.setAdapter(mImageAdapter);
    }

    //function to load charboost ads
public void loadchartboost( )
  {
        Chartboost mChartboost = null;
        mChartboost.startWithAppId(this,this.getResources().getString(R.string.app_id),this.getResources().getString(signature));
        Chartboost.onCreate(this);

    if (mChartboost.hasInterstitial(CBLocation.LOCATION_DEFAULT))
    {
        mChartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
        Toast.makeText(this, "cached", Toast.LENGTH_SHORT).show();
    }
    else
        {
        mChartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
        Toast.makeText(this,"chaching", Toast.LENGTH_SHORT).show();
        Chartboost.showRewardedVideo(CBLocation.LOCATION_DEFAULT);
        }

  }
}



