package com.lab4moviles.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lab4moviles.R;
import com.lab4moviles.entities.Comentario;

import java.util.List;

import static com.lab4moviles.util.Util.formatDateTime;

public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ComentariosViewHolder> {
    private List<Comentario> listaComentarios;
    private Context context;

    public ComentariosAdapter(List<Comentario> listaComentarios, Context c){
        this.listaComentarios = listaComentarios;
        this.context = c;
    }

    public static class ComentariosViewHolder extends RecyclerView.ViewHolder{
        private TextView txtCommentUserDate;
        private TextView txtCommentBody;

        public ComentariosViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCommentUserDate = itemView.findViewById(R.id.txtCommentUserDate);
            txtCommentBody = itemView.findViewById(R.id.txtCommentBody);
        }
    }

    @NonNull
    @Override
    public ComentariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_rv_comentario, parent, false);
        return new ComentariosAdapter.ComentariosViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ComentariosViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);
        holder.txtCommentUserDate.setText(comentario.getUsuario().get("nombre") + " - " + formatDateTime(comentario.getFecha()));
        holder.txtCommentBody.setText(comentario.getTexto());
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }
}

