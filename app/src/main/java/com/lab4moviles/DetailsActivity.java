package com.lab4moviles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.lab4moviles.util.Util.formatDate;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.lab4moviles.adapters.ComentariosAdapter;
import com.lab4moviles.entities.Comentario;
import com.lab4moviles.entities.Publicacion;
import com.lab4moviles.util.FirebaseCallback;

public class DetailsActivity extends AppCompatActivity {

    private static final int COMMENT_ACTIVITY_REQUEST_CODE = 2;
    private FirebaseStorage fStorage;
    private FirebaseFirestore fStore;
    private Publicacion publicacion;

    //Variable para determinar si se agregó algún comentario
    private boolean commentAdded;

    //Textviews que pueden ocultarse
    private TextView txtMainComments_Detalles;
    private TextView txtNoComments;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        commentAdded = false;

        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        //Recibe la publicación
        Intent intent = getIntent();
        publicacion = (Publicacion) intent.getSerializableExtra("publicacion");

        if(publicacion != null){
            Log.d("infoApp", "filling post's info");
            fillInfo();
        }

        //Agregar Comentario
        findViewById(R.id.buttonAddComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, CommentActivity.class);
                intent.putExtra("publicacion", publicacion); //Envía la publicación para que se le agreguen comentarios
                startActivityForResult(intent, COMMENT_ACTIVITY_REQUEST_CODE);
            }
        });

    }

    //Completa los campos con la información de la publicación
    @SuppressLint("SetTextI18n")
    private void fillInfo(){
        TextView txtMainUsername_Detalles = findViewById(R.id.txtMainUsername_Detalles);
        TextView txtMainFecha_Detalles = findViewById(R.id.txtMainFecha_Detalles);
        txtMainComments_Detalles = findViewById(R.id.txtMainComments_Detalles);
        TextView txtMainDescription_Detalles = findViewById(R.id.txtMainDescription_Detalles);
        txtNoComments = findViewById(R.id.txtNoComments);
        ImageView imgPublicacion_Detalles = findViewById(R.id.imgPublicacion_Detalles);

        txtMainUsername_Detalles.setText(publicacion.getUsuario().get("nombre"));
        txtMainFecha_Detalles.setText(formatDate(publicacion.getFecha()));
        txtMainDescription_Detalles.setText(publicacion.getDescripcion());

        setImage(publicacion.getId() + ".JPG", imgPublicacion_Detalles);

        getComentariosFromPublicacion(publicacion, new FirebaseCallback() {
            @Override
            public void onSuccess() {
                refreshCommentsRecyclerView();
            }
        });
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
    @SuppressLint("SetTextI18n")
    private void refreshCommentsRecyclerView(){
        if(!publicacion.getComentarios().isEmpty()){
            txtNoComments.setVisibility(View.GONE);
            String text = "- " + publicacion.getComentarios().size() + " comentario";
            if(publicacion.getComentarios().size()>1){
                text+="s";
            }
            txtMainComments_Detalles.setText(text);

            //Build RecyclerView
            RecyclerView recyclerView = findViewById(R.id.recyclerViewComments);
            ComentariosAdapter comentariosAdapter = new ComentariosAdapter(publicacion.getComentarios(),DetailsActivity.this);
            recyclerView.setAdapter(comentariosAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(DetailsActivity.this));
        }
        else{
            txtMainComments_Detalles.setVisibility(View.INVISIBLE);
        }
    }

    //Cuando retorna de la actividad de agregar comentario
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COMMENT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                getComentariosFromPublicacion(publicacion, new FirebaseCallback() {
                    @Override
                    public void onSuccess() {
                        commentAdded = true; //Se agregó un comentario
                        refreshCommentsRecyclerView();
                    }
                });
            }
        }
    }

    public void getComentariosFromPublicacion(final Publicacion publicacion, final FirebaseCallback callback) {
        publicacion.getComentarios().clear();
        fStore.collection("publicaciones").document(publicacion.getId())
                .collection("comentarios")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Comentario comentario = document.toObject(Comentario.class);
                                publicacion.addComentario(comentario);
                            }
                            callback.onSuccess();
                        } else {
                            Toast.makeText(DetailsActivity.this, "Ocurrió un problema", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        //Volviendo a la pantalla principal (publicaciones de todos)
        if(commentAdded){ //Refrescar el inicio si se añadieron comentarios
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
        else{
            super.onBackPressed();
        }

    }
}