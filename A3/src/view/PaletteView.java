package view;


import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.AnimatorModel;
import model.IView;

public class PaletteView extends JPanel implements IView {

	private static final long serialVersionUID = 1L;
	private AnimatorModel model;
	private JColorChooser tcc;
	private JButton choose, black, blue, green, violet, pink;
	private JRadioButton small, medium, large;

	public PaletteView(AnimatorModel aModel) {
		super();

		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		tcc = new JColorChooser();
		tcc.setBorder(BorderFactory.createTitledBorder("Choose Draw Color"));

		choose = new JButton("Picker");
		choose.setBorderPainted(false);
		choose.setOpaque(true);
		choose.setFocusable(false);
		choose.setBackground(Color.GRAY);

		small = new JRadioButton("sm");
		small.setFocusable(false);
		medium = new JRadioButton("med");
		medium.setSelected(true);
		small.setFocusable(false);
		large = new JRadioButton("big");
		small.setFocusable(false);

		black = new JButton();
		black.setBackground(Color.BLACK);
		black.setBorderPainted(false);
		black.setOpaque(true);
		black.setFocusable(false);

		blue = new JButton();
		blue.setBackground(new Color(51, 204, 255));
		blue.setBorderPainted(false);
		blue.setOpaque(true);
		blue.setFocusable(false);

		green = new JButton();
		green.setBackground(new Color(51, 255, 102));
		green.setBorderPainted(false);
		green.setOpaque(true);
		green.setFocusable(false);

		violet = new JButton();
		violet.setBackground(new Color(204, 51, 255));
		violet.setBorderPainted(false);
		violet.setOpaque(true);
		violet.setFocusable(false);

		pink = new JButton();
		pink.setBackground(new Color(249, 119, 214));
		pink.setBorderPainted(false);
		pink.setOpaque(true);
		pink.setFocusable(false);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipady = 18;

		this.add(black, constraints);
		constraints.gridy = 1;
		this.add(blue, constraints);
		constraints.gridy = 2;
		this.add(green, constraints);
		constraints.gridy = 3;
		this.add(violet, constraints);
		constraints.gridy = 4;
		this.add(pink, constraints);
		constraints.gridy = 5;
		this.add(choose, constraints);

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder("Stroke"));
		p.add(small);
		p.add(medium);
		p.add(large);
		constraints.gridy = 6;
		this.add(p, constraints);

		this.registerControllers();
		model = aModel;
		this.model.addView(this);
	}

	private void registerControllers() {
		tcc.getSelectionModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				Color newColor = tcc.getColor();
				model.setPaletteColor(newColor);
				choose.setBackground(newColor);
				choose.setBorderPainted(false);
				choose.setOpaque(true);
			}
		});

		choose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog d = new JDialog();
				d.add(tcc);
				d.pack();
				d.setVisible(true);
			}
		});

		small.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setStrokeSize(2);
				medium.setSelected(false);
				large.setSelected(false);
			}
		});
		medium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setStrokeSize(5);
				small.setSelected(false);
				large.setSelected(false);
			}
		});
		large.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setStrokeSize(10);
				medium.setSelected(false);
				small.setSelected(false);
			}
		});

		black.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setPaletteColor(Color.BLACK);
			}
		});
		blue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setPaletteColor(new Color(51, 204, 255));
			}
		});
		green.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setPaletteColor(new Color(51, 255, 102));
			}
		});
		violet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setPaletteColor(new Color(204, 51, 255));
			}
		});
		pink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setPaletteColor(new Color(249, 119, 214));
			}
		});
	}

	@Override
	public void updateView() {
	}
}
