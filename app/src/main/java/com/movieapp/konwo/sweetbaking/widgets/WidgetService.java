package com.movieapp.konwo.sweetbaking.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.movieapp.konwo.sweetbaking.R;
import com.movieapp.konwo.sweetbaking.models.Ingredients;
import com.movieapp.konwo.sweetbaking.models.local.IngredientContract;

import java.util.List;

public class WidgetService extends RemoteViewsService {

    private List<Ingredients> ingredients;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(getApplicationContext(), intent);
    }

     class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context mContext;
        private int mAppWidgetId;
        private Cursor mCursor;

//        RemoteListFactory(Context applicationContext) {
//            this.mContext = applicationContext;
//        }

        public  WidgetRemoteViewsFactory(Context context, Intent intent) {

            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
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
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_list_item);

            if (mCursor.getCount() != 0) {
                mCursor.moveToPosition(position);

                remoteViews.setTextViewText(R.id.widget_ingredient_name,
                        mCursor.getString(mCursor.getColumnIndex(IngredientContract.Entry.COLUMN_NAME_INGREDIENTS)));

                String measure =
                        mCursor.getString(mCursor.getColumnIndex(IngredientContract.Entry.COLUMN_NAME_QUANTITY))
                                + " " +
                                mCursor.getString(mCursor.getColumnIndex(IngredientContract.Entry.COLUMN_NAME_MEASURE));

                remoteViews.setTextViewText(R.id.widget_ingredient_measure, measure);
            }

            // Return the remote views object.
            return remoteViews;
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