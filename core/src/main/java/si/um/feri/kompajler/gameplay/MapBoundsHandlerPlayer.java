package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import si.um.feri.kompajler.config.GameConfig;

public class MapBoundsHandlerPlayer {
    private final TiledMapTileLayer borders;
    private final float tileWidthInWorldUnits;
    private final float tileHeightInWorldUnits;

    public MapBoundsHandlerPlayer(TiledMapTileLayer borders, float tileWidthInWorldUnits, float tileHeightInWorldUnits) {
        this.borders = borders;
        this.tileWidthInWorldUnits = tileWidthInWorldUnits;
        this.tileHeightInWorldUnits = tileHeightInWorldUnits;
    }

    public void constrainPlayer(Player player) {
        float adjustmentDistance = 0.01f; // Reduced adjustment distance

        if (isTileBlocked(player.rectangle.x, player.rectangle.y) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y) ||
            isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width / 2, player.rectangle.y) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width / 2, player.rectangle.y + player.rectangle.height) ||
            isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height / 2) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height / 2)) {

            if (isTileBlocked(player.rectangle.x, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height) ||
                isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height / 2)) {
                player.rectangle.x += Math.signum(GameConfig.PLAYER_SPEED) * adjustmentDistance;
            } else if (isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height / 2)) {
                player.rectangle.x -= Math.signum(GameConfig.PLAYER_SPEED) * adjustmentDistance;
            }

            if (isTileBlocked(player.rectangle.x, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width / 2, player.rectangle.y)) {
                player.rectangle.y += Math.signum(GameConfig.PLAYER_SPEED) * adjustmentDistance;
            } else if (isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width / 2, player.rectangle.y + player.rectangle.height)) {
                player.rectangle.y -= Math.signum(GameConfig.PLAYER_SPEED) * adjustmentDistance;
            }

            player.stopMovement = true;
        }
    }

    private boolean isTileBlocked(float x, float y) {
        int tileX = (int) (x / tileWidthInWorldUnits);
        int tileY = (int) (y / tileHeightInWorldUnits);
        TiledMapTileLayer.Cell cell = borders.getCell(tileX, tileY);
        return cell != null && cell.getTile() != null;
    }
}
