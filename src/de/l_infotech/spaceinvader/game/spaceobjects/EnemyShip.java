package de.l_infotech.spaceinvader.game.spaceobjects;

import de.l_infotech.spaceinvader.game.components.CoordinateBox;
import de.l_infotech.spaceinvader.game.components.StaticMatrix;

/**
 * This class represents a Space Object
 * 
 * @author Ludwig Biermann
 * @version 1.0
 * 
 */
public class EnemyShip implements SpaceObject {

	private CoordinateBox box;
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
	 */
	public EnemyShip(int x, int y, int width, int height) {
		box = new CoordinateBox(x, x + width - 1, y, y + height - 1);
		grafik = new byte[width][height];
		destroyGrafik = new byte[width][height];
		alive = true;

		grafik = StaticMatrix.enemy_size3x3;
		destroyGrafik = StaticMatrix.destroy_size3x3;
	}

	@Override
	public CoordinateBox getCoordinates() {
		return box;
	}

	@Override
	public String toString() {
		return "A Enemy Ship";
	}

	@Override
	public void destroy() {
		alive = false;

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
