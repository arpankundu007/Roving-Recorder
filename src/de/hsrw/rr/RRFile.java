package de.hsrw.rr;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class RRFile extends File {

	private static final long serialVersionUID = -3905881279424216648L;

	public static final String EXT_3GP = ".3gp";

	public RRFile(File path, String name) {
		super(path, name);
	}

	@Override
	public String toString() {
		String result = getName().toLowerCase();
		result = result.substring(0, result.indexOf(EXT_3GP));
		try {
			Date d = new Date(Long.parseLong(result));
			result = DateFormat.getInstance().format(d);
		} catch (Throwable tr) {
		}
		return result;
	}
}
