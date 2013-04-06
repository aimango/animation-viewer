package com.elisa.a5;

import java.util.ArrayList;

import android.graphics.Matrix;
import android.graphics.Point;

//object that holds all the information for 1 drawn line segment.
public class Segment extends Object {

	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Matrix> atList = new ArrayList<Matrix>();
	private int startTime;
	private int endTime;

	private String color;
	private int stroke;

	public Segment(int start, int end, String c, int s) {
		startTime = start;
		endTime = end;
		atList.add(new Matrix());
		color = c;
		stroke = s;
	}

	public void setAtList(ArrayList<Matrix> at) {
		atList = at;
	}

	public ArrayList<Matrix> getAtList() {
		return atList;
	}

	public void setPts(ArrayList<Point> pts) {
		points = pts;
	}

	public String getColor() {
		return color;
	}

	public int getStroke() {
		return stroke;
	}

	public int size() {
		return points.size();
	}

	public Point getPoint(int i) {
		return points.get(i);
	}

	public int getStartTime() {
		return this.startTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getEndTime() {
		return this.endTime;
	}

	public void setSegmentTranslate(int x, int y, int frame) {
		if (x != 0 && y != 0) {
			Matrix a = atList.get(frame - startTime);
			a.setTranslate(x, y); // check > <
		}
	}

	// get all the transforms at a particular frame
	public ArrayList<Point> getTranslates(int frame) {
		ArrayList<Point> destination = new ArrayList<Point>();

		// dont draw if doesnt exist at that frame.
		if (frame > endTime || frame < startTime)
			return destination;

		for (Point p : points) {
			Point dest = null;

			float f[] = new float[9];
			atList.get(frame - startTime).getValues(f);

			dest = new Point();
			dest.x = (int) (p.x + f[2]);
			dest.y = (int) (p.y + f[5]);
			destination.add(dest);
		}
		// for (Point p:destination){
		// Log.w("hi",String.valueOf(frame)+ " "
		// +String.valueOf(p.x)+" "+String.valueOf(p.y));
		// }
		return destination;
	}
}