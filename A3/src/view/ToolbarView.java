package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.AnimatorModel;
import model.IView;

public class ToolbarView extends JPanel implements IView {

	private static final long serialVersionUID = 1L;
	private AnimatorModel model;
	private JButton drawToggle, eraseToggle, selectToggle;
	private JButton clearButton, saveButton, loadButton, insertFrame;
	ImageIcon select, deselect;

	public ToolbarView(AnimatorModel aModel) {
		super();

		ImageIcon trash = new ImageIcon("images/trash.png");
		clearButton = new JButton(trash);
		clearButton.setFocusable(false);
		clearButton.setText("Clear");
		clearButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		clearButton.setHorizontalTextPosition(SwingConstants.CENTER);

		ImageIcon draw = new ImageIcon("images/draw2.png");
		drawToggle = new JButton(draw);
		drawToggle.setFocusable(false);
		drawToggle.setText("Draw");
		drawToggle.setVerticalTextPosition(SwingConstants.BOTTOM);
		drawToggle.setHorizontalTextPosition(SwingConstants.CENTER);

		ImageIcon erase = new ImageIcon("images/eraser.png");
		eraseToggle = new JButton(erase);
		eraseToggle.setFocusable(false);
		eraseToggle.setText("Erase");
		eraseToggle.setVerticalTextPosition(SwingConstants.BOTTOM);
		eraseToggle.setHorizontalTextPosition(SwingConstants.CENTER);

		select = new ImageIcon("images/dotssquare.png");
		deselect = new ImageIcon("images/dotssquare_deselect.png");
		selectToggle = new JButton(select);
		selectToggle.setFocusable(false);
		selectToggle.setText("Select");
		selectToggle.setVerticalTextPosition(SwingConstants.BOTTOM);
		selectToggle.setHorizontalTextPosition(SwingConstants.CENTER);

		ImageIcon copy = new ImageIcon("images/copy.png");
		insertFrame = new JButton(copy);
		insertFrame.setFocusable(false);
		insertFrame.setText("Insert");
		insertFrame.setVerticalTextPosition(SwingConstants.BOTTOM);
		insertFrame.setHorizontalTextPosition(SwingConstants.CENTER);

		ImageIcon save = new ImageIcon("images/save.png");
		saveButton = new JButton(save);
		saveButton.setFocusable(false);
		saveButton.setText("Export");
		saveButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		saveButton.setHorizontalTextPosition(SwingConstants.CENTER);

		ImageIcon load = new ImageIcon("images/open-file.png");
		loadButton = new JButton(load);
		loadButton.setFocusable(false);
		loadButton.setText("Import");
		loadButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		loadButton.setHorizontalTextPosition(SwingConstants.CENTER);

		this.add(drawToggle);
		this.add(eraseToggle);
		this.add(selectToggle);
		this.add(insertFrame);
		this.add(clearButton);
		this.add(saveButton);
		this.add(loadButton);

		this.registerListeners();
		model = aModel;
		model.addView(this);
	}

	private void registerListeners() {
		drawToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setState(AnimatorModel.State.draw);
			}
		});

		eraseToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setState(AnimatorModel.State.erase);
			}
		});

		selectToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (model.getState() == AnimatorModel.State.selection) {
					model.deselect();
				} else {
					model.setState(AnimatorModel.State.selection);
				}
			}
		});

		insertFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.insertFrame();
			}
		});

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.restart();
			}
		});

		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setState(AnimatorModel.State.export);
			}
		});

		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.loadAnimation();
				model.gotoZero();
				setAllButtons(true);
			}
		});

	}

	public void setAllButtons(boolean b){
		eraseToggle.setEnabled(b);
		drawToggle.setEnabled(b);
		insertFrame.setEnabled(b);
		clearButton.setEnabled(b);
		selectToggle.setEnabled(b);
	}
	@Override
	public void updateView() {

		AnimatorModel.State state = model.getState();
		// disable everything during playback
		if (state == AnimatorModel.State.playing || state == AnimatorModel.State.load) {
			this.setAllButtons(false);
		} else {
			this.setAllButtons(true);
		}
		if (state == AnimatorModel.State.draw) {
			drawToggle.setEnabled(false);
		} else if (state == AnimatorModel.State.erase) {
			eraseToggle.setEnabled(false);
		}
		if (state == AnimatorModel.State.selection
				&& model.getSelectedIndices().size() > 0) {
			selectToggle.setIcon(deselect);
			selectToggle.setText("Deselect");
		} else {
			selectToggle.setIcon(select);
			selectToggle.setText("Select");
		}
	}
}
