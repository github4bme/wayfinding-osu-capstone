package com.osucse.wayfinding_osu_capstone;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.HashSet;
import java.util.Set;

public class Settings extends BaseActivity {

    /**
     * Names of the setting locations
     */
    public static final String ACCESSIBLE_ROUTING = "ACCESSIBLE_ROUTING";
    public static final String VISUALLY_IMPAIRED = "VISUALLY_IMPAIRED";
    public static final String SHOW_MAP_HINTS = "SHOW_MAP_HINTS";
    public static final String SHARED_FAVORITES = "SHARED_FAVORITES";
    private static final String APP_SETTINGS = "APP_SETTINGS";

    protected static SharedPreferences settings;
    protected static SharedPreferences.Editor editor;

    /**
     * Instance variables
     */
    private Switch accessibleSwitch;
    private Switch visualSwitch;
    private Button                      favoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        super.onCreateDrawer();

        // Should never be null if BaseActivity is creating settings
        if (settings == null) {
            initializeSettings(this);
        }

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

        // load switch states from memory
        this.accessibleSwitch.setChecked(settings.getBoolean(ACCESSIBLE_ROUTING, false));
        this.visualSwitch.setChecked(settings.getBoolean(VISUALLY_IMPAIRED, false));
    }

    public static void initializeSettings (Activity activity) {
        settings = activity.getSharedPreferences(APP_SETTINGS, MODE_PRIVATE);
    }

    /**
     * Helper method for changing the accessibleSwitch
     * @param bool what to set the switch state to
     */
    private static void accessibleSwitchStateChange (boolean bool) {
        if (settings != null) {
            editor = settings.edit();

            editor.putBoolean(ACCESSIBLE_ROUTING, bool);

            editor.commit();
        }
    }

    /**
     * Helper method for changing the visualSwitch
     * @param bool what to set the switch state to
     */
    private static void visualSwitchStateChange (boolean bool) {
        if (settings != null) {
            editor = settings.edit();

            editor.putBoolean(VISUALLY_IMPAIRED, bool);

            editor.commit();
        }
    }




    public static boolean getAccessibleSetting () {
        // Default to false as answer if Settings page has not been visited
        if (settings == null) {
            return false;
        }
        return settings.getBoolean(ACCESSIBLE_ROUTING, false);
    }

    public static boolean getVisualSetting () {
        // Default to false as answer if Settings page has not been visited
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


    public static boolean getShowMapHintsSetting() {
        if (settings == null) {
            return true;
        }

        return settings.getBoolean(SHOW_MAP_HINTS, true);
    }

    public static void setShowMapHintsSetting(boolean newSetting) {
        if (settings != null) {
            editor = settings.edit();

            editor.putBoolean(SHOW_MAP_HINTS, newSetting);

            editor.commit();
        }
    }
}