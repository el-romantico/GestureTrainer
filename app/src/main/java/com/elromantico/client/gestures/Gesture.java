package com.elromantico.client.gestures;

import java.io.Serializable;
import java.util.List;

public class Gesture implements Serializable {

	private static final long serialVersionUID = 7148492971634218981L;
	private String label;
	private float[][] values;

	public Gesture(float[][] values, String label) {
		setValues(values);
		setLabel(label);
	}

	public void setValues(float[][] values) {
		this.values = values;
	}

	public String getLabel() {
		return label;
	}

	public float[][] getValues() {
		return values;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public float getValue(int index, int dim) {
		return values[index][dim];
	}

	public void setValue(int index, int dim, float f) {
		values[index][dim] = f;
	}

	public int length() {
		return values.length;
	}
}
