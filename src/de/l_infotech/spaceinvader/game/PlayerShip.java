package de.l_infotech.spaceinvader.game;

public class PlayerShip implements SpaceObject {
	
	// Annahme links oben ist 0/0
	private Box box;
	private int lives;
	private byte[][] grafik;
	private byte[][] destroyGrafik;
	private boolean alive;
	private byte a = SpaceEngine.SHINE;
	
	private byte[][] size3 = new byte[][]{
		{0,a,0},
		{0,a,0},
		{a,0,a}};
	
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
		box = new Box(x, x + width -1, y, y + height -1);
		grafik = new byte[width][height];
		destroyGrafik = new byte[width][height];
		grafik = size3;
		destroyGrafik = size3;
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
	public byte[][] getGrafik() {
		if(!alive){
			return destroyGrafik;
		}
		return grafik;
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public byte[][] getDestroyGrafik() {
		// TODO Auto-generated method stub
		return destroyGrafik;
	}
}
