package com.movieapp.konwo.sweetbaking.widgets;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.movieapp.konwo.sweetbaking.R;
import com.movieapp.konwo.sweetbaking.models.Ingredients;

import java.util.List;

public class WidgetService extends RemoteViewsService {

    private List<Ingredients> ingredients;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteListFactory(getApplicationContext());
    }

    private class RemoteListFactory implements RemoteViewsFactory {

        Context mContext;

        RemoteListFactory(Context applicationContext) {
            this.mContext = applicationContext;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            ingredients = RecipeWidgetProvider.ingredients;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (ingredients == null) return 0;
            return ingredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget);
            Ingredients ingredient = ingredients.get(position);

            String measure = String.valueOf(ingredient.getQuantity());
            String widget_ingredients = ingredient.getIngredient();
            views.setTextViewText(R.id.widget_ing, widget_ingredients  + "   " + measure);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}