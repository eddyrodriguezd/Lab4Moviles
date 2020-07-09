package com.lab4moviles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PhotoActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private int counter2;
    private TextInputEditText txtDescripcionFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        txtDescripcionFoto = findViewById(R.id.txtDescripcionFoto);

        findViewById(R.id.idSeleccionarFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtDescripcionFoto.getText().toString().isEmpty() || txtDescripcionFoto.getText().toString().matches("^\\s+$")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), GALLERY_REQUEST_CODE);
                }else{
                    Toast.makeText(PhotoActivity.this, "Por favor escriba una descripcion para la foto.", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data!= null){
            Uri imageData = data.getData();
            uploadInfoPublicacion(imageData);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Guarda la publicación en Firestore
    protected void uploadInfoPublicacion(final Uri imageData){
        Map<String, Object> infoUsuario = new HashMap<>();
        infoUsuario.put("id", user.getUid());
        infoUsuario.put("nombre", Objects.requireNonNull(user.getDisplayName()).split(" ")[0]);

        Map<String, Object> infoPublicacion = new HashMap<>();
        infoPublicacion.put("descripcion", txtDescripcionFoto.getText().toString());
        infoPublicacion.put("fecha", new Timestamp(Calendar.getInstance().getTime()));
        infoPublicacion.put("usuario", infoUsuario);

        fStore.collection("publicaciones")
                .add(infoPublicacion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //La imagen se sube utilizando el id de la publicación recien creada
                        uploadImage(documentReference.getId(), imageData);
                    }
                });
    }

    //Sube imagen al Storage
    private void uploadImage(String id, Uri imageData) {
        final StorageReference storageRef = fStorage.getReference().child(id + ".JPG");
        storageRef.putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri imageUrl) {
                        //Volviendo a la pantalla principal (publicaciones de todos)
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
    }
}