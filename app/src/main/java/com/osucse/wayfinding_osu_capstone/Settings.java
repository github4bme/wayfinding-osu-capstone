package com.osucse.wayfinding_osu_capstone;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;


public class Settings extends ActionBarActivity {

    public static final String ACCESSIBLE_ROUTING = "ACCESSIBLE_ROUTING";
    public static final String VISUALLY_IMPAIRED = "VISUALLY_IMPAIRED";

    private Switch accessibleSwitch;
    private Switch visualSwitch;
    private Switch grailSwitch;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // connect private members with view members
        this.accessibleSwitch = (Switch) findViewById(R.id.settings_switch_accessible);
        this.visualSwitch = (Switch) findViewById(R.id.settings_switch_visual);
        this.grailSwitch = (Switch) findViewById(R.id.settings_switch_grail);

        // connect to preferences file
        settings = getPreferences(MODE_PRIVATE);

        // load switch states from memory
        this.accessibleSwitch.setChecked(settings.getBoolean(ACCESSIBLE_ROUTING, false));
        this.visualSwitch.setChecked(settings.getBoolean(VISUALLY_IMPAIRED, false));
        this.grailSwitch.setChecked(settings.getBoolean("grail", false));

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
     * Listener for the grail switch
     * @param view
     */
    public void grailSwitchStateChange (View view) {

        this.editor.putBoolean("grail", ((Switch) view).isChecked());

        this.editor.commit();
    }


}
