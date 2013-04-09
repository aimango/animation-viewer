package model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AnimatorModel extends Object {
	public enum State {
		draw, erase, selection, dragged, playing, export, load
	};

	/* A list of the model's views. */
	private ArrayList<IView> views = new ArrayList<IView>();

	private State state = State.draw;
	private boolean stillDragging = true;
	private int currframe = 0;
	private int totalframes = 0;

	private Color paletteColor = Color.BLACK;
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

	public void setPaletteColor(Color c) {
		paletteColor = c;
	}

	public Color getPaletteColor() {
		return paletteColor;
	}

	public void setStrokeSize(int sz) {
		strokeSize = sz;
		this.updateAllViews();
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
		this.updateAllViews();
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
		this.updateAllViews();
	}

	// increment totalframes, insert a copy of current frame for each segment.
	public void insertFrame() {
		increaseFrames(true);
		for (Segment s : segments) {
			if (!s.isErased(currframe - 1)) {
				if (currframe > totalframes) {
					s.createFrame(currframe);
				} else {
					s.copyFrame(currframe);
				}
			}
		}
		System.out.println("Inserted frame.");

		this.updateAllViews();
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
		this.updateAllViews();
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
		this.updateAllViews();
	}

	public void selectAction(GeneralPath lassoPath) {
		for (int i = 0; i < segments.size(); i++) {
			ArrayList<Point> points = segments.get(i).getTranslates(
					this.getFrame());
			int size = points.size();
			for (int j = 0; j < size; j++) {
				Point currPoint = points.get(j);
				if (!lassoPath.contains(currPoint)) {
					break;
				}
				// all pts inside lasso, so we can select this segment
				else if (j == size - 1) {
					System.out.println("Selected!");
					this.addSelectedIndex(i);
				}
			}
		}
		this.removeLasso();
	}

	public void deselect() {
		selectedIndices.clear();
		this.removeLasso();
		state = State.selection; // allow user to select again.
		this.updateAllViews();
	}

	public void removeLasso() {
		selectingSegment = new Segment(currframe, currframe, paletteColor,
				strokeSize);
		this.updateAllViews();
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
			this.updateAllViews();
		}
	}

	public void gotoZero() {
		currframe = 0;
		this.updateAllViews();
	}

	public void increaseFrames(boolean copyframe) {
		currframe++;
		if (currframe > totalframes || copyframe) {
			totalframes++;
		}
		this.updateAllViews();
	}

	public void decreaseFrames() {
		if (currframe > 0) {
			currframe--;
			this.updateAllViews();
		}
	}

	public void exportImage() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("animation");
		doc.appendChild(rootElement);

		for (Segment s : this.getSegments()) {
			Element segment = doc.createElement("segment");
			rootElement.appendChild(segment);
			String hexColor = String.format("#%06X", (0xFFFFFF & s.getColor()
					.getRGB()));
			segment.setAttribute("color", hexColor);
			segment.setAttribute("stroke", String.valueOf(s.getStroke()));
			segment.setAttribute("start", String.valueOf(s.getStartTime()));
			segment.setAttribute("end", String.valueOf(s.getEndTime()));

			for (int m = 0; m < s.size(); m++) {
				Point p = s.getPoint(m);
				Element point = doc.createElement("point");
				segment.appendChild(point);
				point.setAttribute("x", String.valueOf(p.getX()));
				point.setAttribute("y", String.valueOf(p.getY()));
			}

			ArrayList<AffineTransform> atList = s.getAtList();
			int i = 0;
			for (AffineTransform a : atList) { // check bounds
				Element transform = doc.createElement("transform");
				segment.appendChild(transform);
				transform.setAttribute("frame",
						String.valueOf(s.getStartTime() + i + 1));
				transform.setAttribute("x", String.valueOf(a.getTranslateX()));
				transform.setAttribute("y", String.valueOf(a.getTranslateY()));
				i++;
			}

		}

		// open a file explorer and write the content into xml file
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return ".xml";
			}

			@Override
			public boolean accept(File file) {
				if (file.getName().endsWith(".xml") || file.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}
		});

		int returnVal = fc.showSaveDialog(null);
		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			if (!file.getName().endsWith(".xml")) {
				file = new File(file.getAbsolutePath() + ".xml");
			}
		} else {
			return;
		}

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		}
		DOMSource source = new DOMSource(doc);
		StreamResult result = null;
		result = new StreamResult(file);

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		System.out.println("File saved in " + file.getAbsolutePath());

		this.setState(State.selection);
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	public void loadAnimation() {
		this.setState(State.load);

		// use filechooser to open files
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.getName().endsWith(".xml") || file.isDirectory()) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return ".xml";
			}
		});

		// grey out non-xml files.
		int returnVal = fc.showOpenDialog(null);
		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();

			// handle non-xml selection
			if (!file.getName().endsWith(".xml")) {
				JOptionPane.showMessageDialog(fc,
						"Only xml files can be imported.");
				System.out.println("User chose non-xml file.");
				return;
			}
		} else {
			return;
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
		NodeList nodeLst = docc.getElementsByTagName("segment");
		ArrayList<Segment> segs = new ArrayList<Segment>();
		for (int s = 0; s < nodeLst.getLength(); s++) { // segments
			Node fstNode = nodeLst.item(s);
			if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

				Element fstElmnt = (Element) fstNode;
				Color c = Color.decode(fstElmnt.getAttribute("color"));
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

				ArrayList<AffineTransform> atList = new ArrayList<AffineTransform>();
				NodeList ats = fstElmnt.getElementsByTagName("transform");

				for (int p = 0; p < ats.getLength(); p++) {
					Node leNode = ats.item(p);
					if (leNode.getNodeType() == Node.ELEMENT_NODE) {
						Element lefirst = (Element) leNode;
						double x = Double
								.parseDouble(lefirst.getAttribute("x"));
						double y = Double
								.parseDouble(lefirst.getAttribute("y"));
						AffineTransform at = new AffineTransform();
						at.translate(x, y);
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
		this.updateAllViews();
		System.out.println("Animation imported from file " + file.getName()
				+ " with " + maxFrame + " frames.");
	}

	public void restart() {
		segments.clear();
		selectingSegment = new Segment(currframe, currframe, paletteColor,
				strokeSize);
		selectedIndices.clear();
		totalframes = 0;
		currframe = 0;
		this.updateAllViews();
	}

	public void addView(IView view) {
		views.add(view);
		view.updateView();
	}

	public void removeView(IView view) {
		views.remove(view);
	}

	private void updateAllViews() {
		for (IView view : this.views) {
			view.updateView();
		}
	}
}