package com.elromantico.client.gestures;

import com.elromantico.client.gestures.classifier.Distribution;

public interface GestureRecognitionListener {
    void onGestureRecognized(Distribution distribution);

    void onGestureLearned(String gestureName);

    void onTrainingSetDeleted(String trainingSet);
}

