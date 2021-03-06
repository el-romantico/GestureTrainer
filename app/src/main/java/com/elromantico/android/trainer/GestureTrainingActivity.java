/*
 * GestureTrainer.java
 *
 * Created: 18.08.2011
 *
 * Copyright (C) 2011 Robert Nesselrath
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.elromantico.android.trainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elromantico.android.gesture.R;
import com.elromantico.client.gestures.GestureRecognitionListener;
import com.elromantico.client.gestures.GestureRecognitionService;
import com.elromantico.client.gestures.classifier.Distribution;

public class GestureTrainingActivity extends Activity {

    GestureRecognitionService recognitionService;
    String activeTrainingSet;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            recognitionService = ((GestureRecognitionService.GestureRecognitionServiceBinder) binder).getService();
            recognitionService.startClassificationMode(activeTrainingSet);
            recognitionService.registerListener(gestureListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            recognitionService = null;
        }
    };

    GestureRecognitionListener gestureListener = new GestureRecognitionListener() {

        @Override
        public void onGestureLearned(String gestureName) {
            Toast.makeText(GestureTrainingActivity.this, String.format("Gesture %s learned", gestureName), Toast.LENGTH_SHORT).show();
            System.err.println("Gesture %s learned");
        }

        @Override
        public void onTrainingSetDeleted(String trainingSet) {
            Toast.makeText(GestureTrainingActivity.this, String.format("Training set %s deleted", trainingSet), Toast.LENGTH_SHORT).show();
            System.err.println(String.format("Training set %s deleted", trainingSet));
        }

        @Override
        public void onGestureRecognized(final Distribution distribution) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(GestureTrainingActivity.this, String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()), Toast.LENGTH_LONG).show();
                    System.err.println(String.format("%s: %f", distribution.getBestMatch(), distribution.getBestDistance()));
                }
            });
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final TextView activeTrainingSetText = (TextView) findViewById(R.id.activeTrainingSet);
        final EditText trainingSetText = (EditText) findViewById(R.id.trainingSetName);
        final EditText editText = (EditText) findViewById(R.id.gestureName);
        activeTrainingSet = trainingSetText.getText().toString();
        final Button startTrainButton = (Button) findViewById(R.id.trainButton);
        final Button recognizeButton = (Button) findViewById(R.id.recognizeButton);
        final Button deleteTrainingSetButton = (Button) findViewById(R.id.deleteTrainingSetButton);
        final Button changeTrainingSetButton = (Button) findViewById(R.id.startNewSetButton);

        recognizeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recognitionService != null) {
                    if (recognizeButton.getText().equals("Start recognizing")) {
                        recognizeButton.setText("Stop recognizing");
                        recognitionService.startRecognizing();
                    } else {
                        recognizeButton.setText("Start recognizing");
                        recognitionService.stopRecognizing();
                    }
                }
            }
        });

        startTrainButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (recognitionService != null) {
                    if (editText.isEnabled()) {
                        startTrainButton.setText("Stop Training");
                        editText.setEnabled(false);
                        deleteTrainingSetButton.setEnabled(false);
                        changeTrainingSetButton.setEnabled(false);
                        trainingSetText.setEnabled(false);
                        recognitionService.startLearnMode(activeTrainingSet, editText.getText().toString());
                    } else {
                        startTrainButton.setText("Start Training");
                        editText.setEnabled(true);
                        deleteTrainingSetButton.setEnabled(true);
                        changeTrainingSetButton.setEnabled(true);
                        trainingSetText.setEnabled(true);
                        recognitionService.stopLearnMode();
                    }
                }
            }
        });
        changeTrainingSetButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                activeTrainingSet = trainingSetText.getText().toString();
                activeTrainingSetText.setText(activeTrainingSet);

                if (recognitionService != null) {
                    recognitionService.startClassificationMode(activeTrainingSet);
                }
            }
        });

        deleteTrainingSetButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(GestureTrainingActivity.this);
                builder.setMessage("You really want to delete the training set?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (recognitionService != null) {
                            recognitionService.deleteTrainingSet(activeTrainingSet);
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onPause() {
        recognitionService.unregisterListener(gestureListener);
        recognitionService = null;
        unbindService(serviceConnection);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Intent bindIntent = new Intent("de.dfki.ccaal.gestures.GESTURE_RECOGNIZER");
        bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }
}