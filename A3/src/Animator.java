/**
 * Elisa Lou 20372456
 * Assignment 3 MVC Updated for Assignment 5
 * Winter '13
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.AnimatorModel;
import view.CanvasView;
import view.PaletteView;
import view.PlayerView;
import view.SliderView;
import view.ToolbarView;

public class Animator {
	public static void main(String[] args) {

		final AnimatorModel model = new AnimatorModel();
		JFrame frame = new JFrame("Assignment 3 - MVC Updated");

		Container content = frame.getContentPane();
		content.setLayout(new BorderLayout());
		CanvasView view = new view.CanvasView(model);
		frame.getContentPane().add(view, BorderLayout.CENTER);

		ToolbarView toolbar = new view.ToolbarView(model);
		content.add(toolbar, BorderLayout.NORTH);

		JPanel subPanel = new JPanel();
		subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
		SliderView slider = new view.SliderView(model);
		PlayerView player = new view.PlayerView(model);
		subPanel.add(slider);
		subPanel.add(player);
		content.add(subPanel, BorderLayout.SOUTH);

		PaletteView customizer = new view.PaletteView(model);
		content.add(customizer, BorderLayout.EAST);

		frame.pack();
		frame.setSize(806, 650);
		frame.setMinimumSize(new Dimension(400, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(100, 100);
		frame.setVisible(true);

		view.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				Component c = (Component) arg0.getSource();
				model.setDimens(c.getSize());
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
			}
		});
	}
}
