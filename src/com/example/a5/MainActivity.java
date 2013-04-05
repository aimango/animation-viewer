package com.example.a5;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private AnimatorModel model;
	private Timer t;
	private int fps = 40;
	private SeekBar slider;
	private MyView myView;
	private boolean fileExplore = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		slider = (SeekBar) findViewById(R.id.slider);
		model = new AnimatorModel();

		RelativeLayout v = (RelativeLayout) findViewById(R.id.linearLayout);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, R.id.back);
		params.addRule(RelativeLayout.ABOVE, R.id.slider);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		// p.addRule(RelativeLayout.BELOW, R.id.below_id);
		//
		// android1:layout_width="wrap_content"
		// android1:layout_height="wrap_content"
		// android1:layout_above="@+id/slider"
		// android1:layout_alignParentLeft="true"
		// android1:layout_below="@+id/load"
		//
		myView = new MyView(this);
		v.addView(myView, params);

		// this will run when timer elapses
		TimerTask myTimerTask = new TimerTask() {
			@Override
			public void run() {
				if (model.getState() == AnimatorModel.State.playing) {
					if (model.getFrame() == model.getTotalFrames()) {
						model.setState(AnimatorModel.State.draw);
					} else {
						model.increaseFrames(false);
						slider.setProgress(model.getFrame());
						myView.postInvalidate();
					}
				}
			}
		};
		t = new Timer();
		t.scheduleAtFixedRate(myTimerTask, 0, 1000 / fps);

		Button play = (Button) findViewById(R.id.play);
		play.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				if (slider.getProgress() >= model.getTotalFrames()) {
					model.gotoZero();
					slider.setProgress(model.getFrame());
				}
				model.setState(AnimatorModel.State.playing);
			}
		});

		Button stopbutton = (Button) findViewById(R.id.stop);
		stopbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				model.setState(AnimatorModel.State.draw);
			}
		});

		Button fwdbtn = (Button) findViewById(R.id.fwd);
		fwdbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				model.increaseFrames(false);
				slider.setProgress(model.getFrame());
				myView.invalidate();
			}
		});

		Button backbtn = (Button) findViewById(R.id.back);
		backbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				model.decreaseFrames();
				slider.setProgress(model.getFrame());
				myView.invalidate();
			}
		});

		// Button loadButton = (Button) findViewById(R.id.load);
		// loadButton.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// Intent myIntent = new Intent(MainActivity.this,
		// FileExplorer.class);
		// MainActivity.this.startActivity(myIntent);
		// fileExplore = true;
		// }
		// });

		slider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				model.setFrame(progress);
				TextView lbl = (TextView) findViewById(R.id.timeLbl);
				lbl.setText("Current Frame:" + progress);
				myView.invalidate();
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public void onResume() {
		if (fileExplore) {
			SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
			String name = settings.getString("filename", "ohno");
			Log.w("yay", name);
			model.loadAnimation(name);
			slider.setMax(model.getTotalFrames());
			fileExplore = false;
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_load:
			Intent myIntent = new Intent(MainActivity.this, FileExplorer.class);
			MainActivity.this.startActivity(myIntent);
			fileExplore = true;
			return true;

		case R.id.menu_settings:
			Toast.makeText(MainActivity.this, "Save is Selected",
					Toast.LENGTH_SHORT).show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class MyView extends View {
		public MyView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

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
						path.moveTo(first.x, first.y);

						for (int j = 1; j < s.size(); j++) {
							Point to = transformedPoints.get(j);
							path.lineTo(to.x, to.y);
						}
						if (transformedPoints.size() == 1) {
							path.lineTo(first.x, first.y);
						}
					}

					int stroke = s.getStroke();
					Paint paint = new Paint();
					paint.setColor(Color.parseColor(s.getColor()));
					paint.setStrokeWidth(stroke);
					paint.setStyle(Paint.Style.STROKE);
					canvas.drawPath(path, paint);
				}
			}
		}
	}
}