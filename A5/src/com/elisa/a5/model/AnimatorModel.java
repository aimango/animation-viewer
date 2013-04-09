package com.elisa.a5.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

//TODO: check for right file format?
public class AnimatorModel extends Object {
	public enum State {
		draw, playing
	};

	private State state = State.draw;
	private int currframe = 0;
	private int totalframes = 0;
	private int dimenx, dimeny;
	private String paletteColor = String.valueOf(Color.BLACK);
	private int strokeSize = 5;
	private String currFile = "";

	private ArrayList<Segment> segments = new ArrayList<Segment>();

	public AnimatorModel() {
	}

	public String getCurrFile() {
		return currFile;
	}

	public int getDimenX() {
		return dimenx;
	}

	public int getDimenY() {
		return dimeny;
	}

	public void setSegments(ArrayList<Segment> segs) {
		segments = segs;
	}

	public void setTotalFrames(int i) {
		totalframes = i;
	}

	public void setPaletteColor(String c) {
		paletteColor = c;
	}

	public String getPaletteColor() {
		return paletteColor;
	}

	public void setStrokeSize(int sz) {
		strokeSize = sz;
	}

	public int getStrokeSize() {
		return strokeSize;
	}

	public void setState(State passedState) {
		state = passedState;
	}

	public State getState() {
		return state;
	}

	public ArrayList<Segment> getSegments() {
		return segments;
	}

	public int getFrame() {
		return this.currframe;
	}

	public int getTotalFrames() {
		return this.totalframes;
	}

	public void setFrame(int frame) {
		if (frame <= totalframes) {
			this.currframe = frame;
		}
	}

	public void gotoZero() {
		currframe = 0;
	}

	public void increaseFrames(boolean copyframe) {
		currframe++;
		if (currframe > totalframes || copyframe) {
			totalframes++;
		}
	}

	public void decreaseFrames() {
		if (currframe > 0) {
			currframe--;
		}
	}

	public void loadAnimation(String filename) {
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + filename);
		if (!file.exists()) {
			Log.e("Cannot find file", filename);
			return;
		} else {
			currFile = filename;
			Log.i("Opening file", filename);
		}

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		Document docc = null;
		try {
			docc = db.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		docc.getDocumentElement().normalize();
		int maxFrame = 0;

		Element animation = (Element) (docc.getElementsByTagName("animation"))
				.item(0);
		try {
			dimenx = Integer.parseInt(animation.getAttribute("dimenX"));
			dimeny = Integer.parseInt(animation.getAttribute("dimenY"));

		} catch (NumberFormatException e) {
			dimenx = 720;
			dimeny = 452;
		}

		NodeList nodeLst = docc.getElementsByTagName("segment");
		ArrayList<Segment> segs = new ArrayList<Segment>();
		for (int s = 0; s < nodeLst.getLength(); s++) { // segments
			Node fstNode = nodeLst.item(s);
			if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

				Element fstElmnt = (Element) fstNode;
				String c = fstElmnt.getAttribute("color");

				int start = Integer.parseInt(fstElmnt.getAttribute("start"));
				int end = Integer.parseInt(fstElmnt.getAttribute("end"));
				int stroke = Integer.parseInt(fstElmnt.getAttribute("stroke"));

				if (end > maxFrame) {
					maxFrame = end;
				}
				ArrayList<Point> points = new ArrayList<Point>();
				NodeList pts = fstElmnt.getElementsByTagName("point");
				for (int p = 0; p < pts.getLength(); p++) {
					Node leNode = pts.item(p);
					if (leNode.getNodeType() == Node.ELEMENT_NODE) {
						Element lefirst = (Element) leNode;
						double x = Double
								.parseDouble(lefirst.getAttribute("x"));
						double y = Double
								.parseDouble(lefirst.getAttribute("y"));
						Point point = new Point((int) x, (int) y);
						points.add(point);
					}
				}

				ArrayList<Matrix> atList = new ArrayList<Matrix>();
				NodeList ats = fstElmnt.getElementsByTagName("transform");
				for (int p = 0; p < ats.getLength(); p++) {
					Node leNode = ats.item(p);
					if (leNode.getNodeType() == Node.ELEMENT_NODE) {
						Element lefirst = (Element) leNode;
						float x = Float.parseFloat(lefirst.getAttribute("x"));
						float y = Float.parseFloat(lefirst.getAttribute("y"));
						Matrix at = new Matrix();
						at.postTranslate(x, y);
						atList.add(at);
					}
				}
				Segment suu = new Segment(start, end, c, stroke);
				suu.setAtList(atList);
				suu.setPts(points);
				segs.add(suu);
			}
		}
		this.setSegments(segs);
		totalframes = maxFrame;

		Log.i("Successfully imported file", filename);
	}

}