package view;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import model.AnimatorModel;
import model.IView;

// icon source: http://www.veryicon.com/icons/system/crystal-clear-actions/
public class PlayerView extends JPanel implements IView {

	private static final long serialVersionUID = 1L;
	private AnimatorModel model;
	private JButton playBtn, fwdBtn, rewindBtn, fastFwd, fastRewind;
	private ImageIcon playIcon;

	public PlayerView(AnimatorModel aModel) {
		super();
		this.model = aModel;

		ImageIcon fwd = new ImageIcon("images/fwd.png");
		fwdBtn = new JButton(fwd);
		fwdBtn.setFocusable(false);

		ImageIcon rewind = new ImageIcon("images/rewind.png");
		rewindBtn = new JButton(rewind);
		rewindBtn.setFocusable(false);

		ImageIcon fastfw = new ImageIcon("images/end.png");
		fastFwd = new JButton(fastfw);
		fastFwd.setFocusable(false);

		ImageIcon fastrw = new ImageIcon("images/begin.png");
		fastRewind = new JButton(fastrw);
		fastRewind.setFocusable(false);

		playIcon = new ImageIcon("images/play.png");
		playBtn = new JButton(playIcon);
		playBtn.setFocusable(false);

		this.add(fastRewind);
		this.add(rewindBtn);
		this.add(playBtn);
		this.add(fwdBtn);
		this.add(fastFwd);
		this.registerListeners();

		// Add a this view as a listener to the model
		model.addView(this);
	}

	private void registerListeners() {
		fwdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.increaseFrames(false);
			}
		});
		rewindBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.decreaseFrames();
			}
		});
		fastFwd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.setFrame(model.getTotalFrames());
			}
		});
		fastRewind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.gotoZero();
			}
		});
		final ImageIcon pause = new ImageIcon("images/pause.png");
		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (playBtn.getIcon() == playIcon) {

					// play from 0 in this case
					if (model.getFrame() == model.getTotalFrames()) {
						model.gotoZero();
					}
					model.setState(AnimatorModel.State.playing);
					playBtn.setIcon(pause);
				} else if (playBtn.getIcon() == pause) {
					model.setState(AnimatorModel.State.draw);
					playBtn.setIcon(playIcon);
				}
			}
		});
	}

	@Override
	public void updateView() {
		int totalFrames = model.getTotalFrames();
		// allow play if there is animation to play
		if (totalFrames > 0) {
			rewindBtn.setEnabled(true);
			fwdBtn.setEnabled(true);
			playBtn.setEnabled(true);
			fastRewind.setEnabled(true);
			fastFwd.setEnabled(true);
		} else if (totalFrames == 0) {
			rewindBtn.setEnabled(false);
			fwdBtn.setEnabled(false);
			playBtn.setEnabled(false);
			fastRewind.setEnabled(false);
			fastFwd.setEnabled(false);
		}

		AnimatorModel.State state = model.getState();
		// disable everything during playback
		if (state != AnimatorModel.State.playing) {
			playBtn.setIcon(playIcon);
		}

		// disable play if currently animating
		if (state == AnimatorModel.State.dragged) {
			rewindBtn.setEnabled(false);
			fwdBtn.setEnabled(false);
			playBtn.setEnabled(false);
			fastRewind.setEnabled(false);
			fastFwd.setEnabled(false);
		}

	}
}
