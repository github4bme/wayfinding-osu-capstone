package com.osucse.wayfinding_osu_capstone;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;


public class Settings extends BaseActivity {

    /**
     * Names of the setting locations
     */
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
        super.onCreateDrawer();

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
}