package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class SelectSourceLocation extends ActionBarActivity {

    public static ArrayAdapter<String> adapter;
    public static ListView listView;
    public static EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_source_location);

        // connect activity to layout items
        listView = (ListView) findViewById(R.id.source_list);
        editText = (EditText) findViewById(R.id.source_list_search);

        // creates adapter and attaches it to the listview
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, StartUpTasks.getLocationList());
        listView.setAdapter(adapter);

        // create listeners for the edittext and listview items
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SelectSourceLocation.this.adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //possibly resort list...
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                String selectedItem = ((TextView)view).getText().toString();
                Intent intent = new Intent(SelectSourceLocation.this, SelectDestinationLocation.class);
                intent.putExtra("SOURCE_LOCATION",selectedItem);
                startActivity(intent);
                SelectSourceLocation.this.finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_source_location, menu);
        return true;
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


}
