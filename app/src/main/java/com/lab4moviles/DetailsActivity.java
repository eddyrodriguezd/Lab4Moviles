package com.lab4moviles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.lab4moviles.util.Util.formatDate;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.lab4moviles.adapters.ComentariosAdapter;
import com.lab4moviles.entities.Comentario;
import com.lab4moviles.entities.Publicacion;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private FirebaseStorage fStorage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fStorage = FirebaseStorage.getInstance();

        Intent intent = getIntent();
        Publicacion publicacion = (Publicacion) intent.getSerializableExtra("publicacion");

        if(publicacion != null){
            fillInfo(publicacion);
        }

    }

    //Completa los campos con la información de la publicación
    @SuppressLint("SetTextI18n")
    private void fillInfo(Publicacion publicacion){
        TextView txtMainUsername_Detalles = findViewById(R.id.txtMainUsername_Detalles);
        TextView txtMainFecha_Detalles = findViewById(R.id.txtMainFecha_Detalles);
        TextView txtMainComments_Detalles = findViewById(R.id.txtMainComments_Detalles);
        TextView txtMainDescription_Detalles = findViewById(R.id.txtMainDescription_Detalles);
        TextView txtNoComments = findViewById(R.id.txtNoComments);
        ImageView imgPublicacion_Detalles = findViewById(R.id.imgPublicacion_Detalles);

        txtMainUsername_Detalles.setText(publicacion.getUsuario().get("nombre"));
        txtMainFecha_Detalles.setText(formatDate(publicacion.getFecha()));
        txtMainDescription_Detalles.setText(publicacion.getDescripcion());

        if(!publicacion.getComentarios().isEmpty()){
            txtNoComments.setVisibility(View.GONE);
            txtMainComments_Detalles.setText("- " + publicacion.getComentarios().size() + " comentarios");
            refreshCommentsRecyclerView(publicacion.getComentarios());
        }
        else{
            txtMainComments_Detalles.setVisibility(View.INVISIBLE);
        }

        setImage(publicacion.getId() + ".JPG", imgPublicacion_Detalles);
    }

    //Obtiene la imagen del Storage de Firebase
    private void setImage(String photoName, final ImageView imgView){
        fStorage.getReference().child(photoName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imgView);
            }
        });
    }

    //Actualiza el Recycler View de comentarios
    private void refreshCommentsRecyclerView(List<Comentario> listaComentarios){
        RecyclerView recyclerView = findViewById(R.id.recyclerViewComments);
        ComentariosAdapter comentariosAdapter = new ComentariosAdapter(listaComentarios,DetailsActivity.this);
        recyclerView.setAdapter(comentariosAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this));
    }

}