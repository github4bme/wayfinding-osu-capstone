package com.osucse.wayfinding_osu_capstone;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;


public class Settings extends ActionBarActivity {

    /**
     * Names of the setting locations
     */
    public static final String ACCESSIBLE_ROUTING = "ACCESSIBLE_ROUTING";
    public static final String VISUALLY_IMPAIRED = "VISUALLY_IMPAIRED";

    private static SharedPreferences settings;

    /**
     * Instance variables
     */
    private Switch                      accessibleSwitch;
    private Switch                      visualSwitch;
    private SharedPreferences.Editor    editor;

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
        this.accessibleSwitch.setChecked(getSetting(ACCESSIBLE_ROUTING));
        this.visualSwitch.setChecked(getSetting(VISUALLY_IMPAIRED));

        // for adding commits
        editor = settings.edit();
    }


    /**
     * Listener for the accessible switch
     * @param view
     */
    public void accessibleSwitchStateChange (View view) {

        this.editor.putBoolean(ACCESSIBLE_ROUTING, ((Switch) view).isChecked());

        this.editor.commit();
    }

    /**
     * Listener for the visual switch
     * @param view
     */
    public void visualSwitchStateChange (View view) {

        this.editor.putBoolean(VISUALLY_IMPAIRED, ((Switch) view).isChecked());

        this.editor.commit();
    }

    /**
     * Used to get the current values in the settings memory
     * @param setting the setting to get the value of (ACCESSIBLE_ROUTING, VISUALLY_IMPAIRED, ...)
     * @return the setting and false if it is not set
     */
    public static boolean getSetting(String setting) {
        return settings.getBoolean(setting, false);
    }
}