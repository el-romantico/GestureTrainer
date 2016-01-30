package com.elromantico.client.gestures.recorder;

import java.util.List;

public interface GestureRecorderListener {

	void gestureTrained(float[][] values);

	void gestureRecognized(float[][] values);
}
