package com.movieapp.konwo.sweetbaking.main.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.movieapp.konwo.sweetbaking.R;
import com.movieapp.konwo.sweetbaking.adapters.RecipesStepsAdapter;
import com.movieapp.konwo.sweetbaking.main.fragments.StepDetailsFragment;
import com.movieapp.konwo.sweetbaking.main.fragments.StepDetailsFragmentLarge;
import com.movieapp.konwo.sweetbaking.models.Ingredients;
import com.movieapp.konwo.sweetbaking.models.Recipe;
import com.movieapp.konwo.sweetbaking.models.Steps;
import com.movieapp.konwo.sweetbaking.utilities.Callbacks;
import com.movieapp.konwo.sweetbaking.widgets.RecipeWidgetProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsListActivity extends AppCompatActivity
        implements RecipesStepsAdapter.StepsClickListener, StepDetailsFragment.OnStepClickListener {

    public static final String INTENT_EXTRA = "recipe";
    public static final String WIDGET_PREF = "widget_prefs";
    public static final String ID_PREF = "id";
    public static final String NAME_PREF = "name";

    private boolean isTwoPane;
    private int mRecipeId;
    private List<Steps> stepsList;
    private String mRecipeName;

    private Recipe recipe;

    @BindView(R.id.step_list_rv)
    RecyclerView mRecyclerView;
    public ArrayList<Object> objects;

    private RecipesStepsAdapter mAdapter;
    int stepIndex;

    public StepDetailsFragment  videoLoading = new StepDetailsFragment();

    public void setvideoloading(StepDetailsFragment detailsFragment){
        videoLoading = detailsFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_list);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        objects = new ArrayList<>();

        // get intent extras
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        assert intent != null;
        if (intent.hasExtra(INTENT_EXTRA)) {
            recipe = getIntent().getParcelableExtra(INTENT_EXTRA);
            mRecipeId = recipe.getId();
            mRecipeName = recipe.getName();
            List<Ingredients> ingredients = recipe.getIngredients();
            stepsList = recipe.getSteps();
            String mRecipeName = recipe.getName();
            objects.addAll(ingredients);
            objects.addAll(stepsList);
            setTitle(mRecipeName);
        }


        if (findViewById(R.id.guideline) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            isTwoPane = true;
        }

        initViews();

    }

    private void initViews() {
        if(!isTwoPane){
            mAdapter = new RecipesStepsAdapter(this, objects, isTwoPane, this);
            assert mRecyclerView != null;
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private void closeOnError() {
        finish();
    }


    @Override
    public void onStepClick(Steps steps) {
        if (steps != null) {
            if (isTwoPane) {
                Toast.makeText(this, "This is a click from the two pane view", Toast.LENGTH_SHORT).show();
                //Callbacks cb = (Callbacks)getSupportFragmentManager().getFragments().get(0);
                //cb.callback(steps);
                //videoLoading.createSteps(steps);
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, StepDetailsFragmentLarge.newInstance(steps, recipe)).commit();
            } else {
                Intent intent = new Intent(this, StepsDetailsActivity.class);
                intent.putExtra(StepsDetailsActivity.EXTRA, steps);
                intent.putExtra(StepsDetailsActivity.EXTRA_NAME, mRecipeName);
                intent.putParcelableArrayListExtra(StepsDetailsActivity.EXTRA_LIST,
                        (ArrayList<? extends Parcelable>) stepsList);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_widget_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_widget:
                addToPrefsForWidget();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToPrefsForWidget() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(WIDGET_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ID_PREF, mRecipeId);
        editor.putString(NAME_PREF, mRecipeName);
        editor.apply();

        // add selected recipe to widget
        RecipeWidgetProvider.updateWidget(this);
    }

    @Override
    public void onPreviousStepClick(Steps steps) {
        stepIndex = steps.getId();
        if (stepIndex > 0) {
            showStep(stepsList.get(stepIndex - 1));
        } else {
            finish();
        }
    }

    @Override
    public void onNextStepClick(Steps steps) {
        stepIndex = steps.getId();
        if (stepIndex < stepsList.size() - 1) {
            showStep(stepsList.get(stepIndex + 1));
        } else {
            finish();
        }
    }

    @Override
    public void play(Steps steps) {
        StepDetailsFragment.getInstance().createSteps(steps);
    }

    private void showStep(Steps steps) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        StepDetailsFragment fragment = StepDetailsFragment.newInstance(steps);
        transaction.replace(R.id.step_detail_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
