package si.um.feri.kompajler.gameplay;

public class PlayerScore {
    private int playerId;
    private int score;

    public PlayerScore(int playerId, int score) {
        this.playerId = playerId;
        this.score = score;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getScore() {
        return score;
    }

    public void increaseScore() {
        ++this.score;
    }
}
