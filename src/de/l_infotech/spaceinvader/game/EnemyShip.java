package de.l_infotech.spaceinvader.game;

public class EnemyShip implements SpaceObject {
	
	// Annahme links oben ist 0/0
	private Box box;
	private byte[][] grafik;
	private byte[][] destroyGrafik;
	private boolean alive;
	private byte a = SpaceEngine.SHINE;
	
	private byte[][] size3 = new byte[][]{
			{a,0,a},
			{a,a,a},
			{a,0,a}};

	private byte[][] size3_d = new byte[][]{
			{a,0,a},
			{0,a,0},
			{a,0,a}};
		
	/**
	 * creates a new Space Ship
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 * @param width of the Space Ship
	 * @param height of the Space Ship
	 */
	public EnemyShip(int x, int y,int width, int height) {
		box = new Box(x, x + width -1, y, y + height -1);
		grafik = new byte[width][height];
		destroyGrafik = new byte[width][height];
		alive = true;
		
		grafik = size3;
		destroyGrafik = size3_d;
	}
	
	/**
	 * Returns the Coordinates
	 * 
	 * @return Coordinates as int[] x than y
	 */
	@Override
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
		return destroyGrafik;
	}
	
}
