package com.elromantico.client.gestures.classifier.featureExtraction;

import com.elromantico.client.gestures.Gesture;

public class NormedGridExtractor implements IFeatureExtractorConstCount {

	public Gesture sampleSignal(Gesture signal) {
		Gesture s = new GridExtractor().sampleSignal(signal);
		return new NormExtractor().sampleSignal(s);
	}

}
