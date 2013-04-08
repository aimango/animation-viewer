package com.elisa.a5.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.elisa.a5.R;

public class FileExplorerActivity extends ListActivity {
	private List<String> xmlFiles = null;
	private List<String> paths = null;
	private TextView myPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00B386")));
		myPath = (TextView) findViewById(R.id.path);
		getDir("/mnt/sdcard");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void getDir(String dirPath) {
		myPath.setText("Location: " + dirPath);
		xmlFiles = new ArrayList<String>();
		paths = new ArrayList<String>();

		File dir = new File(dirPath);
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (!file.isDirectory()) { // only look at files
				int j = file.getName().lastIndexOf('.');
				if (j > 0) {
					String extension = file.getName().substring(j + 1);
					if (extension.equals("xml")) {
						paths.add(file.getPath());
						xmlFiles.add(file.getName());
					}
				}
			}
		}
		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.row, xmlFiles);
		setListAdapter(fileList);
	}

	@Override
	// When a list item is clicked, get the filename and save it in sharedprefs
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(paths.get(position));
		String filename = file.getName();

		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("filename", filename);
		editor.putString("fileexplore", "yes");
		editor.commit();
		finish();
	}
}