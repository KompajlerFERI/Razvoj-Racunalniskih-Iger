package main.java.si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import main.java.si.um.feri.kompajler.assets.RegionNames;

public class Player {
    private int hitpoints;
    private TextureRegion tankBottom;
    private TextureRegion tankTop;

    public Player(TextureAtlas atlas) {
        this.hitpoints = 1;
        this.tankBottom = atlas.findRegion(RegionNames.TANK_BOTTOM_GREEN);
        this.tankTop = atlas.findRegion(RegionNames.TANK_TOP_GREEN);
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public TextureRegion getTankBottom() {
        return tankBottom;
    }

    public TextureRegion getTankTop() {
        return tankTop;
    }
}
