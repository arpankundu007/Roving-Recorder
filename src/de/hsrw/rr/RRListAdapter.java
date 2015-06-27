package de.hsrw.rr;
import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.widget.ArrayAdapter;

public class RRListAdapter extends ArrayAdapter<File> {

	public RRListAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_1);
		// find existing files and add them
		findAndAddFiles();
	}

	private void findAndAddFiles() {
		File dir = RR.getBaseDir();
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (!filename.toLowerCase().endsWith(RRFile.EXT_3GP)) {
					return false;
				}
				File f = new File(dir, filename);
				return f.canRead() && !f.isDirectory();
			}
		});
		if (files != null) {
			for (File f : files) {
				add(new RRFile(f.getParentFile(), f.getName()));
			}
		}
	}
	
	public void refresh(File f){
		add(new RRFile(f.getParentFile(), f.getName()));
		this.notifyDataSetChanged();
	}
}
