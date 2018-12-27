package com.movieapp.konwo.sweetbaking.main.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.movieapp.konwo.sweetbaking.R;
import com.movieapp.konwo.sweetbaking.adapters.RecipesStepsAdapter;
import com.movieapp.konwo.sweetbaking.databinding.ActivityStepsDetailsBinding;
import com.movieapp.konwo.sweetbaking.databinding.ActivityStepsListBackBinding;
import com.movieapp.konwo.sweetbaking.databinding.RecipesStepDetailsBinding;
import com.movieapp.konwo.sweetbaking.main.activities.StepsListActivity;
import com.movieapp.konwo.sweetbaking.models.Recipe;
import com.movieapp.konwo.sweetbaking.models.Steps;
import com.movieapp.konwo.sweetbaking.utilities.Callbacks;

import java.util.List;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class StepDetailsFragmentLarge extends Fragment implements Player.EventListener, View.OnClickListener, Callbacks {

    private static final String EXTRA = "step" ;
    private static final String POSITION = "position";
    private static final String RECETTE = "recipe";
    private static final String LOG_TAG = StepDetailsFragmentLarge.class.getSimpleName();

    PlayerView mPlayerView;
    TextView stepDescription;
    ImageView videoThumbnail;
    TextView noVideo;

    private Context mContext;
    private Steps steps;
    private Recipe recipe;
    private Recipe mRecipe;
   private List<Recipe> mRecipeList;
    private boolean isTablet;
    private String videoUrl;
    private SimpleExoPlayer mExoPlayer;
    private long playerPosition;
    private boolean playWhenReady;
    private int scrennOrientation;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaSource mediaSource;
    private MediaSessionCompat mediaSession;
    private List<Object> objects;

    private static StepDetailsFragmentLarge instance  = null;


    private OnStepClickListener mListener;

    private Steps currentSteps;

    public StepDetailsFragmentLarge(){

    }

//    public StepDetailsFragment( Intent intent) {
//        payloadIntent = intent;
//    }

//    public static StepDetailsFragment newInstance (Intent intent) {
//        StepDetailsFragment fragment = new StepDetailsFragment();
//        Bundle arg = new Bundle();
//        arg.putParcelable(EXTRA, null);
//    }

    public static StepDetailsFragmentLarge newInstance(Steps steps, Recipe recipe) {

        if(null == instance)
            instance = new StepDetailsFragmentLarge();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA, steps);
        bundle.putParcelable(RECETTE, recipe);

        instance.setArguments(bundle);
        Log.e(TAG, "newInstance started ");
        return instance;
    }

    public void createSteps(Steps steps) {
        this.steps = steps;
        videoUrl = steps.getVideoURL();
        initExoPlayer();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            steps = getArguments().getParcelable(EXTRA);
            recipe = getArguments().getParcelable(RECETTE);
        }
        videoUrl = steps != null ? steps.getVideoURL() : null;
        if (savedInstanceState != null) {
            playerPosition = savedInstanceState.getLong(POSITION);
            Toast.makeText(getContext(), "starting from position " + playerPosition , Toast.LENGTH_SHORT).show();
        } else {
            playerPosition = 0;
            Toast.makeText(getContext(), "Startin form zero", Toast.LENGTH_SHORT).show();
        }
        instance = new StepDetailsFragmentLarge(); //this;
    }



    public static StepDetailsFragmentLarge getInstance(){
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Just check out what happens...
        if (null == container) return null;
        ActivityStepsListBackBinding binding = DataBindingUtil.inflate(inflater,R.layout.activity_steps_list_back, container, false);

        binding.setSteps(steps);
        binding.setIngredients(recipe.getIngredients().get(0));
        mContext = getActivity();
        mPlayerView = binding.exoPlayerView;
        videoThumbnail = binding.ivVideoThumbnail;
        stepDescription = binding.tvStepDescription;
        isTablet = getResources().getBoolean(R.bool.istTwoPane);
        scrennOrientation = getResources().getConfiguration().orientation;

        // If screen is in landscape mode, show video in full screen
        // else show nav buttons and indicator
        if (scrennOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullScreenPlayer();
        } else {
            Button nextStep = binding.btnNextStep;
            nextStep.setOnClickListener(this);
            Button previousStep = binding.btnPreviousStep;
            previousStep.setOnClickListener(this);
        }

        // Load the objects from the activity
        objects = ((StepsListActivity)getActivity()).objects;

         //Load the recyclerview...
       // RecyclerView rv =  getView().getRootView().findViewById(R.id.step_list_rv);
              //  (RecyclerView) binding.stepListRv;

        LinearLayoutManager ll = new LinearLayoutManager(getContext());

        ll.setOrientation(LinearLayoutManager.VERTICAL);
        binding.stepListRv.setLayoutManager(ll);
        RecipesStepsAdapter.StepsClickListener listener = (RecipesStepsAdapter.StepsClickListener) getActivity();
       RecipesStepsAdapter stepsAdapter = new RecipesStepsAdapter(getContext(), objects, true, listener);
        binding.stepListRv.setAdapter(
                stepsAdapter
        );
        stepsAdapter.notifyDataSetChanged();
        binding.stepListRv.setHasFixedSize(true);

        return binding.getRoot();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStepClickListener) {
            mListener = (OnStepClickListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + getString(R.string.error_interface));
        }

    }


    // init exoPlayer
    private void initExoPlayer() {
//        Toast.makeText(getContext(), "init playing", Toast.LENGTH_LONG).show();
        // check if mExoPlayer is not null before init new
        if (mExoPlayer == null && !(videoUrl.isEmpty()) ) {

            // Jump out if the player is null
            if(null == mPlayerView) return;
            // show the player
            mPlayerView.setVisibility(View.VISIBLE);
            // default trackselector
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);
            // user agent
            String userAgent = Util.getUserAgent(mContext, getString(R.string.app_name));
            DataSource.Factory factory = new DefaultDataSourceFactory(mContext, userAgent);
            MediaSource mediaSource =
                    new ExtractorMediaSource.Factory(factory).createMediaSource(Uri.parse(videoUrl));
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.seekTo(playerPosition);
            mExoPlayer.setPlayWhenReady(true);
        } else {
            // hide the video view in landscape
            if (scrennOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                mPlayerView.setVisibility(View.GONE);
                videoThumbnail.setVisibility(View.VISIBLE);
                stepDescription.setVisibility(View.VISIBLE);
            } else  {
                // in portrait
                mPlayerView.setVisibility(View.GONE);
                videoThumbnail.setVisibility(View.VISIBLE);
            }
        }
    }

    void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    // method to hide the system UI for full screnn mode
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        Objects.requireNonNull(((AppCompatActivity)
                Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initExoPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mExoPlayer == null)) {
            initExoPlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    private void setFullScreenPlayer() {
        if (!videoUrl.isEmpty() && !isTablet) {
            hideSystemUI();
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playWhenReady && playbackState == Player.STATE_READY) {
            Log.d(LOG_TAG, "Player is playing");
            Toast.makeText(getContext(), "playing", Toast.LENGTH_LONG).show();
//            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer.getCurrentPosition(), 1f);
        } else if (playbackState == Player.STATE_READY) {
            Log.d(LOG_TAG, "Player is paused");
            Toast.makeText(getContext(), "player is pause", Toast.LENGTH_LONG).show();
           // stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer.getCurrentPosition(), 1f);
            playerPosition = mExoPlayer.getCurrentPosition();

        }
//        mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(POSITION, playerPosition);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_next_step:
                mListener.onNextStepClick(steps);
                break;
            case R.id.btn_previous_step:
                mListener.onPreviousStepClick(steps);
                break;
        }
    }

    @Override
    public void callback(Steps steps) {
        if(null == instance)
            Toast.makeText(getContext(), "This is an existing instance", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "This is an existing instance", Toast.LENGTH_SHORT).show();
    }


    public interface OnStepClickListener {
        void onPreviousStepClick(Steps steps);
        void onNextStepClick(Steps steps);

        void play(Steps steps);
    }
}