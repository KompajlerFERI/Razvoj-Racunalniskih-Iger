package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import si.um.feri.kompajler.config.GameConfig;

public class MapBoundsHandlerPlayer {
    private final TiledMapTileLayer borders;

    public MapBoundsHandlerPlayer(TiledMapTileLayer borders) {
        this.borders = borders;
    }

    public void constrainPlayer(Player player) {
        //System.out.println(player.rectangle.x + " " + player.rectangle.y);

        // Check each corner of the player's rectangle
        if (isTileBlocked(player.rectangle.x, player.rectangle.y) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y) ||
            isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height) ||
            isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height)) {

            while (isTileBlocked(player.rectangle.x, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y) ||
                isTileBlocked(player.rectangle.x, player.rectangle.y + player.rectangle.height) ||
                isTileBlocked(player.rectangle.x + player.rectangle.width, player.rectangle.y + player.rectangle.height)) {
                if (player.rectangle.x > 500 ) {
                    player.rectangle.x -= Math.signum(GameConfig.PLAYER_SPEED);
                    //player.rectangle.y -= Math.signum(GameConfig.PLAYER_SPEED);
                }
                if (player.rectangle.x < 500 ) {
                    player.rectangle.x += Math.signum(GameConfig.PLAYER_SPEED);
                    //player.rectangle.y += Math.signum(GameConfig.PLAYER_SPEED);
                }
                if (player.rectangle.y > 500 ) {
                    //player.rectangle.x -= Math.signum(GameConfig.PLAYER_SPEED);
                    player.rectangle.y -= Math.signum(GameConfig.PLAYER_SPEED);
                }
                if (player.rectangle.y < 500 ) {
                    //player.rectangle.x += Math.signum(GameConfig.PLAYER_SPEED);
                    player.rectangle.y += Math.signum(GameConfig.PLAYER_SPEED);
                }
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
