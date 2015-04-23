package com.osucse.wayfinding_osu_capstone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.HashSet;
import java.util.Set;


public class Settings extends ActionBarActivity {

    /**
     * Names of the setting locations
     */
    public static final String ACCESSIBLE_ROUTING = "ACCESSIBLE_ROUTING";
    public static final String VISUALLY_IMPAIRED = "VISUALLY_IMPAIRED";
    public static final String SHARED_FAVORITES = "SHARED_FAVORITES";

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    /**
     * Instance variables
     */
    private Switch                      accessibleSwitch;
    private Switch                      visualSwitch;
    private Button                      favoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // connect private members with view members
        this.accessibleSwitch = (Switch) findViewById(R.id.settings_switch_accessible);
        this.visualSwitch = (Switch) findViewById(R.id.settings_switch_visual);
        this.favoritesButton = (Button) findViewById(R.id.settings_button_favorites);

        // listener for the accessibleSwitch
        this.accessibleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.accessibleSwitchStateChange(isChecked);
            }
        });

        // listener for the visualSwitch
        this.visualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.visualSwitchStateChange(isChecked);
            }
        });

        // listener for teh favoritesButton
        this.favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create an intent
                Intent intent = new Intent(Settings.this, SelectFavoriteBuildings.class);

                // start the intent
                startActivity(intent);
            }
        });

        // connect to preferences file
        settings = getPreferences(MODE_PRIVATE);

        // load switch states from memory
        this.accessibleSwitch.setChecked(settings.getBoolean(ACCESSIBLE_ROUTING, false));
        this.visualSwitch.setChecked(settings.getBoolean(VISUALLY_IMPAIRED, false));

    }


    /**
     * Helper method for changing the accessibleSwitch
     * @param bool what to set the switch state to
     */
    private static void accessibleSwitchStateChange (Boolean bool) {

        editor = settings.edit();

        editor.putBoolean(ACCESSIBLE_ROUTING, bool);

        editor.commit();
    }

    /**
     * Helper method for changing the visualSwitch
     * @param bool what to set the switch state to
     */
    private static void visualSwitchStateChange (boolean bool) {

        editor = settings.edit();

        editor.putBoolean(VISUALLY_IMPAIRED, bool);

        editor.commit();
    }


    public static boolean getAccessibleSetting () {
        return settings.getBoolean(ACCESSIBLE_ROUTING, false);
    }

    public static boolean getVisualSetting () {
        if (settings == null) {
            return false;
        }
        return settings.getBoolean(VISUALLY_IMPAIRED, false);
    }

    public static Set<String> getFavoritesFromSettings () {
        return settings.getStringSet(SHARED_FAVORITES, new HashSet<String>());
    }

    public static void setFavoritesToSettings (Set<String> favorites) {
        editor = settings.edit();

        editor.putStringSet(SHARED_FAVORITES, favorites);

        editor.commit();
    }

}