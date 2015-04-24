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
 *
 * This is the navigation drawer activity. It is built to be extended by every other activity we use.
 *
 */
public class BaseActivity extends ActionBarActivity {
    public DrawerLayout drawerLayout;
    public ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private String[] menuItems;
    public Toolbar toolbar;
    protected static final int HOME = 0;
    protected static final int DIRECTIONS = 1;
    protected static final int TOURS = 2;
    protected static final int SETTINGS = 3;
    private ProgressDialog progress;

    //creates navigation drawer
    protected void onCreateDrawer() {

        Settings.initializeSettings(this);

        progress = new ProgressDialog(this);
        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(
                this,                   /* host Activity */
                drawerLayout,          /* DrawerLayout object */
                toolbar,
                0,
                0
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(R.string.app_name);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.menu);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        menuItems = getResources().getStringArray(R.array.menu_items);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1,
                menuItems)); //menuItems is array of list items defined in strings.xml

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

    /*
    Chooses which activity to start from the navigation bar.
     */
    private void goToNavDrawerItem(int item) {
        Intent intent;
        switch (item) {
            case HOME:
                if(this.getClass() == InitialActivity.class)
                {
                    drawerLayout.closeDrawers();
                    break;
                }
                intent = new Intent(this, InitialActivity.class);
                startActivity(intent);
                break;
            case DIRECTIONS:
                drawerLayout.closeDrawers();
                progress.setMessage("Loading... ");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
                new BuildingHttpRequestTask().execute();
                break;
            case TOURS:
                drawerLayout.closeDrawers();
                progress.setMessage("Loading... ");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
                new ToursHttpRequestTask().execute();
                break;
            case SETTINGS:
                drawerLayout.closeDrawers();
                //loading not needed since this does not communicate with server
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
            return BuildingListAdapter.getBuildingsFromServer();
        }

        @Override
        protected void onPostExecute(BuildingCollection buildings) {
            //clear loading bar
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
            return BuildingListAdapter.getToursFromServer();
        }

        @Override
        protected void onPostExecute(TourCollection tours) {
            //Clear loading bar
            progress.hide();
            Intent intent = new Intent(BaseActivity.this, SelectTour.class);
            startActivity(intent);
        }
    }
}

