package uqac.dim.pixelpursuit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uqac.dim.pixelpursuit.difficultydabase.Difficulty;

public class LevelSelectorAdapter extends RecyclerView.Adapter<LevelSelectorAdapter.ViewHolder> {

    Context context;
    private final List<Difficulty> difficulties;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Button boutonDifficulte;
        private final TextView vitesse, taille, temps;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            boutonDifficulte = itemView.findViewById(R.id.bouton_lvlselectoritem);
            vitesse = itemView.findViewById(R.id.tv_vitesse_lvlselectoritem);
            taille = itemView.findViewById(R.id.tv_taille_lvlselectoritem);
            temps = itemView.findViewById(R.id.tv_temps_lvlselectoritem);
        }
    }

    public LevelSelectorAdapter(Context context, List<Difficulty> difficulties) {
        this.context = context;
        this.difficulties = difficulties;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.level_selector_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.boutonDifficulte.setText(difficulties.get(position).nom);
        holder.vitesse.setText(String.format(context.getResources().getString(R.string.vitesse_jeu), difficulties.get(position).vitesse));
        holder.taille.setText(String.format(context.getResources().getString(R.string.taille_image), difficulties.get(position).taille));
        holder.temps.setText(String.format(context.getResources().getString(R.string.temps_jeu), difficulties.get(position).temps));
    }

    @Override
    public int getItemCount() {
        return difficulties.size();
    }
}

