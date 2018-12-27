package com.movieapp.konwo.sweetbaking.main.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.common.annotations.VisibleForTesting;
import com.movieapp.konwo.sweetbaking.R;
import com.movieapp.konwo.sweetbaking.main.fragments.RecipesFragment;
import com.movieapp.konwo.sweetbaking.models.Steps;
import com.movieapp.konwo.sweetbaking.utilities.IdlingResourcesExpresso;

import java.util.Objects;

import androidx.test.espresso.IdlingResource;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.movieapp.konwo.sweetbaking.main.activities.StepsListActivity.CRASH_REPORT;
import static com.movieapp.konwo.sweetbaking.main.activities.StepsListActivity.STEP_DESC;
import static com.movieapp.konwo.sweetbaking.main.activities.StepsListActivity.STEP_ID;
import static com.movieapp.konwo.sweetbaking.main.activities.StepsListActivity.STEP_SHORTDESC;
import static com.movieapp.konwo.sweetbaking.main.activities.StepsListActivity.STEP_THUMBURL;
import static com.movieapp.konwo.sweetbaking.main.activities.StepsListActivity.STEP_VIDEO;
import static com.movieapp.konwo.sweetbaking.main.activities.StepsListActivity.WIDGET_PREF;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

       // setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        }

        RecipesFragment fragment = new RecipesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.recipe_fragment, fragment);
        transaction.commit();

    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return IdlingResourcesExpresso.getIdlingResource();
    }
}
