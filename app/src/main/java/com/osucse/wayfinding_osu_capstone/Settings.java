package com.osucse.wayfinding_osu_capstone;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;


public class Settings extends ActionBarActivity {

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

        // connect private members with view members
        this.accessibleSwitch = (Switch) findViewById(R.id.settings_switch_accessible);
        this.visualSwitch = (Switch) findViewById(R.id.settings_switch_visual);

        // connect to preferences file
        settings = getPreferences(MODE_PRIVATE);

        // load switch states from memory
        this.accessibleSwitch.setChecked(settings.getBoolean(ACCESSIBLE_ROUTING, false));
        this.visualSwitch.setChecked(settings.getBoolean(VISUALLY_IMPAIRED, false));

    }


    /**
     * Listener for the accessible switch
     * @param view
     */
    public static void accessibleSwitchStateChange (View view) {

        editor = settings.edit();

        editor.putBoolean(ACCESSIBLE_ROUTING, ((Switch) view).isChecked());

        editor.commit();
    }

    /**
     * Listener for the visual switch
     * @param view
     */
    public static void visualSwitchStateChange (View view) {

        editor = settings.edit();

        editor.putBoolean(VISUALLY_IMPAIRED, ((Switch) view).isChecked());

        editor.commit();
    }
}