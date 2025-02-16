package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import si.um.feri.kompajler.config.GameConfig;

public class MapBoundsHandlerPlayer {
    private final TiledMapTileLayer borders;

    public MapBoundsHandlerPlayer(TiledMapTileLayer borders) {
        this.borders = borders;
    }

    public void constrainPlayer(Player player) {
        if (isTileBlocked(player.rectangle.x, player.rectangle.y) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y) ||
            isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width / 2, player.rectangle.y) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width / 2, player.rectangle.y + player.rectangle.height) ||
            isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height / 2) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height / 2)) {

            // adjust pozicija glede na to v kero smer se premika
            if (isTileBlocked(player.rectangle.x, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height) ||
                isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height / 2)) {
                player.rectangle.x += Math.signum(GameConfig.PLAYER_SPEED) * 1; // ce se overlapa premakni vun
            } else if (isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height / 2)) {
                player.rectangle.x -= Math.signum(GameConfig.PLAYER_SPEED) * 1; // ce se overlapa premakni vun
            }

            if (isTileBlocked(player.rectangle.x, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width / 2, player.rectangle.y)) {
                player.rectangle.y += Math.signum(GameConfig.PLAYER_SPEED) * 1; // ce se overlapa premakni vun
            } else if (isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width / 2, player.rectangle.y + player.rectangle.height)) {
                player.rectangle.y -= Math.signum(GameConfig.PLAYER_SPEED) * 1; // ce se overlapa premakni vun
            }

            player.stopMovement = true;
        }
    }

    private boolean isTileBlocked(float x, float y) {
        int tileX = (int) (x / borders.getTileWidth());
        int tileY = (int) (y / borders.getTileHeight());
        TiledMapTileLayer.Cell cell = borders.getCell(tileX, tileY);
        return cell != null && cell.getTile() != null;
    }
}
