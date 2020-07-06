package com.lab4moviles.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.lab4moviles.DetailsActivity;
import com.lab4moviles.R;
import com.lab4moviles.entities.Publicacion;

import java.util.List;

import static com.lab4moviles.util.Util.formatDate;

public class PublicacionesAdapter extends RecyclerView.Adapter<PublicacionesAdapter.PublicacionesViewHolder> {
    private List<Publicacion> listaPublicaciones;
    private Context context;
    private StorageReference storageReference;

    public PublicacionesAdapter(List<Publicacion> listaPublicaciones, Context c, StorageReference storageReference){
        this.listaPublicaciones = listaPublicaciones;
        this.context = c;
        this.storageReference = storageReference;
    }

    public static class PublicacionesViewHolder extends RecyclerView.ViewHolder{
        private TextView txtMainUsername;
        private TextView txtMainFecha;
        private TextView txtMainComments;
        private TextView txtMainDescription;
        private ImageView imgPublicacion;
        private Button buttonVerMas;

        public PublicacionesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMainUsername = itemView.findViewById(R.id.txtMainUsername);
            txtMainFecha = itemView.findViewById(R.id.txtMainFecha);
            txtMainComments = itemView.findViewById(R.id.txtMainComments);
            txtMainDescription = itemView.findViewById(R.id.txtMainDescription);
            imgPublicacion = itemView.findViewById(R.id.imgPublicacion);
            buttonVerMas = itemView.findViewById(R.id.buttonVerMas);
        }
    }

    @NonNull
    @Override
    public PublicacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_rv_publicacion, parent, false);
        return new PublicacionesAdapter.PublicacionesViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PublicacionesViewHolder holder, int position) {
        final Publicacion publicacion = listaPublicaciones.get(position);
        holder.txtMainUsername.setText(publicacion.getUsuario().get("nombre"));
        holder.txtMainFecha.setText(formatDate(publicacion.getFecha()));
        holder.txtMainDescription.setText(publicacion.getDescripcion());

        if(!publicacion.getComentarios().isEmpty()){
            holder.txtMainComments.setText("- " + publicacion.getComentarios().size() + " comentarios");
        }
        else{
            holder.txtMainComments.setVisibility(View.INVISIBLE);
        }

        //Imagen de la publicación
        setImage(publicacion.getId() + ".JPG", holder);

        //Si se presiona el botón "Ver más"
        holder.buttonVerMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("publicacion", publicacion);
                context.startActivity(intent);
            }
        });

    }

    //Obtiene la imagen del Storage de Firebase
    public void setImage(final String photoName, final PublicacionesViewHolder holder){
        storageReference.child(photoName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(holder.imgPublicacion);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }
}

