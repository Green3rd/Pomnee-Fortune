package green3rd.connect.facebook.pomnee.fortune;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;

import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;



import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import green3rd.connect.facebook.pomnee.fortune.dialog.ConnectNetworkDialog;
import green3rd.connect.facebook.pomnee.fortune.dialog.ConnectNetworkDialog_oneChoice;
import green3rd.connect.server.SharedFunctions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RandomFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RandomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RandomFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private UiLifecycleHelper uiHelper;

    private Bitmap pictureFromWeb;

    private String urlSharedPicture;
    private FacebookDialog shareDialog;

    private boolean isPictureLoaded=false;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RandomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RandomFragment newInstance(String param1, String param2) {
        RandomFragment fragment = new RandomFragment();
        return fragment;
    }

    public RandomFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(getActivity(), null);
        uiHelper.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", "Facebook: "+String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Facebook: Success!");
                /*
                https://developers.facebook.com/docs/android/share#linkshare-handlingresponses
                 */
                boolean didCancel = FacebookDialog.getNativeDialogDidComplete(data);
                String completionGesture = FacebookDialog.getNativeDialogCompletionGesture(data);
                String postId = FacebookDialog.getNativeDialogPostId(data);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new SetUpLoadingImage().execute();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_random, container, false);

        final ImageView shareFacebookImage = (ImageView) rootView.findViewById(R.id.shareButton);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                        new SetUpLoadingImage().execute();

            }
        }, 0, 5000);

        shareFacebookImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                       // Log.d("On Fragment","ACTION_DOWN");
                        shareFacebookImage.setImageResource(R.drawable.share2);
                        if(isNetworkConnected(getActivity().getApplicationContext())) {
                            shareToFacebook();
                        }else{
                            DialogFragment dialog = new ConnectNetworkDialog_oneChoice();
                            dialog.show(getFragmentManager(), "ConnectNetworkDialogFragment_oneChoice");
                            shareFacebookImage.setImageResource(R.drawable.share1);
                        }
                        Log.d("On Fragment","shareToFacebook finished");

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("On Fragment","ACTION_UP");
                       // shareFacebookImage.setImageResource(R.drawable.share1);
                      //

                        break;
                }


                return false;
            }

            private void shareToFacebook() {
                Log.d("On Fragment","shareToFacebook");


                if (FacebookDialog.canPresentShareDialog(getActivity().getApplicationContext(),
                        FacebookDialog.ShareDialogFeature.PHOTOS)) {
                    // Publish the post using the Share Dialog


                    // Publish the post using the Photo Share Dialog
//                    Set<Bitmap> pictureSet = new HashSet<Bitmap>();
//                    pictureSet.add(pictureFromWeb);
//                    FacebookDialog shareDialog = new FacebookDialog.PhotoShareDialogBuilder(getActivity())
//                            .addPhotos(pictureSet)
//                            .build();

                    uiHelper.trackPendingDialogCall(shareDialog.present());

                    //https://developers.facebook.com/docs/graph-api/overview
                    //https://developers.facebook.com/docs/android/graph


                    Log.d("On Fragment","shareToFacebook Picture Fin");
                } else {
                    //publishFeedDialog();
                }

            }
            private boolean isNetworkConnected(Context c) {
                ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conManager.getActiveNetworkInfo();
                return ( netInfo != null && netInfo.isConnected() );
            }

        });
        return rootView;

    }



    private class SetUpLoadingImage extends AsyncTask<Void, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Void... params) {
            connect();
            return null;
        }

        private void connect() {

            Resources res = getResources();

            //Preparing, Share to facebook
            String[] links = res.getStringArray(R.array.link_pictures);
            int randomIndex =(int)(Math.random() * (links.length-1));
            final String foreHeadString = res.getStringArray(R.array.foretell_head)[randomIndex];
            final String foreBodyString = res.getStringArray(R.array.foretell_body)[randomIndex];

            urlSharedPicture = links[randomIndex];
            shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
                    .setLink("https://www.facebook.com/pomnee.gag")
                    //.setLink("https://www.facebook.com/PomNeeFortune/photos/a.777650218998758.1073741828.773909882706125/777650112332102/?type=1")
                    .setPicture(urlSharedPicture)
                    .setCaption("Caption นะครับ")
                    .setDescription(foreBodyString)
                    .setName(foreHeadString)
                    .build();
            Log.d("connect()", "set shareDialog");


            //Download Picture
            try {
                pictureFromWeb = SharedFunctions.getBitmapFromURL(urlSharedPicture);
                String url="https://scontent-sin.xx.fbcdn.net/hphotos-xpf1/v/t1.0-9/983811_1499510547001609_2978230253039628468_n.png?oh=cc92ad2645918fb65129cae4be93cb54&oe=554B2ABC";


                    Log.d("On Touch", "Set fortune picture");

                    //access UI elements from the main thread only.
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // This code will always run on the UI thread, therefore is safe to modify UI elements.
                            ImageView imageViewObg = (ImageView) getView().findViewById(R.id.imageView2);


                            //FYI: http://goo.gl/YZ6XBh
                            int fadeInDuration = 3000; // Configure time values here

                            Animation fadeIn = new AlphaAnimation(0, 1);
                            fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
                            fadeIn.setDuration(fadeInDuration);
                        /*
                            int timeBetween = 200;
                            int fadeOutDuration = 1000;
                            Animation fadeOut = new AlphaAnimation(1, 0);
                            fadeOut.setInterpolator(new AccelerateInterpolator()); // and this
                            fadeOut.setStartOffset(fadeInDuration + timeBetween);
                            fadeOut.setDuration(fadeOutDuration);
                          */
                            AnimationSet animation = new AnimationSet(false); // change to false
                            animation.addAnimation(fadeIn);
                            // animation.addAnimation(fadeOut);
                            animation.setRepeatCount(1);
                            imageViewObg.setAnimation(animation);


                            if (pictureFromWeb != null){
                                Log.d("On Touch", "Set fortune picture, Success");
                                imageViewObg.setImageBitmap(pictureFromWeb);
                                isPictureLoaded=true;
                            }else{
                                Log.d("On Touch", "Set fortune picture, Fail");
                                imageViewObg.setImageResource(R.drawable.bg_empty);
                                isPictureLoaded=false;
                            }



                            //TextView foretellHead = (TextView) getView().findViewById(R.id.foretellHead);
                            TextView foretellBody = (TextView) getView().findViewById(R.id.foretellBody);

                            //foretellHead.setText(foreHeadString);
                            foretellBody.setText(foreBodyString);

                        }
                    });


            }catch (Exception e) {
                Log.d("connect","Fail to Set fortune picture, error: "+ e.getLocalizedMessage());
            }


        }


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
      /* // Communicating with Other Fragments
      try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
