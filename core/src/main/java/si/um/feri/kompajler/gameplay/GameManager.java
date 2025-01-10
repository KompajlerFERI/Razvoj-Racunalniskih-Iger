package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.utils.Array;

public class GameManager {
    private static GameManager instance;
    public Array<Bullet> bullets;

    private GameManager() {
        bullets = new Array<>();
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

    public void updateBullets(float deltaTime) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(deltaTime);
            if (bullet.count == 0) {
                bullets.removeIndex(i);
            }
        }
    }
}
