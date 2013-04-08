package com.elisa.a5.view;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.elisa.a5.R;
import com.elisa.a5.model.AnimatorModel;
import com.elisa.a5.model.Segment;

/**
 * Elisa Lou 20372456 Assignment 5 Android Winter '13
 */
public class MainActivity extends Activity {
	private AnimatorModel model;
	private Timer t;
	private SeekBar slider;
	private MyView myView;
	private boolean settingsNav = false;
	private TimerTask myTimerTask;
	private Button fwdBtn, playBtn, backBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		slider = (SeekBar) findViewById(R.id.slider);
		model = new AnimatorModel();
		t = new Timer();

		LinearLayout v = (LinearLayout) findViewById(R.id.canvasArea);
		myView = new MyView(this);
		v.addView(myView);
		this.registerControllers();
	}

	private void setPlayback(boolean b) {
		fwdBtn.setEnabled(b);
		backBtn.setEnabled(b);
		playBtn.setEnabled(b);
	}

	// button and slider logic.
	private void registerControllers() {

		fwdBtn = (Button) findViewById(R.id.fwd);
		fwdBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				model.increaseFrames(false);
				slider.setProgress(model.getFrame());
				myView.invalidate();
			}
		});

		backBtn = (Button) findViewById(R.id.back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				model.decreaseFrames();
				slider.setProgress(model.getFrame());
				myView.invalidate();
			}
		});

		playBtn = (Button) findViewById(R.id.play);
		playBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (playBtn.getText() == "Play") {
					playBtn.setText("Pause");

					backBtn.setEnabled(false);
					fwdBtn.setEnabled(false);
					if (slider.getProgress() >= model.getTotalFrames()) {
						model.gotoZero();
						slider.setProgress(model.getFrame());
					}
					model.setState(AnimatorModel.State.playing);
				} else {
					playBtn.setText("Play");
					setPlayback(true);
					model.setState(AnimatorModel.State.draw);

				}
			}
		});

		slider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				model.setFrame(progress);
				TextView lbl = (TextView) findViewById(R.id.timeLbl);
				lbl.setText("Frame: " + progress);
				myView.invalidate();
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				if (model.getTotalFrames() > 0) {
					model.setState(AnimatorModel.State.draw);
					setPlayback(true);
				}
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		slider.setMax(0);
		setPlayback(false);
	}

	@Override
	public void onResume() {
		myView.invalidate();

		// grab filename from sharedprefs
		SharedPreferences prefs = getSharedPreferences("MyPrefsFile", 0);
		String fileExplore = prefs.getString("fileexplore", "no");

		// if we selected something while in the file explorer
		if (fileExplore.equals("yes")) {
			String name = prefs.getString("filename", "ohno");
			model.loadAnimation(name);

			slider.setMax(model.getTotalFrames());
			model.gotoZero();
			slider.setProgress(model.getFrame());
		}
		// if we went to settings or selected something while in the file
		// explorer
		if (settingsNav || fileExplore.equals("yes")) {
			// grab saved fps from sharedprefs or use default of 30
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(MainActivity.this);
			int fps = Integer.parseInt(sharedPrefs.getString("fps", "30"));

			// cancel old timer if it's scheduled
			if (t != null) {
				t.cancel();
			}
			t = new Timer();
			myTimerTask = new TimerTask() {
				@Override
				public void run() {
					if (model.getState() == AnimatorModel.State.playing) {
						if (model.getFrame() >= slider.getMax()) {
							model.setState(AnimatorModel.State.draw);
							MainActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									playBtn.setText("Play");
									setPlayback(true);
								}
							});
						} else {
							model.increaseFrames(false);
							slider.setProgress(model.getFrame());
							myView.postInvalidate();
						}
					}
				}
			};
			t.scheduleAtFixedRate(myTimerTask, 0, 1000 / fps);

			// Reset the flags
			settingsNav = false;
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("fileexplore", "no");
			editor.commit();
		}
		if (slider.getMax() > 0) {
			this.setPlayback(true);
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Action bar items
		switch (item.getItemId()) {
		case R.id.menu_load:
			Intent myIntent = new Intent(MainActivity.this,
					FileExplorerActivity.class);

			model.setState(AnimatorModel.State.draw);
			MainActivity.this.startActivity(myIntent);

			return true;

		case R.id.menu_settings:
			Intent i = new Intent(this, SettingsActivity.class);
			model.setState(AnimatorModel.State.draw);
			MainActivity.this.startActivity(i);
			settingsNav = true;
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// custom view to draw the animation
	public class MyView extends View {
		Paint paint = new Paint();
		Path path = new Path();

		public MyView(Context context) {
			super(context);
		}

		private String convertColor(String color) {
			if (color.equals("white")) {
				return "#FFFFFF";
			} else if (color.equals("light blue")) {
				return "#99CCFF";
			} else if (color.equals("red")) {
				return "#CC0000";
			} else if (color.equals("yellow")) {
				return "#FFECB3";
			} else if (color.equals("purple")) {
				return "#CC99FF";
			} else if (color.equals("black")) {
				return "#000000";
			} else if (color.equals("grey")) {
				return "#7D7D7D";
			} else if (color.equals("light green")) {
				return "#47FF47";
			} else if (color.equals("dark green")) {
				return "#008040";
			} else if (color.equals("dark blue")) {
				return "#0059B3";
			} else if (color.equals("orange")) {
				return "#FF9900";
			} else if (color.equals("pink")) {
				return "#FF66CC";
			}
			Log.i("Default the color to white", color);
			return "#FFFFFF";
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			int width = this.getWidth();
			int height = this.getHeight();
			ArrayList<Segment> segments = model.getSegments();
			if (segments.size() > 0) {
				for (Segment s : segments) {
					int currFrame = model.getFrame();

					ArrayList<Point> transformedPoints = s
							.getTranslates(currFrame);
					if (transformedPoints.size() > 0) {
						Point first = transformedPoints.get(0);
						int firstx = first.x * width / 720;
						int firsty = first.y * height / 452;
						path.moveTo(firstx, firsty);

						for (int j = 1; j < s.size(); j++) {
							Point to = transformedPoints.get(j);
							path.lineTo(to.x * width / 720, to.y * height / 452);
						}
						if (transformedPoints.size() == 1) {
							path.lineTo(firstx, firsty);
						}
					}

					int stroke = s.getStroke();
					paint.setColor(Color.parseColor(s.getColor()));
					paint.setStrokeWidth(stroke);
					paint.setStyle(Paint.Style.STROKE);

					// grab saved background colour from sharedprefs
					SharedPreferences sharedPrefs = PreferenceManager
							.getDefaultSharedPreferences(MainActivity.this);
					String color = sharedPrefs.getString("colors", "white");
					color = convertColor(color);
					this.setBackgroundColor(Color.parseColor(color));

					canvas.drawPath(path, paint);
					path.reset();
				}
			}
		}
	}
}