package uqac.dim.pixelpursuit;

import android.util.Log;

public class LeaderboardPlayer {

    private String playerName;
    private long playerScore;
    private String scoreDate;

    public LeaderboardPlayer(String playerName, long playerScore, String scoreDate) {
        this.playerName = playerName;
        this.playerScore = playerScore;
        this.scoreDate = scoreDate;
        String TAG = "LeaderboardPlayer";
        Log.d(TAG, "Création d'un joueur " + this.playerName + " + " + this.playerScore);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public long getPlayerScore() {
        return playerScore;
    }

    public String getPlayerScoreString(){
        return String.valueOf(playerScore);
    }

    public void setPlayerScore(long playerScore) {
        this.playerScore = playerScore;
    }

    public String getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(String scoreDate) {
        this.scoreDate = scoreDate;
    }
}
