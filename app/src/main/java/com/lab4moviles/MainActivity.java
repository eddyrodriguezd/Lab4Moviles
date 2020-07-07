package com.lab4moviles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.lab4moviles.adapters.PublicacionesAdapter;
import com.lab4moviles.entities.Comentario;
import com.lab4moviles.entities.Publicacion;
import com.lab4moviles.util.FirebaseCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lab4moviles.util.Util.isInternetAvailable;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_ACTIVITY_REQUEST_CODE = 1;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private List<Publicacion> listaPublicaciones;
    private RecyclerView recyclerView;

    //Variable que permite determinar si ya se cargaron todas las publicaciones
    private int contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaPublicaciones = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        recyclerView = findViewById(R.id.recyclerViewPublicaciones);
        refreshView();

        //Agregar foto
        findViewById(R.id.imgButtonAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                startActivityForResult(intent, PHOTO_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            TextView txtNombreUsuarioHeader = findViewById(R.id.txtNombreUsuarioHeader);
            txtNombreUsuarioHeader.setText(Objects.requireNonNull(user.getDisplayName()).split(" ")[0]);
        }
        super.onStart();
    }

    //Actualiza el Recycler View de publicaciones
    public void refreshView(){
        getPublicacionesfromFirebase(new FirebaseCallback() {
            @Override
            public void onSuccess() {
                PublicacionesAdapter publicacionesAdapter = new PublicacionesAdapter(listaPublicaciones,
                        MainActivity.this, fStorage.getReference());
                recyclerView.setAdapter(publicacionesAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
        });
    }

    //Obtiene todas las publicaciones desde Firebase
    public void getPublicacionesfromFirebase(final FirebaseCallback callback) {
        contador = 0;

        if (isInternetAvailable(this)) {
            listaPublicaciones.clear();

            fStore.collection("publicaciones")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d("infoPublicacion", "task Successful");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    task.getResult().size();

                                    final Publicacion publicacion = document.toObject(Publicacion.class);
                                    publicacion.setId(document.getId());
                                    getComentarios(publicacion, new FirebaseCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    listaPublicaciones.add(publicacion);
                                                    contador++;
                                                    if(contador == task.getResult().size())
                                                        callback.onSuccess();
                                                }
                                            });
                                }
                                callback.onSuccess();
                            } else {
                                Toast.makeText(MainActivity.this, "Ocurrió un problema", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    //Obtiene todas los comentarios de una publicación en Firebase
    public void getComentarios(final Publicacion publicacion, final FirebaseCallback callback) {
        fStore.collection("publicaciones").document(publicacion.getId())
                .collection("comentarios")
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
                            Toast.makeText(MainActivity.this, "Ocurrió un problema", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Cuando retorna de la siguiente actividad
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Hay que actualizar la pantalla con la nueva publicación
                refreshView();
            }
        }
    }
}