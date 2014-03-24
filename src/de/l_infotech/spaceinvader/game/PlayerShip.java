package de.l_infotech.spaceinvader.game;

public class PlayerShip implements SpaceObject {
	
	// Annahme links oben ist 0/0
	private Box box;
	private int lives;
	private int[][] grafik;
	private int[][] destroyGrafik;
	private boolean alive;
	
	/**
	 * creates a new Space Ship
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 * @param width of the Space Ship
	 * @param height of the Space Ship
	 * @param lives of the ship
	 */
	public PlayerShip(int x, int y,int width, int height, int lives) {
		box = new Box(x, x + width, y, y - height);
		grafik = new int[width][height];
		destroyGrafik = new int[width][height];
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLives() {
		return lives;
	}
	
	/**
	 * Returns the Coordinates
	 * 
	 * @return Coordinates as int[] x than y
	 */
	public Box getCoordinates() {
		return box;
	}
	
	@Override
	public String toString() {
		return "Player Ship";
	}

	@Override
	public void destroy() {
		lives--;
		if(lives <= 0) {
			alive = false;	
		}
	}

	@Override
	public int[][] getGrafik() {
		if(!alive){
			return destroyGrafik;
		}
		return grafik;
	}

	@Override
	public boolean isAlive() {
		return alive;
	}
}
