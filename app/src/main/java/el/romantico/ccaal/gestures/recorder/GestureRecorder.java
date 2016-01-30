/*
 * GestureRecorder.java
 *
 * Created: 18.08.2011
 *
 * Copyright (C) 2011 Robert Nesselrath
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package el.romantico.ccaal.gestures.recorder;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class GestureRecorder implements SensorEventListener {

	public enum State {
		TRAINING, TRAINED, RECOGNIZING, RECOGNIZED, NO_OP
	};

	SensorManager sensorManager;

	ArrayList<float[]> gestureValues = new ArrayList<float[]>();
	Context context;
	GestureRecorderListener listener;
	State state = State.NO_OP;

	public GestureRecorder(Context context) {
		this.context = context;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		float[] value = {
            sensorEvent.values[SensorManager.DATA_X],
            sensorEvent.values[SensorManager.DATA_Y],
            sensorEvent.values[SensorManager.DATA_Z]
        };

		switch (state) {
        case TRAINING:
        case RECOGNIZING:
            gestureValues.add(value);
            break;
		case TRAINED:
            if (gestureValues != null && gestureValues.size() != 0) {
                listener.gestureTrained(gestureValues);
            }
			gestureValues = new ArrayList<float[]>();
            state = State.NO_OP;
            break;
        case RECOGNIZED:
            listener.gestureRecognized(gestureValues);
            gestureValues = new ArrayList<>();
            state = State.NO_OP;
            break;
		}
	}

	public void registerListener(GestureRecorderListener listener) {
		this.listener = listener;
		start();
	}

	public void setState(State state) {
		this.state = state;
	}

	public void start() {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
	}

	public void stop() {
		sensorManager.unregisterListener(this);
	}

	public void unregisterListener(GestureRecorderListener listener) {
		this.listener = null;
		stop();
	}

	public void pause(boolean b) {
		if (b) {
			sensorManager.unregisterListener(this);
		} else {
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		}
	}

}