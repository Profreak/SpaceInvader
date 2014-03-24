package de.l_infotech.spaceinvader.game;

public class EnemyShip implements SpaceObject {
	
	// Annahme links oben ist 0/0
	private Box box;
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
	 */
	public EnemyShip(int x, int y,int width, int height) {
		box = new Box(x, x + width, y, y - height);
		grafik = new int[width][height];
		destroyGrafik = new int[width][height];
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
	public String toString(){
		return "A Enemy Ship";
	}

	@Override
	public void destroy() {
		alive = false;
		
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
