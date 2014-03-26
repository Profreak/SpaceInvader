package de.l_infotech.spaceinvader.game;

import android.util.Log;

/**
 * a Coordinate Box 
 * 
 * @author Ludwig Biermann
 *
 */
public class Box {

	public static final String TAG = Box.class.getSimpleName();
	public int x0;
	public int x1;
	public int y0;
	public int y1;
	
	/**
	 * Creates a new Box
	 * 
	 * @param x0 left x coordinates
	 * @param x1 right x coordinates
	 * @param y0 top y coordinates
	 * @param y1 bottom y coordinates
	 */
	public Box(int x0, int x1, int y0, int y1) {
		Log.d(TAG, "x0: " + x0 + " y0: " + y0 + " x1: " +  x1  + " y1: " + y1 );
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
	}
	
	/**
	 * Is a Hit
	 * 
	 * @param x x Coordinate
	 * @param y y Coordinates
	 * @return true if hit 
	 */
	
	// x0: 1 - x1: 3
	// y0: 0 - y1: 2
	//	x: 2 -  y: 1
	//
	//
	public boolean isHit(int x, int y){
		if(x0 <= x && x <= x1) {
			if(y0 <= x && x <= y1) {
				Log.d("HIT", "HIT at x: "+ x + " y: " + y);
				Log.d("HIT", "HIT at x: "+ x0 + " y: " + x1);
				Log.d("HIT", "HIT at x: "+ y0 + " y: " + y1);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Move the Hitbox by a x and y Coordinate
	 * 
	 * @param x x Coordinate
	 * @param y y Coordinate
	 */
	public boolean move(int x, int y){
		if((x0+x) >= 0 && (y0+y) >= 0 && (x1+x) < SpaceEngine.MAX_RESOLUTION && (y1+y) < SpaceEngine.MAX_RESOLUTION){
			Log.d(TAG, "move x: " + x + " y: " + y);
			x0 += x;
			x1 += x;
			y0 += y;
			y1 += y;
			return true;
		}
		return false;
	}
}