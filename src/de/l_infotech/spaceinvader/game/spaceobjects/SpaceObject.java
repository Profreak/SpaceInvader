package de.l_infotech.spaceinvader.game.spaceobjects;

import de.l_infotech.spaceinvader.game.components.CoordinateBox;

/**
 * This interface discripes a Space Object
 * 
 * @author Ludwig Biermann
 * @version 1.0
 * 
 */
public interface SpaceObject {

	/**
	 * Returns the Coordinates as Coordinate Box
	 * 
	 * @return Coordinate Box
	 */
	public CoordinateBox getCoordinates();

	/**
	 * set the SpaceObject as destroyed
	 */
	public void destroy();

	/**
	 * Return if the Space Object is alive
	 * 
	 * @return true if the Space Object is alive
	 */
	public boolean isAlive();

	/**
	 * Returns a graphics matrix
	 * 
	 * @return the graphics as byte matrix
	 */
	public byte[][] getGraphics();

	/**
	 * Return a destroy graphics
	 * 
	 * @return the destroy graphics as byte matrix
	 */
	public byte[][] getDestroyGraphics();
}
