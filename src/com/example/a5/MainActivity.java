package com.example.a5;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.a5.AnimatorModel.State;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private AnimatorModel model;
	private Timer t;
	private int fps = 40;
	private Canvas canvas;

	//int temp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		canvas = new Canvas();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		model = new AnimatorModel();

		final RelativeLayout v = (RelativeLayout) findViewById(R.id.linearLayout);
		final MyView myView = new MyView(this);
		v.addView(myView);
		
		
        final Button button = (Button) findViewById(R.id.play);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	model.setState(AnimatorModel.State.playing);
            	myView.invalidate();
            	myView.draw(canvas);
            }
        });
        
        final Button stopbutton = (Button) findViewById(R.id.stop);
        stopbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	model.setState(AnimatorModel.State.draw);
            }
        });
       
        final Button fwdbtn = (Button) findViewById(R.id.fwd);
        fwdbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	model.increaseFrames(false);
            	myView.invalidate();
            	myView.draw(canvas);
            }
        });
        
        final Button backbtn = (Button) findViewById(R.id.back);
        backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	model.decreaseFrames();
            	myView.invalidate(); 
            	myView.draw(canvas);
            }
        });
        

		// this will run when timer elapses
		TimerTask myTimerTask = new TimerTask() {

			@Override
			public void run() {

				if (model.getState() == AnimatorModel.State.playing) {
					
					model.increaseFrames(false);
					
					myView.draw(canvas);
					Log.w("Timer is at", String.valueOf(model.getFrame()));
					//temp++;
				}
 
			}

		};

		// new timer
		t = new Timer();
		
		// schedule timer
		t.scheduleAtFixedRate(myTimerTask, 0, 1000/fps);



	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class MyView extends View {
		public MyView(Context context) {
			super(context);
			model.loadAnimation();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			// ZEE SEGMENTS
			ArrayList<Segment> segments = model.getSegments();
			if (segments.size() > 0) {
				int i = 0;
				for (Segment s : segments) {
					int size = s.size();

					int currFrame = model.getFrame();
					//Log.w("curr frame is ",String.valueOf(currFrame));
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

					i++;
				}
			}
		}
	}
}