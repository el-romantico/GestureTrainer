package el.romantico.ccaal.gestures;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import el.romantico.ccaal.gestures.classifier.Distribution;
import el.romantico.ccaal.gestures.classifier.GestureClassifier;
import el.romantico.ccaal.gestures.classifier.featureExtraction.NormedGridExtractor;
import el.romantico.ccaal.gestures.recorder.GestureRecorder;
import el.romantico.ccaal.gestures.recorder.GestureRecorderListener;

public class GestureRecognitionService extends Service implements GestureRecorderListener {

    public class GestureRecognitionServiceBinder extends Binder {

        public GestureRecognitionService getService() {
            return GestureRecognitionService.this;
        }
    }

    private final IBinder binder = new GestureRecognitionServiceBinder();

    GestureRecorder recorder;
    GestureClassifier classifier;
    String activeTrainingSet;
    String activeLearnLabel;

    Set<GestureRecognitionListener> listeners = new HashSet<>();

    public void deleteTrainingSet(String trainingSetName) {
        if (classifier.deleteTrainingSet(trainingSetName)) {
            for (GestureRecognitionListener listener : listeners) {
                listener.onTrainingSetDeleted(trainingSetName);
            }
        }
    }

    public void registerListener(GestureRecognitionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void startClassificationMode(String trainingSetName) {
        activeTrainingSet = trainingSetName;
        recorder.start();
        classifier.loadTrainingSet(trainingSetName);
    }

    public void startLearnMode(String trainingSetName, String gestureName) {
        activeTrainingSet = trainingSetName;
        activeLearnLabel = gestureName;
        recorder.setState(GestureRecorder.State.TRAINING);
    }

    public void stopLearnMode() {
        recorder.setState(GestureRecorder.State.TRAINED);
    }

    public void startRecognizing() {
        recorder.setState(GestureRecorder.State.RECOGNIZING);
    }

    public void stopRecognizing() {
        recorder.setState(GestureRecorder.State.RECOGNIZED);
    }

    public void unregisterListener(GestureRecognitionListener listener) {
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            stopClassificationMode();
        }
    }

    public void stopClassificationMode() {
        recorder.stop();
    }

    public IBinder onBind(Intent intent) {
        recorder.registerListener(this);
        return binder;
    }

    @Override
    public void onCreate() {
        recorder = new GestureRecorder(this);
        classifier = new GestureClassifier(new NormedGridExtractor(), this);
        super.onCreate();
    }

    @Override
    public void gestureTrained(List<float[]> values) {
        classifier.trainData(activeTrainingSet, new Gesture(values, activeLearnLabel));
        classifier.commitData();
        for (GestureRecognitionListener listener : listeners) {
            listener.onGestureLearned(activeLearnLabel);
        }
    }

    @Override
    public void gestureRecognized(List<float[]> values) {
        recorder.pause(true);
        Distribution distribution = classifier.classifySignal(activeTrainingSet, new Gesture(values, null));
        recorder.pause(false);
        if (distribution != null && distribution.size() > 0) {
            for (GestureRecognitionListener listener : listeners) {
                listener.onGestureRecognized(distribution);
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        recorder.unregisterListener(this);
        return super.onUnbind(intent);
    }

}
