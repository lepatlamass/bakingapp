package com.movieapp.konwo.sweetbaking.main.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.movieapp.konwo.sweetbaking.R;
import com.movieapp.konwo.sweetbaking.databinding.ActivityStepsDetailsBinding;
import com.movieapp.konwo.sweetbaking.main.fragments.StepDetailsFragment;
import com.movieapp.konwo.sweetbaking.models.Steps;

import java.util.List;

public class StepsDetailsActivity extends AppCompatActivity implements StepDetailsFragment.OnStepClickListener {

    public static final String EXTRA = "step";
    public static final String EXTRA_NAME = "recipe_name";
    public static final String EXTRA_LIST = "recipe_step_list" ;
    private static final String STEP_LIST = "current_list";
    private static final String STEP_INDEX = "index";

    private Steps steps;
    private List<Steps> stepsList;
    int stepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStepsDetailsBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_steps_details);

        //Toolbar toolbar = binding.detailListToolbar;
       // setSupportActionBar(toolbar);

        String mRecipeName = getIntent().getStringExtra(EXTRA_NAME);
        setTitle(mRecipeName);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            steps = getIntent().getParcelableExtra((EXTRA));
            stepsList = getIntent().getParcelableArrayListExtra(StepsDetailsActivity.EXTRA_LIST);
            addFragment();
        } else {
            stepsList = savedInstanceState.getParcelableArrayList(STEP_LIST);
            stepIndex = savedInstanceState.getInt(STEP_INDEX);
        }

    }

    private void addFragment() {
        StepDetailsFragment fragment = StepDetailsFragment.newInstance(steps);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.detail_container, fragment)
                .commit();
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

    }

    private void showStep(Steps steps) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        StepDetailsFragment fragment = StepDetailsFragment.newInstance(steps);
        transaction.replace(R.id.detail_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}