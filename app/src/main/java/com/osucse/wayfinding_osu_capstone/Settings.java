package com.osucse.wayfinding_osu_capstone;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;


public class Settings extends ActionBarActivity {

    /**
     * Names of the setting locations
     */
    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
    public static final String ACCESSIBLE_ROUTING = "ACCESSIBLE_ROUTING";
    public static final String VISUALLY_IMPAIRED = "VISUALLY_IMPAIRED";

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    /**
     * Instance variables
     */
    private Switch                      accessibleSwitch;
    private Switch                      visualSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // connect private members with view members
        this.accessibleSwitch = (Switch) findViewById(R.id.settings_switch_accessible);
        this.visualSwitch = (Switch) findViewById(R.id.settings_switch_visual);

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

        // connect to preferences file
        settings = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

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

    /**
     * This will tell you the value of the ACCESSIBLE_ROUTING
     * @param activity required for fetching preferences (just use this)
     * @return true or false
     */
    public static boolean getAccessibleSetting (Activity activity) {
        return activity.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).getBoolean(ACCESSIBLE_ROUTING, false);
    }

    /**
     * This will tell you the value of the VISUALLY_IMPAIRED
     * @param activity required for fetching preferences (just use this)
     * @return true or false
     */
    public static boolean getVisualSetting (Activity activity) {
        return activity.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).getBoolean(VISUALLY_IMPAIRED, false);
    }
}