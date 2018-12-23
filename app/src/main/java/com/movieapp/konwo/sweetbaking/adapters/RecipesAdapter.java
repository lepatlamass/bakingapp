package com.movieapp.konwo.sweetbaking.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.movieapp.konwo.sweetbaking.R;
import com.movieapp.konwo.sweetbaking.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private static final String RECIPES_KEY = "recipe";

    private List<Recipe> mRecipes;
    private Context mContext;
    private RecipeClickListener mListener;

    public RecipesAdapter(Context context, RecipeClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View root = LayoutInflater
                .from(viewGroup.getContext()).inflate(R.layout.recipes_item, viewGroup, false);

        return new RecipeViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeViewHolder holder, int i) {
        final Recipe recipeItem = mRecipes.get(i);
        // set the recipe name
        holder.tv_recipe_name.setText(recipeItem.getName());
        // the recipe image if there's one
//        String img = recipeItem.getImage();
//        if (!img.isEmpty()) {
//            Picasso.get()
//                    .load(img)
//                    .into(holder.iv_recipe);
//        }
    }

    @Override
    public int getItemCount() {
        return mRecipes == null ? 0 : mRecipes.size();
    }

    public void setData(List<Recipe> mRecipeList) {
        mRecipes = mRecipeList;
        notifyDataSetChanged();
    }

    public interface RecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_recipe_name)
        TextView tv_recipe_name;
//        @BindView(R.id.iv_recipe_image)
//        AppCompatImageView iv_recipe;


        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Recipe recipe = mRecipes.get(position);
            mListener.onRecipeClick(recipe);
        }
    }
}
