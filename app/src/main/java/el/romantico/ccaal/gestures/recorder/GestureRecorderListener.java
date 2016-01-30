package el.romantico.ccaal.gestures.recorder;

import java.util.List;

public interface GestureRecorderListener {

	void gestureTrained(List<float[]> values);

	void gestureRecognized(List<float[]> values);
}
