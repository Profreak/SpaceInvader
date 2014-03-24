package de.l_infotech.spaceinvader.game;

public interface SpaceObject {
	
	public Box getCoordinates();
	
	public void destroy();
	
	public boolean isAlive();
	
	public int[][] getGrafik();
}
