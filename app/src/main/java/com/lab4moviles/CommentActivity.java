package com.lab4moviles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.lab4moviles.entities.Comentario;
import com.lab4moviles.entities.Publicacion;
import com.lab4moviles.entities.Usuario;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    private TextView mTextView_TituloNuevoComentario;
    private EditText mEditText_Comentario;
    private Button mButton_Publicar;
    private Usuario usuario;
    private String tituloComentario;
    private Comentario nuevoComentario;
    private Date userFecha;
    private Publicacion publicacion;

    //Firebase
    //private FirebaseFirestore db = FirebaseFirestore.getInstance();
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

        publicacion = (Publicacion) getIntent().getSerializableExtra("publicacion");
        userFecha = (Date) getIntent().getSerializableExtra("userFecha");

        mTextView_TituloNuevoComentario = findViewById(R.id.idTituloNuevoComentario);
        mEditText_Comentario = findViewById(R.id.txtDescripcionComentario);
        mButton_Publicar = findViewById(R.id.idBotonPublicar);

        mTextView_TituloNuevoComentario.append(receiveData());

        mButton_Publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cuerpo_comentario = mEditText_Comentario.getText().toString();
                //Faltaria ver si es que aca abajo en comentario se pone tambien el Map<String, String> usuario
                nuevoComentario = new Comentario(userFecha,cuerpo_comentario);
                if(!nuevoComentario.getTexto().isEmpty()){

                    uploadInfo(nuevoComentario, publicacion);
                    Intent intent = new Intent();
                    intent.putExtra("nuevo comentario", nuevoComentario);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(CommentActivity.this, "Por favor rellene el comentario", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(CommentActivity.this, "Nuevo comentario realizado", Toast.LENGTH_SHORT).show();
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

    public String receiveData() {
        Intent intent = getIntent();
        String tituloComentario = "error: comentario no encontrado";
        if(intent.getStringExtra("titulo comentario") != null){
            tituloComentario = intent.getStringExtra("titulo comentario");
        }
        return tituloComentario;
    }

    public void uploadInfo(Comentario comentario, Publicacion publicacion) {
        Map<String, Object> infoUsuario = new HashMap<>();
        infoUsuario.put("id", user.getUid());
        infoUsuario.put("nombre", Objects.requireNonNull(user.getDisplayName()).split(" ")[0]);

        Map<String, Object> infoPublicacion = new HashMap<>();
        TextInputEditText txtDescripcionComentario = findViewById(R.id.txtDescripcionComentario);
        infoPublicacion.put("descripcion", txtDescripcionComentario.getText().toString());
        infoPublicacion.put("fecha", new Timestamp(Calendar.getInstance().getTime()));
        infoPublicacion.put("usuario", infoUsuario);

        fStore.collection("publicaciones").document(publicacion.getId()).collection("comentarios")
                .add(comentario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CommentActivity.this, "Se ha agregado el comentario", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this, "Algo salio mal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}