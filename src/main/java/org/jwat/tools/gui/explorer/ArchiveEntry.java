package org.jwat.tools.gui.explorer;

import java.util.Date;

public class ArchiveEntry {

	public int index;

	public boolean bCompressed;

	public long offset;

	public String uri;

	public Date date;

	public long length;

	public String name;

    public String toString() {
        return name;
    }

}
