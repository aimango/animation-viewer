package view;

import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.AnimatorModel;
import model.IView;

public class SliderView extends JSlider implements IView {

	private static final long serialVersionUID = 1L;
	private AnimatorModel model;

	public SliderView(AnimatorModel aModel) {
		super();
		model = aModel;
		model.addView(this);
		this.registerControllers();

		this.setValue(0);
		this.setMaximum(1);
		this.setPaintTicks(true);
		this.setPaintLabels(true);
	}

	private void registerControllers() {
		this.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				if (slider.getValueIsAdjusting()) {
					model.setFrame(slider.getValue());
				}
			}
		});
	}

	@Override
	public void updateView() {
		int max = model.getTotalFrames();
		this.setMaximum(max);
		this.setValue(model.getFrame());
		this.setMajorTickSpacing(max / 10);
		
		Hashtable<Integer, JLabel> table = new Hashtable<Integer, JLabel>();
		table.put(0, new JLabel(Integer.toString(0)));
		table.put(max, new JLabel(Integer.toString(max)));
		this.setLabelTable(table);
	}
}
