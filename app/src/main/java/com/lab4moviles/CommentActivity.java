package com.lab4moviles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.lab4moviles.entities.Publicacion;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    private Publicacion publicacion;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        //Recibe la publicación
        publicacion = (Publicacion) getIntent().getSerializableExtra("publicacion");

        final TextInputEditText txtDescripcionComentario= findViewById(R.id.txtDescripcionComentario);

        //Al presionar "Agregar comentario"
        findViewById(R.id.idBotonPublicar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtDescripcionComentario.getText().toString().equals("")) {
                    uploadInfo(txtDescripcionComentario.getText().toString(), publicacion.getId());
                }
                else {
                    Toast.makeText(CommentActivity.this, "Por favor rellene el comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        user = mAuth.getCurrentUser();
        if (user != null) {
            TextView txtNombreUsuarioHeader = findViewById(R.id.txtNombreUsuarioHeader);
            txtNombreUsuarioHeader.setText(Objects.requireNonNull(user.getDisplayName()).split(" ")[0]);
            findViewById(R.id.imgButtonAdd).setVisibility(View.GONE);
        }
        super.onStart();
    }

    public void uploadInfo(String cuerpoComentario, String idPublicacion) {
        Map<String, Object> infoUsuario = new HashMap<>();
        infoUsuario.put("id", user.getUid());
        infoUsuario.put("nombre", Objects.requireNonNull(user.getDisplayName()).split(" ")[0]);

        Map<String, Object> infoComentario = new HashMap<>();
        infoComentario.put("texto", cuerpoComentario);
        infoComentario.put("fecha", new Timestamp(Calendar.getInstance().getTime()));
        infoComentario.put("usuario", infoUsuario);

        fStore.collection("publicaciones").document(idPublicacion).collection("comentarios")
                .add(infoComentario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CommentActivity.this, "Se ha agregado el comentario", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        //intent.putExtra("nuevo comentario", nuevoComentario);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this, "Algo salió mal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}