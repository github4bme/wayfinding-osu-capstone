package com.osucse.wayfinding_osu_capstone;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.osucse.wayfinding_api.Location;
import com.osucse.wayfinding_api.LocationCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


public class LocationList extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        LocationCollection lc = new LocationCollection();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_list, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        new HttpRequestTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, LocationCollection> {
        @Override
        protected LocationCollection doInBackground(Void... params) {
            try {
                final String url = "http://cseosuwintest.cloudapp.net:9000/locations";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                LocationCollection locations = restTemplate.getForObject(url, LocationCollection.class);
                return locations;
            } catch (Exception e) {
                Log.e("LocationList", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(LocationCollection locations) {
            ListView locationListView = (ListView) findViewById(R.id.listView);

            List<String> locationNames = new ArrayList<String>();


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.abc_screen_simple, locationNames);

        locationListView.setAdapter(adapter);
        }

    }
}
