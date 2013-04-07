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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.elisa.a5.R;
import com.elisa.a5.model.AnimatorModel;
import com.elisa.a5.model.Segment;

public class MainActivity extends Activity {
	private AnimatorModel model;
	private Timer t;
	private SeekBar slider;
	private MyView myView;
	private boolean settings = false;
	private TimerTask myTimerTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		slider = (SeekBar) findViewById(R.id.slider);
		model = new AnimatorModel();
		t = new Timer();

		RelativeLayout v = (RelativeLayout) findViewById(R.id.mainLayout);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, R.id.back);
		params.addRule(RelativeLayout.ABOVE, R.id.slider);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

		myView = new MyView(this);
		v.addView(myView, params);

		final Button fwdBtn = (Button) findViewById(R.id.fwd);
		fwdBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				model.increaseFrames(false);
				slider.setProgress(model.getFrame());
				myView.invalidate();
			}
		});

		final Button backBtn = (Button) findViewById(R.id.back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				model.decreaseFrames();
				slider.setProgress(model.getFrame());
				myView.invalidate();
			}
		});

		Button playBtn = (Button) findViewById(R.id.play);
		playBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				backBtn.setEnabled(false);
				fwdBtn.setEnabled(false);
				if (slider.getProgress() >= model.getTotalFrames()) {
					model.gotoZero();
					slider.setProgress(model.getFrame());
				}
				model.setState(AnimatorModel.State.playing);
			}
		});

		Button stopBtn = (Button) findViewById(R.id.stop);
		stopBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				backBtn.setEnabled(true);
				fwdBtn.setEnabled(true);
				model.setState(AnimatorModel.State.draw);
			}
		});

		slider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				model.setFrame(progress);
				TextView lbl = (TextView) findViewById(R.id.timeLbl);
				lbl.setText("Current Frame:" + progress);
				myView.invalidate();
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	@Override
	public void onResume() {
		myView.invalidate();
		SharedPreferences prefs = getSharedPreferences("MyPrefsFile", 0);
		String fileExplore = prefs.getString("fileexplore", "no");
		if (fileExplore.equals("yes")) {
			String name = prefs.getString("filename", "ohno");
			model.loadAnimation(name);
			slider.setMax(model.getTotalFrames());
			model.gotoZero();
			slider.setProgress(model.getFrame());
		}
		if (settings || fileExplore.equals("yes")) {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(MainActivity.this);
			int fps = Integer.parseInt(sharedPrefs.getString("fps", "30"));
			t = new Timer();
			// this will run when timer elapses
			myTimerTask = new TimerTask() {
				@Override
				public void run() {
					if (model.getState() == AnimatorModel.State.playing) {
						if (model.getFrame() >= model.getTotalFrames()) {
							model.setState(AnimatorModel.State.draw);
						} else {
							model.increaseFrames(false);
							slider.setProgress(model.getFrame());
							myView.postInvalidate();
						}
					}
				}
			};
			t.scheduleAtFixedRate(myTimerTask, 0, 1000 / fps);
			settings = false;
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("fileexplore", "no");
			editor.commit();
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_load:
			Intent myIntent = new Intent(MainActivity.this, FileExplorer.class);
			t.cancel();
			MainActivity.this.startActivity(myIntent);

			return true;

		case R.id.menu_settings:
			Intent i = new Intent(this, SettingsActivity.class);
			t.cancel();
			MainActivity.this.startActivity(i);
			settings = true;
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class MyView extends View {
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
			Log.w("Other color, default to white", color);
			return "#FFFFFF";
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			@SuppressWarnings("deprecation")
			int width = getWindowManager().getDefaultDisplay().getWidth();
			
			ArrayList<Segment> segments = model.getSegments();
			if (segments.size() > 0) {
				for (Segment s : segments) {
					int currFrame = model.getFrame();
					Path path = new Path();

					// make sure # points is greater than 0 and that the segment
					// is spse to be visible in the current frame
					ArrayList<Point> transformedPoints = s
							.getTranslates(currFrame);
					if (transformedPoints.size() > 0) {
						Point first = transformedPoints.get(0);
						path.moveTo(first.x*width/720, first.y*width/720);

						for (int j = 1; j < s.size(); j++) {
							Point to = transformedPoints.get(j);
							path.lineTo(to.x*width/720, to.y*width/720);
						}
						if (transformedPoints.size() == 1) {
							path.lineTo(first.x*width/720, first.y*width/720);
						}
					}

					int stroke = s.getStroke();
					Paint paint = new Paint();
					paint.setColor(Color.parseColor(s.getColor()));
					paint.setStrokeWidth(stroke);
					paint.setStyle(Paint.Style.STROKE);

					SharedPreferences sharedPrefs = PreferenceManager
							.getDefaultSharedPreferences(MainActivity.this);
					String color = sharedPrefs.getString("colors", "white");
					color = convertColor(color);
					this.setBackgroundColor(Color.parseColor(color));
					canvas.drawPath(path, paint);
				}
			}
		}
	}
}