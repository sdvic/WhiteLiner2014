package org.wintrisstech.erik.iaroc;
/**************************************************************************
 * Simplified version 140512A by Erik  Super Happy Version
 * Vic's commit version 140904A
 * version 150122A AndroidStudio version
 * version 150225B AndroidStudio version
 * version 160215 Vic simplified version
 **************************************************************************/

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.Locale;

import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.irobot.ioio.SimpleIRobotCreate;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * This is the main activity of the 2016 iARoC demo application.
 * There should be no need to modify this class. Modify Lada instead.
 *
 * @author Erik Colban
 */
public class Dashboard extends IOIOActivity
{
    /**
     * Text view that contains all logged messages
     */
    private TextView mText;
    private ScrollView scroller;
    /**
     * A Lada instance
     */
    private Lada kalina;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        /*
		 * Since the android device is carried by the iRobot Create, we want to
		 * prevent a change of orientation, which would cause the activity to
		 * pause.
		 */
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.main);
        mText = (TextView) findViewById(R.id.text);
        scroller = (ScrollView) findViewById(R.id.scroller);
        log(getString(R.string.wait_ioio));
    }

    @Override
    public void onPause()
    {
        if (kalina != null)
        {
            log("Pausing");
        }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    public void onInit(int arg0)
    {
    }

    @Override
    public IOIOLooper createIOIOLooper()
    {
        return new IOIOLooper()
        {

            public void setup(IOIO ioio) throws ConnectionLostException,
                    InterruptedException
            {
				/*
				 * When the setup() method is called the IOIO is connected.
				 */
                log(getString(R.string.ioio_connected));

				/*
				 * Establish communication between the android and the iRobot
				 * Create through the IOIO board.
				 */
                log(getString(R.string.wait_create));
                IRobotCreateInterface iRobotCreate = new SimpleIRobotCreate(ioio);
                log(getString(R.string.create_connected));

				/*
				 * Get a Lada (built on the iRobot Create) and let it go... The
				 * ioio_ instance is passed to the constructor in case it is
				 * needed to establish connections to other peripherals, such as
				 * sensors that are not part of the iRobot Create.
				 */
                kalina = new Lada(ioio, iRobotCreate, Dashboard.this);
                kalina.initialize();
            }

            public void loop() throws ConnectionLostException,
                    InterruptedException
            {
                kalina.loop();
            }

            public void disconnected()
            {
                log(getString(R.string.ioio_disconnected));
            }

            public void incompatible()
            {
            }
        };
    }

    /**
     * Writes a message to the Dashboard instance.
     *
     * @param msg the message to write
     */
    public void log(final String msg)
    {
        runOnUiThread(new Runnable()
        {

            public void run()
            {
                mText.append(msg);
                mText.append("\n");
                scroller.smoothScrollTo(0, mText.getBottom());
            }
        });
    }
}
