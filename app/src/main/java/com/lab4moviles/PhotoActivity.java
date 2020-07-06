package com.lab4moviles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class PhotoActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.idSeleccionarFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), GALLERY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
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
            ImageView imgSelected = findViewById(R.id.imgSelected);
            imgSelected.setImageURI(imageData);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}