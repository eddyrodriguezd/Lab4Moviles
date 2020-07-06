package com.lab4moviles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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

    //POR AHORA, LUEGO CAMBIAR POR EL VERDADERO
    private String userId = "idUsuario";

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
    }

    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }
        super.onStart();
    }

    //Actualiza el Recycler View
    public void refreshView(){
        getPublicacionesfromFirebase(new FirebaseCallback() {
            @Override
            public void onSuccess() {
                PublicacionesAdapter listaIncidenciasAdapter = new PublicacionesAdapter(listaPublicaciones,
                        MainActivity.this, fStorage.getReference());
                recyclerView.setAdapter(listaIncidenciasAdapter);
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
                    .whereEqualTo("usuario.id", userId)
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

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }


}