package com.elisa.a5.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.elisa.a5.R;

public class FileExplorer extends ListActivity {
	private List<String> xmlFiles = null;
	private List<String> paths = null;
	private TextView myPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer);
		myPath = (TextView) findViewById(R.id.path);
		getDir("/mnt/sdcard");
	}

	private void getDir(String dirPath) {
		myPath.setText("Location: " + dirPath);
		xmlFiles = new ArrayList<String>();
		paths = new ArrayList<String>();

		File f = new File(dirPath);
		File[] files = f.listFiles();

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