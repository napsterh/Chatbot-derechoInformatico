package com.example.os.asistentevirtual;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.karan.churi.PermissionManager.PermissionManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import ai.api.android.AIConfiguration;
import ai.api.android.GsonFactory;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;
import ai.api.ui.AIButton;


import android.speech.tts.TextToSpeech;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    AIButton aiButton;
    public static final String TAG = MainActivity.class.getName();
    private TextView resultEscucha;
    private TextView resultRespuesta;
    private Gson gson = GsonFactory.getGson();
    private PermissionManager permissionManager;

    TextToSpeech leer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        permissionManager=new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);

        leer = new TextToSpeech(this, this);
        resultEscucha = (TextView) findViewById(R.id.tv_escuchado);
        resultRespuesta = (TextView) findViewById(R.id.tv_respuesta);
        final AIConfiguration config = new AIConfiguration("bec8a0d1120f44feafa85096b748cd7c",
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        aiButton = (AIButton) findViewById(R.id.micButton);

        aiButton.initialize(config);
        aiButton.setResultsListener(new AIButton.AIButtonListener() {
            @Override
            public void onResult(final AIResponse response) {

                    runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        Log.d(TAG, "onResult");
                        JSONObject base = null;
                        String speechText = null;
//                        resultTextView.setText(gson.toJson(response));

                        Log.i(TAG, "Received success response");

                        // this is example how to get different parts of result object
                        final Status status = response.getStatus();
                        Log.i(TAG, "Status code: " + status.getCode());
                        Log.i(TAG, "Status type: " + status.getErrorType());

                        final Result result = response.getResult();
                        Log.i(TAG, "Resolved query: " + result.getResolvedQuery());

                        resultEscucha.setText(result.getResolvedQuery());
                        Log.i(TAG, "Action: " + result.getAction());
                        final String speech = result.getFulfillment().getSpeech();
                        Log.i(TAG, "Speech: " + speech);
                        String[] splitSpeech = speech.split("_");
                        if(String.valueOf(splitSpeech[0]).equals("latlng")){
                            //latlng_-13.12,-78.15_mensaje
                            leer.speak(splitSpeech[2], TextToSpeech.QUEUE_FLUSH, null, null);
                            resultRespuesta.setText(splitSpeech[2]);
                            String[] latLang = String.valueOf(splitSpeech[1]).split(",");

                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);

                            intent.putExtra("lat", Double.parseDouble(latLang[0]));
                            intent.putExtra("lng", Double.parseDouble(latLang[1]));
                            //Log.i(TAG, "lat: " + Double.parseDouble(latLang[0]) +" lng "+Double.parseDouble(latLang[1]));
                            startActivity(intent);
                        } else if(String.valueOf(splitSpeech[0]).equals("img")){
                            //img_foto_mensaje
                            leer.speak(splitSpeech[2], TextToSpeech.QUEUE_FLUSH, null, null);
                            resultRespuesta.setText(splitSpeech[2]);
                            String urlimg = splitSpeech[1];

                            Intent intent = new Intent(MainActivity.this, ImgActivity.class);
                            intent.putExtra("img", urlimg);
                            startActivity(intent);
                        } else {
                            leer.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                            resultRespuesta.setText(speech);
                        }

                        final Metadata metadata = result.getMetadata();
                        if (metadata != null) {
                            Log.i(TAG, "Intent id: " + metadata.getIntentId());
                            Log.i(TAG, "Intent name: " + metadata.getIntentName());
                        }

                        final HashMap<String, JsonElement> params = result.getParameters();
                        if (params != null && !params.isEmpty()) {
                            Log.i(TAG, "Parameters: ");
                            for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                                Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(AIError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("ApiAi", "onError");
                    }
                });
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    private void reemplazarTexto(){

    }

    @Override
    public void onInit(int i) {

    }
}
