package com.stomas.pruebanacional;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText txtIdEntrenador, txtNombre, txtNumero;
    private ListView lista;
    private Spinner spFormato;
    private FirebaseFirestore db;

    String [] Formatos = {"Junior", "Senior", "Máster"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CargarListaFirestore();
        db = FirebaseFirestore.getInstance();
        txtIdEntrenador = findViewById(R.id.txtIDTrainer);
        txtNombre = findViewById(R.id.txtNombre);
        txtNumero = findViewById(R.id.txtNumero);
        spFormato = findViewById(R.id.spFormato);
        lista = findViewById(R.id.lista);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Formatos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormato.setAdapter(adapter);
    }

    public void enviarDatosFirestore(View view){
        String idtrainer = txtIdEntrenador.getText().toString();
        String nombre = txtNombre.getText().toString();
        String numero = txtNumero.getText().toString();
        String tipoFormato = spFormato.getSelectedItem().toString();

        Map<String, Object> participante = new HashMap<>();
        participante.put("idtrainer", idtrainer);
        participante.put("nombre", nombre);
        participante.put("numero", numero);
        participante.put("tipoFormato", tipoFormato);

        db.collection("participantes")
            .document(idtrainer)
                .set(participante)
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(MainActivity.this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(MainActivity.this, "Error al enviar datos.", Toast.LENGTH_SHORT).show();
                });

    }

    public void CargarLista(View view){
        CargarListaFirestore();
    }

    private void CargarListaFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("particpantes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<String> listaParticipantes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String linea = "|| " + document.getString("idtrainer") + " || " +
                                        document.getString("nombre") + " || " +
                                        document.getString("numero");
                                listaParticipantes.add(linea);
                            }
                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                              MainActivity.this,
                              android.R.layout.simple_list_item_1,
                              listaParticipantes
                            );
                            lista.setAdapter(adaptador);
                        } else {
                            Log.e("TAG", "Error al obtener datos.", task.getException());
                        }
                    }
                });
    }


    public void cargarActivity(View view) {
        //inicio de actividad en clase Usuario
        Intent intent = new Intent(this, MQTTClass.class);
        startActivity(intent);
    }
}