package com.stomas.pruebanacional;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTClass extends AppCompatActivity {
    private static String mqttHost = "tcp://bevelthief935.cloud.shiftr.io:1883";
    private static String IdUsuario = "AppAndroid";
    private static String Topico = "Mensaje";
    private static String User = " bevelthief935";
    private static String Pass = "2G6mCgwwkkJmknxe";

    private TextView textView;
    private EditText editTextMensaje;
    private Button botonEnvio;
    private MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajeria);

        textView = findViewById(R.id.textView1);
        editTextMensaje = findViewById(R.id.txtMensajeria);
        botonEnvio = findViewById(R.id.botonEnvioMensajeria);
        try {
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());
            mqttClient.connect(options);
            Toast.makeText(this, "Estas conectado a Servidor MQTT", Toast.LENGTH_SHORT).show();
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "Se ha perdido la conexión a MQTT");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload());
                    runOnUiThread(() -> textView.setText(payload));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "¡Éxito en el envío!");
                }
            });

        }catch (MqttException e){
            e.printStackTrace();
        }

        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = editTextMensaje.getText().toString();
                try {
                    if (mqttClient != null && mqttClient.isConnected()){
                        mqttClient.publish(Topico, mensaje.getBytes(), 0, false);
                        textView.append("\n - "+ mensaje);
                        Toast.makeText(MQTTClass.this, "Mensaje enviado exitosamente", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MQTTClass.this, "Error en el envío del mensaje. Revisar conexión a MQTT", Toast.LENGTH_SHORT).show();
                    }
                }catch (MqttException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void cargarActivity(View view) {
        //cambio de página
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
