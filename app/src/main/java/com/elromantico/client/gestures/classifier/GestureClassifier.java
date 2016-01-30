package com.elromantico.client.gestures.classifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.elromantico.client.gestures.Gesture;
import com.elromantico.client.gestures.classifier.featureExtraction.IFeatureExtractor;

public class GestureClassifier {

	protected List<Gesture> trainingSet = Collections.emptyList();
	protected IFeatureExtractor featureExtractor;
	protected String activeTrainingSet = "";
	private String storageDir = "storage/sdcard0";
	private final Context context;

	public GestureClassifier(IFeatureExtractor fE, Context context) {
		trainingSet = new ArrayList<Gesture>();
		featureExtractor = fE;
		this.context = context;
	}

	public boolean commitData() {
		if (activeTrainingSet != null && activeTrainingSet != "") {
			try {
				Log.d("KOR", new File(storageDir, activeTrainingSet + ".gst").getAbsolutePath());
				FileOutputStream fos = new FileOutputStream(new File(storageDir, activeTrainingSet + ".gst").toString());
				ObjectOutputStream o = new ObjectOutputStream(fos);
				o.writeObject(trainingSet);
				o.close();
				fos.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean trainData(String trainingSetName, Gesture signal) {
		loadTrainingSet(trainingSetName);
		trainingSet.add(featureExtractor.sampleSignal(signal));
		return true;
	}

	@SuppressWarnings("unchecked")
	public void loadTrainingSet(String trainingSetName) {
		if (!trainingSetName.equals(activeTrainingSet)) {
			activeTrainingSet = trainingSetName;
			FileInputStream input;
			ObjectInputStream o;
			try {
				input = new FileInputStream(new File(storageDir, activeTrainingSet + ".gst"));
				o = new ObjectInputStream(input);
				trainingSet = (ArrayList<Gesture>) o.readObject();
				try {
					o.close();
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				trainingSet = new ArrayList<Gesture>();
			}
		}
	}

	public boolean deleteTrainingSet(String trainingSetName) {
		if (activeTrainingSet != null && activeTrainingSet.equals(trainingSetName)) {
			trainingSet = new ArrayList<Gesture>();
		}

		return new File(storageDir, activeTrainingSet + ".gst").delete();
	}

	public Distribution classifySignal(String trainingSetName, Gesture signal) {
		if (trainingSetName == null) {
			System.err.println("No Training Set Name specified");
			trainingSetName = "default";
		}
		if (!trainingSetName.equals(activeTrainingSet)) {
			loadTrainingSet(trainingSetName);
		}

		Distribution distribution = new Distribution();
		Gesture sampledSignal = featureExtractor.sampleSignal(signal);

		for (Gesture s : trainingSet) {
			double dist = DTWAlgorithm.calcDistance(s, sampledSignal);
			distribution.addEntry(s.getLabel(), dist);
		}
		if (trainingSet.isEmpty()) {
			System.err.printf("No training data for trainingSet %s available.\n", trainingSetName);
		}

		return distribution;
	}

}