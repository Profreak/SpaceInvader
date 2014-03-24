package de.l_infotech.spaceinvader.game;

/**
 * 
 * @author Ludwig Biermann
 *
 */
public class Laser implements SpaceObject {
	
	// Annahme links oben ist 0/0
	private Box box;
	private boolean alive;
	int[][] grafik;
	
	/**
	 * creates a new Space Ship
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 * @param t thickness of the Space Ship
	 */
	public Laser(int x, int y, int t) {
		box = new Box(x, x  + t, y, y + 3*t);
		alive = true;
		grafik = new int[x+t][y+3*t];
	}
	
	/**
	 * Returns the Coordinates
	 * 
	 * @return Coordinates 
	 */
	public Box getCoordinates() {
		return box;
	}
	
	@Override
	public String toString(){
		return "a Laser Shoot";
	}

	@Override
	public void destroy() {
		alive = false;
	}

	@Override
	public int[][] getGrafik() {
		return grafik;
	}

	@Override
	public boolean isAlive() {
		return alive;
	}
}