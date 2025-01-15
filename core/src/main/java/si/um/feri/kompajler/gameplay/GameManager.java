package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

import si.um.feri.kompajler.assets.AssetDescriptors;

public class GameManager {
    private static GameManager instance;
    public Array<Bullet> bullets;
    public Array<Player> players;
    public Array<PlayerScore> playerScores;

    private GameManager() {
        bullets = new Array<>();
        players = new Array<>();
        playerScores = new Array<>();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }
    public Array<Player> getPlayers() {
        return players;
    }
    public Array<PlayerScore> getPlayerScores() {
        return playerScores;
    }

    private void updatePlayerScore(int id) {
        int increaseScorePlayerId = -1;
        if (id == 0) increaseScorePlayerId = 1;
        else if (id == 1) increaseScorePlayerId = 0;

        for (PlayerScore playerScore : playerScores) {
            if (increaseScorePlayerId != -1 && playerScore.getPlayerId() == increaseScorePlayerId) {
                playerScore.increaseScore();
            }
            System.out.println("Player: " + playerScore.getPlayerId() + "|Score = " + playerScore.getScore());
        }

    }

    public void updateBullets(float deltaTime, AssetManager assetManager) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(deltaTime);


            for (Player player : players) {
                if (bullet.getBounds().overlaps(player.getBounds()) && bullet.playerId != player.getId()) {
                    player.damage();
                    System.out.println("Player damaged: " + player.getId());
                    updatePlayerScore(player.getId());
                    assetManager.get(AssetDescriptors.EXPLOSION_WAV).play(0.5f);
                    bullets.removeIndex(i);
                    break;
                }
                if (bullet.getBounds().overlaps(player.getBounds()) && bullet.damageFriendly) {
                    player.damage();
                    bullets.removeIndex(i);
                    System.out.println("Player damaged: " + player.getId());
                    //System.out.println(player.getId() + "|" + player.getHitpoints());
                    break;
                }
            }

            if (bullet.count == 0) {
                bullets.removeIndex(i);
            }
        }
    }
}
