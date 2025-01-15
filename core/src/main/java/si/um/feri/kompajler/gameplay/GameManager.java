package si.um.feri.kompajler.gameplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

import si.um.feri.kompajler.assets.AssetDescriptors;

public class GameManager {
    private static GameManager instance;
    public Array<Bullet> bullets;
    public Array<Player> players;
    public Array<PlayerScore> playerScores;
    private static final List<int[]> validPositions = new ArrayList<>();
    private static final Random random = new Random();

    static {
        validPositions.add(new int[]{50, 50});
        validPositions.add(new int[]{50, 1430});
        validPositions.add(new int[]{1440, 50});
        validPositions.add(new int[]{1440, 1430});
    }

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
        boolean reset = false;
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
                    reset = true;
                    break;
                }
                if (bullet.getBounds().overlaps(player.getBounds()) && bullet.damageFriendly) {
                    player.damage();
                    bullets.removeIndex(i);
                    System.out.println("Player damaged: " + player.getId());
                    updatePlayerScore(player.getId());
                    assetManager.get(AssetDescriptors.EXPLOSION_WAV).play(0.5f);
                    reset = true;
                    break;
                }
            }
            if (reset) {
                int[][] positions = selectTwoRandomPositions();
                for (int j = 0; j < players.size; j++) {
                    players.get(j).resetPosition(positions[j][0], positions[j][1]);
                }
            }

            if (bullet.count == 0) {
                bullets.removeIndex(i);
            }
        }
    }

    public int[][] selectTwoRandomPositions() {
        Collections.shuffle(validPositions, random);
        return new int[][]{validPositions.get(0), validPositions.get(1)};
    }
}
