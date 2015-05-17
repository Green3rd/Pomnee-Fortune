package green3rd.connect.facebook.pomnee.fortune;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Context;

import android.content.res.Configuration;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;

import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.google.android.gms.ads.InterstitialAd;


import green3rd.connect.facebook.pomnee.fortune.dialog.ConnectNetworkDialog;

/*
Itthikorns-MacBook-Pro:~ Akamu$ keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
Enter keystore password: benevolent
ga0RGNYHvNM5d0SLGQfpQWAPGJ8=

 */

//FYI: https://developers.facebook.com/quickstarts/329175223809971/?platform=android
//FYI: https://developers.facebook.com/docs/android/share#sharing_photos_prerequisites
public class MainActivity extends FragmentActivity  implements ConnectNetworkDialog.ConnectNetworkDialogListener{


    public static FragmentManager fragmentManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            fragmentManager = getFragmentManager();
            Log.d("onCreate","getFragmentManager "+fragmentManager.getBackStackEntryCount());
            //getSupportFragmentManager()
            fragmentManager
                    .beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("Main", "onConfigurationChanged" );
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed()
    {
        // Catch back action and pops from backstack
        // (if you called previously to addToBackStack() in your transaction)
        if (fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStack();
        }
        // Default action on back pressed
        else super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //Do nothing, waiting user to turn on his internet connection.
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        PlaceholderFragment.switchToRandomPage();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        InterstitialAd mInterstitialAd;

        public PlaceholderFragment() {
        }



        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);

            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getView().setBackgroundResource(R.drawable.bg);
                //Toast.makeText(getActivity().getApplicationContext(), "landscape", Toast.LENGTH_SHORT).show();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                getView().setBackgroundResource(R.drawable.bg_portrait);
                //Toast.makeText(getActivity().getApplicationContext(), "portrait", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //rootView.setOnTouchListener(this);
            Log.d("On Fragment","Ready!");

            final ImageView buttonImage = (ImageView) rootView.findViewById(R.id.imageView1);


            if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
                rootView.setBackgroundResource(R.drawable.bg_portrait);
            //else Configuration.ORIENTATION_LANDSCAPE


            buttonImage.setOnTouchListener (new View.OnTouchListener()
            {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            buttonImage.setImageResource(R.drawable.button2);
                            break;
                        case MotionEvent.ACTION_UP:
                            buttonImage.setImageResource(R.drawable.button1);
                            if(isNetworkConnected(getActivity().getApplicationContext())) {
                                if (mInterstitialAd.isLoaded()) {
                                    mInterstitialAd.show();
                                }else {
                                    switchToRandomPage();
                                }
                            }else{
                                DialogFragment dialog = new ConnectNetworkDialog();
                                dialog.show(getFragmentManager(), "ConnectNetworkDialogFragment");
                            }
                            break;
                    }


                    return false;
                }

                private boolean isNetworkConnected(Context c) {
                    ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = conManager.getActiveNetworkInfo();
                    return ( netInfo != null && netInfo.isConnected() );
                }


            });


            AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            mInterstitialAd = new InterstitialAd(getActivity().getApplicationContext());
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            requestNewInterstitial();

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                    switchToRandomPage();
                }
            });

            return rootView;
        }


        private void requestNewInterstitial() {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("YOUR_DEVICE_HASH")
                    .build();

            mInterstitialAd.loadAd(adRequest);
        }

        protected static void switchToRandomPage(){

            RandomFragment newFragment = new RandomFragment();

            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction()
                    //.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    //.setCustomAnimations(R.anim.enter, R.anim.exit)// Just the forward animation, not including backward.
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.container, newFragment)
                    .addToBackStack(null)  //Allow the user to press back to the previous fragment (By Calling popBackStack afterward).
                    .commitAllowingStateLoss();

        }


    }



}



