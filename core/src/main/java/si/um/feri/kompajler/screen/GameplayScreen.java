package si.um.feri.kompajler.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

import si.um.feri.kompajler.DigitalniDvojcek;

public class GameplayScreen implements Screen {
    private final DigitalniDvojcek game;

    public GameplayScreen(DigitalniDvojcek game) {
        this.game = game;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1f, 0f, 0f, 1f);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
