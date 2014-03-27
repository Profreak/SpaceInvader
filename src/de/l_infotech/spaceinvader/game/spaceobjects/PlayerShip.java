package de.l_infotech.spaceinvader.game.spaceobjects;

import de.l_infotech.spaceinvader.game.components.CoordinateBox;
import de.l_infotech.spaceinvader.game.components.StaticMatrix;

/**
 * This class represents the player ship.
 * 
 * @author Ludwig Biermann
 * @version 1.0
 * 
 */
public class PlayerShip implements SpaceObject {

	// Annahme links oben ist 0/0
	private CoordinateBox box;
	private int lives;
	private byte[][] grafik;
	private byte[][] destroyGrafik;
	private boolean alive;

	/**
	 * creates a new Space Ship
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @param width
	 *            of the Space Ship
	 * @param height
	 *            of the Space Ship
	 * @param lives
	 *            of the ship
	 */
	public PlayerShip(int x, int y, int width, int height, int lives) {
		this.box = new CoordinateBox(x, x + width - 1, y, y + height - 1);
		this.grafik = new byte[width][height];
		this.destroyGrafik = new byte[width][height];
		this.grafik = StaticMatrix.player_size3x3;
		this.destroyGrafik = StaticMatrix.destroy_size3x3;
		this.lives = lives;
		this.alive = true;
	}

	/**
	 * Returns the amount of lives
	 * 
	 * @return amount of lives
	 */
	public int getLives() {
		return lives;
	}

	@Override
	public CoordinateBox getCoordinates() {
		return box;
	}

	@Override
	public String toString() {
		return "Player Ship";
	}

	@Override
	public void destroy() {
		lives--;
		if (lives <= 0) {
			alive = false;
		}
	}

	@Override
	public byte[][] getGraphics() {
		if (!alive) {
			return destroyGrafik;
		}
		return grafik;
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public byte[][] getDestroyGraphics() {
		return destroyGrafik;
	}
}
