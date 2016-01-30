package el.romantico.ccaal.gestures.classifier.featureExtraction;

import el.romantico.ccaal.gestures.Gesture;

public class NormedGridExtractor implements IFeatureExtractorConstCount {

	public Gesture sampleSignal(Gesture signal) {
		Gesture s = new GridExtractor().sampleSignal(signal);
		return new NormExtractor().sampleSignal(s);
	}

}
