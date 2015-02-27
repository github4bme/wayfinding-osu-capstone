package com.osucse.wayfinding_osu_capstone;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.osucse.wayfinding_api.Location;
import com.osucse.wayfinding_api.LocationCollection;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LocationList extends ActionBarActivity {

    public static ArrayAdapter<String> adapter;
    public static ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        adapter.clear();

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String item = ((TextView)view).getText().toString();
                TextView tv = (TextView)findViewById(R.id.textView8);
                tv.setText(item);
            }
        });
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
        //new HttpRequestTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            new HttpRequestTask().execute();
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
            final List<String> locationNames = new ArrayList<String>();

            for(Location l : locations.getLocations())
            {
                if(l.getName() != null) {
                    locationNames.add(l.getName());
                }
            }

            Collections.sort(locationNames);

            TextView tv = (TextView) findViewById(R.id.textView8);
            tv.setText("Select One:");

            /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(LocationList.this, R.layout.abc_list_menu_item_layout, locationNames);

            locationListView.setAdapter(adapter);*/

                    adapter.clear();
                    adapter.addAll(locationNames);

        }

    }
}
