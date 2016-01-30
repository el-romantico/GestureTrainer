package com.elromantico.client.gestures.classifier.featureExtraction;

import com.elromantico.client.gestures.Gesture;

public interface IFeatureExtractor {
	Gesture sampleSignal(Gesture signal);
}