package com.example.a5;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.a5.MainActivity;

public class FileExplorer extends ListActivity {
	private List<String> item = null;
	private List<String> path = null;
	private String root = "/mnt/sdcard";
	private TextView myPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer);
		myPath = (TextView) findViewById(R.id.path);
		getDir(root);
	}

	private void getDir(String dirPath) {
		myPath.setText("Location: " + dirPath);
		item = new ArrayList<String>();
		path = new ArrayList<String>();

		File f = new File(dirPath);
		File[] files = f.listFiles();

//		if (!dirPath.equals(root)) {
//			item.add(root);
//			path.add(root);
//			item.add("../");
//			path.add(f.getParent());
//		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			
			if (!file.isDirectory()) {
				int j = file.getName().lastIndexOf('.');
				if (j > 0) {
					String extension = file.getName().substring(j + 1);
					// Log.w("supp",file.getName().substring(j+1));
					if (extension.equals("xml")){
						path.add(file.getPath());
						item.add(file.getName());
					}
				}
			}
		}

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.row, item);
		setListAdapter(fileList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		File file = new File(path.get(position));
		String s = file.getName();

		SharedPreferences settings = getSharedPreferences("MyPrefsFile", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("filename", s);

		// Commit the edits!
		editor.commit();

		//File file = new File(path.get(position));
		// if (!file.isDirectory()) {
		// new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher)
		//
		// .setTitle("[" + file.getName() + "]")
		//
		// .setPositiveButton("OK",
		//
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// }
		// }).show();
		// }
		finish();
	}
}