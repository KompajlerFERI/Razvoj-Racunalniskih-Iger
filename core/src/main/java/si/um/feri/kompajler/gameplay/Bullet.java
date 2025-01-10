package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import si.um.feri.kompajler.config.GameConfig;

public class Bullet {
    private Rectangle bounds;
    private Vector2 velocity;
    private float speed;
    private TextureRegion textureRegion;
    public int count;

    public Bullet(float x, float y, float velocityX, float velocityY, TextureAtlas atlas) {
        this.textureRegion = atlas.findRegion("bullet"); // Assuming "bullet" is the name of the region in the atlas
        float width = textureRegion.getRegionWidth() * 1.5f;
        float height = textureRegion.getRegionHeight() * 1.5f;
        this.speed = GameConfig.BULLET_SPEED;
        this.bounds = new Rectangle(x, y, width, height);
        this.velocity = new Vector2(velocityX, velocityY).nor().scl(speed);
        this.count = 5;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void update(float deltaTime) {
        bounds.x += velocity.x * deltaTime;
        bounds.y += velocity.y * deltaTime;
    }

    public void bounceX() {
        velocity.x = -velocity.x;
    }

    public void bounceY() {
        velocity.y = -velocity.y;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }
}
