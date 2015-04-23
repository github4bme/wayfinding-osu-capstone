package com.osucse.wayfinding_osu_capstone;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.view.MenuItem;
import android.content.res.Configuration;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.app.ProgressDialog;
import com.osucse.wayfinding_api.BuildingCollection;
import com.osucse.wayfinding_api.TourCollection;

/**
 * Created by aritelesman on 4/22/15.
 */
public class BaseActivity extends ActionBarActivity {
    public DrawerLayout drawerLayout;
    public ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private String[] menuItems;
    public Toolbar toolbar;

    private ProgressDialog progress;

    protected void onCreateDrawer() {
        progress = new ProgressDialog(this);
        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(
                this,                   /* host Activity */
                drawerLayout,          /* DrawerLayout object */
                toolbar,   /* nav drawer image to replace 'Up' caret */
                0,      /* "open drawer" description for accessibility */
                0/* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(R.string.app_name);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                //Log("onDrawerClosed()");
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.menu);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                //Log("onDrawerOpened()");
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        menuItems = getResources().getStringArray(R.array.menu_items);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1,
                menuItems)); //Items is an ArrayList or array with the items you want to put in the Navigation Drawer.

        drawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                goToNavDrawerItem(pos);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void goToNavDrawerItem(int item) {
        Intent intent;
        switch (item) {
            case 0:
                progress.setMessage("Loading... ");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
                new BuildingHttpRequestTask().execute();
                break;
            case 1:
                progress.setMessage("Loading... ");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
                new ToursHttpRequestTask().execute();
                break;
            case 2:
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * A private internal class that handles updating the internal class StartUpTasks
     */
    private class BuildingHttpRequestTask extends AsyncTask<Void, Void, BuildingCollection> {
        @Override
        protected BuildingCollection doInBackground(Void... params) {
            return StartUpTasks.getBuildingsFromServer();
        }

        @Override
        protected void onPostExecute(BuildingCollection buildings) {
            // Clears the "Loading..." message so it is not there when you navigate back to this screen from Map Display
            // This has never given me a threading error, but it is possible that it could
//            TextView loadingListDisplay = (TextView) BaseActivity.this.findViewById(R.id.loading_list_display);
//            loadingListDisplay.setTextSize(20);
//            loadingListDisplay.setText("");
            progress.hide();
            Intent intent = new Intent(BaseActivity.this, SelectSourceLocation.class);
            startActivity(intent);
        }
    }

    /**
     * A private internal class that handles updating the internal class StartUpTasks
     */
    private class ToursHttpRequestTask extends AsyncTask<Void, Void, TourCollection> {
        @Override
        protected TourCollection doInBackground(Void... params) {
            return StartUpTasks.getToursFromServer();
        }

        @Override
        protected void onPostExecute(TourCollection tours) {
            // Clears the "Loading..." message so it is not there when you navigate back to this screen from Map Display
            // This has never given me a threading error, but it is possible that it could
//            TextView loadingListDisplay = (TextView) BaseActivity.this.findViewById(R.id.loading_list_display);
//            loadingListDisplay.setTextSize(20);
//            loadingListDisplay.setText("");
            progress.hide();
            Intent intent = new Intent(BaseActivity.this, SelectTour.class);
            startActivity(intent);
        }
    }
}

