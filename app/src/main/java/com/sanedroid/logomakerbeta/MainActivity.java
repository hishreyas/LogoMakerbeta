package com.sanedroid.logomakerbeta;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.shashi.mysticker.StickerView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class MainActivity extends AppCompatActivity {
    GridView mGridView;
    ListAdapter mListAdapter;
    ArrayList<com.sanedroid.logomakerbeta.ListAdapter.Items> object = new ArrayList();
    TextView mTextView;
    ProgressDialog mProgressDialog;
    SwipeRefreshLayout mLayoutSwipe;
    RelativeLayout mErrorlayout;
    ImageButton offlinebutton;
    ImageButton sharebutton;
    Typeface mTypeFace;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageRef;
    Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mGridView =findViewById(R.id.mainGridView);
        mTextView = findViewById(R.id.menu_layoutTextView);
        mLayoutSwipe=findViewById(R.id.refresh);
        mErrorlayout=findViewById(R.id.error_layout);
        offlinebutton=findViewById(R.id.downloaded);
        sharebutton=findViewById(R.id.menu_layoutshareButton);


        //set font of title
        mTypeFace = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.path));
        mTextView.setTypeface(mTypeFace);

       //check for permission
        checkpermisssion();

        //initialize FireBase if child is root add mStorageRef=mFirebaseStorage.getReferences().child('root_name');
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef= mFirebaseStorage.getReference();

        // initialize ads
        intializeads(this);

        //initialize progress dialog and get image data

        mProgressDialog = new ProgressDialog(this);
        getimgdata(mStorageRef);

        // intent used to move from home page to list page

        //click event on loaded items

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View convert, int position, long db) {
                com.sanedroid.logomakerbeta.ListAdapter.Items mItems = object.get(position);

                StorageReference mtemp = mFirebaseStorage.getReference().child(mItems.getCollection_name());
                Intent mIntentdata = new Intent(MainActivity.this, ListImages.class);

                mIntentdata.putExtra(getResources().getString(R.string.key_data), mItems.getCollection_name());
                startActivity(mIntentdata);

         //this code will executed when user will click on items
                // TODO: Implement this method
            }


        });

        //event on swipe to refresh

        mLayoutSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                object.clear();
                getimgdata(mStorageRef);
            }
        });

        //download icon click event

        offlinebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent mIntentoffline = new Intent(MainActivity.this, ListImages.class);
                mIntentoffline.putExtra(getResources().getString(R.string.offline_key),getResources().getString(R.string.offline_value));
                startActivity(mIntentoffline);
            }
        });

        // event on share icon

        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent share=new Intent();
                share(share);
            }
        });

    }


