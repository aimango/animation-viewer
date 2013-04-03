package com.example.a5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class AnimatorModel extends Object {
	public enum State {
		draw, erase, selection, dragged, playing, export
	};

	private State state = State.draw;
	private boolean stillDragging = true;
	private int currframe = 0;
	private int totalframes = 0;

	private String paletteColor = String.valueOf(Color.BLACK);
	private int strokeSize = 5;

	private ArrayList<Segment> segments = new ArrayList<Segment>();
	private ArrayList<Integer> selectedIndices = new ArrayList<Integer>();
	private Segment currSegment = new Segment(currframe, currframe,
			paletteColor, strokeSize);
	private Segment selectingSegment = new Segment(currframe, currframe,
			paletteColor, strokeSize);

	// Override the default constructor, making it private.
	public AnimatorModel() {
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
		// this.updateAllViews();
	}

	public int getStrokeSize() {
		return strokeSize;
	}

	public void setState(State passedState) {
		// remove the selected items & lasso trace
		if (passedState == State.draw || passedState == State.erase
				|| passedState == State.playing) {
			selectedIndices.clear();
			this.removeLasso();
		}

		state = passedState;
		// this.updateAllViews();
	}

	public State getState() {
		return state;
	}

	public void pushFrame() {
		for (Segment s : segments) {
			if (!s.isErased(currframe) && s.getEndTime() <= currframe) {
				s.createFrame(currframe + 1);
			}
		}
		currframe++;
		if (currframe > totalframes) {
			totalframes++;
		}
		// this.updateAllViews();
	}

	// increment totalframes, insert a copy of current frame for each segment.
	public void insertFrame() {
		increaseFrames(true);
		for (Segment s : segments) {
			if (currframe > totalframes) {
				s.createFrame(currframe);
			} else {
				s.copyFrame(currframe);
			}
		}
		System.out.println("Inserted frame.");

		// this.updateAllViews();
	}

	public void addPointToSegment(Point point) {
		if (state == State.draw) {
			currSegment.addPoint(point);
			if (stillDragging && segments.size() == 0) {
				segments.add(currSegment);
			}
			segments.set(segments.size() - 1, currSegment);
		} else if (state == State.selection
				&& this.getSelectedIndices().size() == 0) {
			selectingSegment.addPoint(point);
		}
		// this.updateAllViews();
	}

	public void createSegment() {
		currSegment = new Segment(currframe, currframe, this.getPaletteColor(),
				strokeSize);
		segments.add(currSegment);
	}

	public ArrayList<Segment> getSegments() {
		return segments;
	}

	public Segment getSelectingSegment() {
		return selectingSegment;
	}

	public void addTranslate(int x, int y) {
		int currFrame = this.getFrame();
		for (int index : selectedIndices) {
			segments.get(index).setSegmentTranslate(x, y, currFrame);
		}
	}

	public void eraseAction(int oldX, int oldY) {
		int largestEndTime = 0, currEndTime = 0;
		for (Segment s : segments) {
			ArrayList<Point> points = s.getTranslates(this.getFrame());

			for (int j = 0; j < points.size(); j++) {
				Point currPoint = points.get(j);
				int x = currPoint.x;
				int y = currPoint.y;

				if (oldX > x - 10 && oldX < x + 10 && oldY > y - 10
						&& oldY < y + 10) {
					// System.out.println("Erasing the " + i + "th obj");
					s.setEndTime(currframe - 1);
					break;
				}
			}
			currEndTime = s.getEndTime();
			if (currEndTime > largestEndTime) {
				largestEndTime = currEndTime;
			}
		}
		if (largestEndTime < totalframes) {
			totalframes = largestEndTime;
			currframe = largestEndTime;
			System.out.println("Number of frames cut down to  " + totalframes);
		}
		// this.updateAllViews();
	}

	// public void selectAction(Path lassoPath) {
	// for (int i = 0; i < segments.size(); i++) {
	// ArrayList<Point> points = segments.get(i).getTranslates(
	// this.getFrame());
	// int size = points.size();
	// for (int j = 0; j < size; j++) {
	// Point currPoint = points.get(j);
	// if (!lassoPath.contains(currPoint)) {
	// break;
	// }
	// // all pts inside lasso, so we can select this segment
	// else if (j == size - 1) {
	// System.out.println("Selected!");
	// this.addSelectedIndex(i);
	// }
	// }
	// }
	// this.removeLasso();
	// }

	public void deselect() {
		selectedIndices.clear();
		this.removeLasso();
		state = State.selection; // allow user to select again.
		// this.updateAllViews();
	}

	public void removeLasso() {
		selectingSegment = new Segment(currframe, currframe, paletteColor,
				strokeSize);
		// this.updateAllViews();
	}

	public void setStillDragging(boolean stillDragging) {
		this.stillDragging = stillDragging;
	}

	public boolean getStillDragging() {
		return this.stillDragging;
	}

	public void addSelectedIndex(int selected) {
		selectedIndices.add(selected);
	}

	public ArrayList<Integer> getSelectedIndices() {
		return this.selectedIndices;
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
			// this.updateAllViews();
		}
	}

	public void gotoZero() {
		currframe = 0;
		// this.updateAllViews();
	}

	public void increaseFrames(boolean copyframe) {
		currframe++;
		if (currframe > totalframes || copyframe) {
			totalframes++;
		}
		// this.updateAllViews();
	}

	public void decreaseFrames() {
		if (currframe > 0) {
			currframe--;
			// this.updateAllViews();
		}
	}

	public void exportImage() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("animation");
			doc.appendChild(rootElement);

			for (Segment s : this.getSegments()) {
				Element segment = doc.createElement("segment");
				rootElement.appendChild(segment);

				segment.setAttribute("color", String.valueOf(s.getColor()));
				segment.setAttribute("stroke", String.valueOf(s.getStroke()));
				segment.setAttribute("start", String.valueOf(s.getStartTime()));
				segment.setAttribute("end", String.valueOf(s.getEndTime()));

				for (int m = 0; m < s.size(); m++) {
					Point p = s.getPoint(m);
					Element point = doc.createElement("point");
					segment.appendChild(point);
					point.setAttribute("x", String.valueOf(p.x));
					point.setAttribute("y", String.valueOf(p.y));
				}

				ArrayList<Matrix> atList = s.getAtList();
				int i = 0;
				for (Matrix a : atList) { // check bounds
					Element transform = doc.createElement("transform");
					segment.appendChild(transform);
					transform.setAttribute("frame",
							String.valueOf(s.getStartTime() + i + 1));

					float mVal[] = new float[9];
					a.getValues(mVal);

					transform.setAttribute("x", String.valueOf(mVal[2]));
					transform.setAttribute("y", String.valueOf(mVal[5]));
					i++;
				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(
					"../xml-files/file.xml"));

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);

			System.out.println("File saved!");

			this.setState(State.selection);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	public void loadAnimation(String filename) {
		Log.w(Environment.getExternalStorageDirectory().toString(), "hi");
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + filename);
		if (!file.exists()) {
			Log.w("File aint there", "fml");
			file.mkdirs();
		} else {
			Log.w("File is there", "yay");
		}

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document docc = null;
		try {
			docc = db.parse(file);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		docc.getDocumentElement().normalize();
		System.out.println("Root element "
				+ docc.getDocumentElement().getNodeName());

		int maxFrame = 0;
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

				if (end > maxFrame)
					maxFrame = end;
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
				System.out.println("at len" + ats.getLength());
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
		this.setTotalFrames(maxFrame);
		// this.updateAllViews();
	}

	public void restart() {
		segments.clear();
		selectingSegment = new Segment(currframe, currframe, paletteColor,
				strokeSize);
		selectedIndices.clear();
		totalframes = 0;
		currframe = 0;
		// this.updateAllViews();
	}
}