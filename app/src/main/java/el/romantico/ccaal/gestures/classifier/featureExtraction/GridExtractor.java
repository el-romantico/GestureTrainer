package el.romantico.ccaal.gestures.classifier.featureExtraction;

import java.util.ArrayList;

import el.romantico.ccaal.gestures.Gesture;

public class GridExtractor implements IFeatureExtractorConstCount {
	final static int SAMPLE_STEPS = 32;

	public Gesture sampleSignal(Gesture signal) {

		ArrayList<float[]> sampledValues = new ArrayList<float[]>();
		Gesture sampledSignal = new Gesture(sampledValues, signal.getLabel());
		float findex;

		for (int j = 0; j < SAMPLE_STEPS; ++j) {
			sampledValues.add(new float[3]);
			for (int i = 0; i < 3; ++i) {
				findex = (float) (signal.length() - 1) * j / (SAMPLE_STEPS - 1);
				float res = findex - (int) findex;
				sampledSignal.setValue(j, i, (1 - res) * signal.getValue((int) findex, i) + ((int) findex + 1 < signal.length() - 1 ? res * signal.getValue((int) findex + 1, i) : 0));
			}
		}

		return sampledSignal;
	}
}