//function to load options
    @SuppressLint("RestrictedApi")
    public void popupMenuItem(View v) {

        MenuBuilder menuBuilder = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.items_menu, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, v);
        optionsMenu.setForceShowIcon(true);
        optionsMenu.show();

        for (int i = 0; i < menuBuilder.size(); i++) {
            menuBuilder.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.about:

                            displaydialog(MainActivity.this, R.layout.dialog_about, null);
                            break;

                        case R.id.privacy:

                            displaydialog(MainActivity.this, R.layout.dilog_privacy, null);
                            break;
                    }

                    return true;
                }
            });
        }
    }

    //function to get image directory in Firebase

    void getimgdata(StorageReference storageReference)
    {

        progress(mProgressDialog,this);

     if(checknetwork(getApplicationContext())) {

          mErrorlayout.setVisibility(View.GONE);
          storageReference.listAll()
                 .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                           @Override
                                           public void onSuccess(ListResult listResult) {

                                               for (StorageReference prefix : listResult.getPrefixes())
                                               {
                                                   // All the prefixes under listRef.
                                                   if (prefix.getName().contentEquals(getResources().getString(R.string.ket_frame)))
                                                   {


                                                   }
                                                   else
                                                       {
                                                       object.add(new com.sanedroid.logomakerbeta.ListAdapter.Items(R.drawable.collection_icon, prefix.getName(), null));
                                                       // TODO: Implement this
                                                   }                                            // You may call listAll() recursively on them.

                                               }


                                               mListAdapter = new com.sanedroid.logomakerbeta.ListAdapter(MainActivity.this, object);
                                               mGridView.setAdapter(mListAdapter);
                                               mProgressDialog.dismiss();
                                               mLayoutSwipe.setRefreshing(false);
                                           }

                                       }


                 )
                 .addOnFailureListener(
                         new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         mProgressDialog.dismiss();

                         Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                         // Uh-oh, an error occurred!
                     }
                 });

     }
     else
         {

         mErrorlayout.setVisibility(View.VISIBLE);
         mProgressDialog.dismiss();
         mLayoutSwipe.setRefreshing(false);

     }

    }

    //function to show progress dialog

    public void progress(ProgressDialog mProgressDialog,Context mcontext)
    {

        mProgressDialog.create();
        mProgressDialog.setMessage(mcontext.getResources().getString(R.string.progress_messages));
        mProgressDialog.show();
        mProgressDialog.setCanceledOnTouchOutside(false);


    }

    //function to show custom dialogs

    public void displaydialog(final Context context, int Resd, String child)
    {

         ProgressDialog mProgress = new ProgressDialog(context);
         Dialog mDialog = new Dialog(context);
         mDialog.setContentView(Resd);
         mDialog.show();
    }

    //function to check permission

    public void  checkpermisssion()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        }

    }

    //check network is connected

    public boolean checknetwork(Context cnt)
    {
        NetworkInfo mNetworkinfo=null;
        final ConnectivityManager conmgr=(ConnectivityManager)cnt.getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkinfo=conmgr.getActiveNetworkInfo();

        if(mNetworkinfo!=null && mNetworkinfo.getType()==ConnectivityManager.TYPE_WIFI )
        {

           return true;
        }
        else if(mNetworkinfo!=null && mNetworkinfo.getType()==ConnectivityManager.TYPE_MOBILE){
            return true;
        }
        else {
            return false;
        }
    }

    //function for sharing app

    public  void share(Intent share)
    {

        share.setAction(Intent.ACTION_SEND);
        share.setType(getResources().getString(R.string.type_intent_text));
        share.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.share_text));
        startActivity(Intent.createChooser(share,getResources().getString(R.string.file_chooser_title)));
    }

    //function for  showing ads

public void intializeads(final Context mContext) {
    MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
        @Override
        public void onInitializationComplete(InitializationStatus initializationStatus) {


        }
    });
    final InterstitialAd mInterstitialAd;
    mInterstitialAd = new InterstitialAd(mContext);
    mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_ad_id));

    final AdRequest mAdrequest = new AdRequest.Builder().build();

    mInterstitialAd.loadAd(mAdrequest);
    mInterstitialAd.setAdListener(new AdListener() {
        @Override
        public void onAdLoaded() {
            mInterstitialAd.show();

            Toast.makeText(mContext, "Ad Loaded", Toast.LENGTH_SHORT).show();
            // Code to be executed when an ad finishes loading.
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            Toast.makeText(mContext, "Ad Not Loaded", Toast.LENGTH_SHORT).show();
            // Code to be executed when an ad request fails.
        }

        @Override
        public void onAdOpened() {
            Toast.makeText(mContext, "Ad Opened", Toast.LENGTH_SHORT).show();
            // Code to be executed when the ad is displayed.
        }

        @Override
        public void onAdClicked() {

            // Code to be executed when the user clicks on an ad.
        }

        @Override
        public void onAdLeftApplication() {
            mInterstitialAd.show();
            Toast.makeText(mContext, "Ad App Left", Toast.LENGTH_SHORT).show();
            // Code to be executed when the user has left the app.
        }

        @Override
        public void onAdClosed() {
            Toast.makeText(mContext, "Ad Closed", Toast.LENGTH_SHORT).show();
            // Code to be executed when the interstitial ad is closed.
        }
     });

    }
}


