package uqac.dim.pixelpursuit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    Context context;
    private final List<LeaderboardPlayer> players;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView medalImage;
        private final TextView playerName, playerScore, scoreDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medalImage = itemView.findViewById(R.id.player_medal);
            playerName = itemView.findViewById(R.id.player_name);
            playerScore = itemView.findViewById(R.id.player_score);
            scoreDate = itemView.findViewById(R.id.player_date);
        }
    }

    public LeaderboardAdapter(Context context, List<LeaderboardPlayer> players) {
        this.context = context;
        this.players = players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0){
            holder.medalImage.setImageResource(R.drawable.medaille_or);
        }
        else if (position == 1){
            holder.medalImage.setImageResource(R.drawable.medaille_argent);
        }
        else if (position == 2) {
            holder.medalImage.setImageResource(R.drawable.medaille_bronze);
        }
        else{
            holder.medalImage.setImageResource(R.drawable.medaille_vide);
        }
        holder.playerName.setText(String.format(context.getResources().getString(R.string.leaderboard_nom), players.get(position).getPlayerName()));
        holder.playerScore.setText(String.format(context.getResources().getString(R.string.leaderboard_score_string), players.get(position).getPlayerScoreString()));
        holder.scoreDate.setText(String.format(context.getResources().getString(R.string.leaderboard_date), players.get(position).getScoreDate()));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }
}
