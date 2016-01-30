package el.romantico.ccaal.gestures.classifier.featureExtraction;

import el.romantico.ccaal.gestures.Gesture;

public interface IFeatureExtractor {
	Gesture sampleSignal(Gesture signal);
}