package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

import si.um.feri.kompajler.assets.AssetDescriptors;

public class GameManager {
    private static GameManager instance;
    public Array<Bullet> bullets;
    public Array<Player> players;

    private GameManager() {
        bullets = new Array<>();
        players = new Array<>();
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

    public void updateBullets(float deltaTime, AssetManager assetManager) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(deltaTime);


            for (Player player : players) {
                if (bullet.getBounds().overlaps(player.getBounds()) && bullet.playerId != player.getId()) {
                    player.damage();
                    assetManager.get(AssetDescriptors.EXPLOSION_WAV).play(0.5f);
                    bullets.removeIndex(i);
                    System.out.println(player.getId() + "|" + player.getHitpoints());
                    break;
                }
                if (bullet.getBounds().overlaps(player.getBounds()) && bullet.damageFriendly) {
                    player.damage();
                    bullets.removeIndex(i);
                    System.out.println(player.getId() + "|" + player.getHitpoints());
                    break;
                }
            }

            if (bullet.count == 0) {
                bullets.removeIndex(i);
            }
        }
    }
}
