package el.romantico.ccaal.gestures;

import el.romantico.ccaal.gestures.classifier.Distribution;

public interface GestureRecognitionListener {
    void onGestureRecognized(Distribution distribution);

    void onGestureLearned(String gestureName);

    void onTrainingSetDeleted(String trainingSet);
}

