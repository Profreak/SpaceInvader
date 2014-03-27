package de.l_infotech.spaceinvader.game.spaceobjects;

import de.l_infotech.spaceinvader.game.components.CoordinateBox;
import de.l_infotech.spaceinvader.game.components.StaticMatrix;

/**
 * This class represent a Laser Shot
 * 
 * @author Ludwig Biermann
 * @version 1.0
 * 
 */
public class Laser implements SpaceObject {

	private CoordinateBox box;
	private boolean alive;
	byte[][] grafik;

	/**
	 * creates a new Space Ship
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @param t
	 *            thickness of the Space Ship
	 */
	public Laser(int x, int y, int t) {
		box = new CoordinateBox(x, x, y, y + t);
		alive = true;
		grafik = new byte[2][1]; // grafik hack
		grafik = StaticMatrix.laser_size1x2;
	}

	@Override
	public CoordinateBox getCoordinates() {
		return box;
	}

	@Override
	public String toString() {
		return "a Laser Shoot";
	}

	@Override
	public void destroy() {
		alive = false;
	}

	@Override
	public byte[][] getGraphics() {
		return grafik;
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public byte[][] getDestroyGraphics() {
		return grafik;
	}
}