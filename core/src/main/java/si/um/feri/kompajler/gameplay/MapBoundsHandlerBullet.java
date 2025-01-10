package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
public class MapBoundsHandlerBullet {
    private Array<Rectangle> walls;

    public MapBoundsHandlerBullet(TiledMapTileLayer wallLayer) {
        this.walls = new Array<>();
        // Extract wall rectangles from the TiledMap layer
        for (int x = 0; x < wallLayer.getWidth(); x++) {
            for (int y = 0; y < wallLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = wallLayer.getCell(x, y);
                if (cell != null) { // Assuming non-null cells are walls
                    Rectangle wall = new Rectangle(
                        x * wallLayer.getTileWidth(),
                        y * wallLayer.getTileHeight(),
                        wallLayer.getTileWidth(),
                        wallLayer.getTileHeight()
                    );
                    walls.add(wall);
                }
            }
        }
    }

    public void handleBulletCollision(Bullet bullet, float deltaTime) {
        Rectangle bulletBounds = bullet.getBounds();
        Vector2 bulletVelocity = bullet.getVelocity();

        for (Rectangle wall : walls) {
            if (bulletBounds.overlaps(wall)) {
                float overlapX = Math.min(bulletBounds.x + bulletBounds.width, wall.x + wall.width) -
                    Math.max(bulletBounds.x, wall.x);
                float overlapY = Math.min(bulletBounds.y + bulletBounds.height, wall.y + wall.height) -
                    Math.max(bulletBounds.y, wall.y);

                if (overlapX < overlapY) {
                    bullet.bounceX();
                    bullet.setPosition(bulletBounds.x + bulletVelocity.x * deltaTime, bulletBounds.y);
                } else {
                    bullet.bounceY();
                    bullet.setPosition(bulletBounds.x, bulletBounds.y + bulletVelocity.y * deltaTime);
                }
                bullet.damageFriendly = true;
                bullet.count--;
                break;
            }
        }
    }
}
