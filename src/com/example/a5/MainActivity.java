package com.example.a5;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LinearLayout v = (LinearLayout) findViewById(R.id.linearLayout);
		MyView myView = new MyView(this);
		v.addView(myView);
		
		try {
			File file = new File(Environment.DIRECTORY_DCIM + File.separator
					+ "file.xml");
			if (!file.exists()) {
				Log.w("myApp", "sup");
				file.mkdirs();
			} else {
				Log.w("yay", "yay");
			}
							
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			System.out.println("Root element " + doc.getDocumentElement().getNodeName());
			NodeList nodeLst = doc.getElementsByTagName("employee");
			System.out.println("Information of all employees");
			
			for (int s = 0; s < nodeLst.getLength(); s++) {
			
				Node fstNode = nodeLst.item(s);
			    
			    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			  
			    	Element fstElmnt = (Element) fstNode;
			    	NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("firstname");
					Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
					NodeList fstNm = fstNmElmnt.getChildNodes();
					System.out.println("First Name : "  + ((Node) fstNm.item(0)).getNodeValue());
					NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("lastname");
					Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
					NodeList lstNm = lstNmElmnt.getChildNodes();
					System.out.println("Last Name : " + ((Node) lstNm.item(0)).getNodeValue());
			    }
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class MyView extends View {

		class Pt {
			float x, y;

			Pt(float _x, float _y) {
				x = _x;
				y = _y;
			}
		}

		Pt[] myPath = { new Pt(100, 100), new Pt(200, 200), new Pt(200, 500),
				new Pt(400, 500), new Pt(400, 200) };

		public MyView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.STROKE);
			Path path = new Path();
			path.moveTo(myPath[0].x, myPath[0].y);

			for (int i = 1; i < myPath.length; i++) {
				path.lineTo(myPath[i].x, myPath[i].y);
			}
			canvas.drawPath(path, paint);
		}
	}
}