package org.jwat.tools.gui.explorer;

import org.jwat.common.ContentType;
import org.jwat.common.Diagnostics;
import org.jwat.warc.WarcDate;

public class ArchiveEntry {

	public int index;

	public boolean bCompressed;

	public long offset;

	public String offsetStr;

	public String uri;

	public WarcDate date;

	public long contentLength;

	public ContentType contentType;

	public String name;

	public Diagnostics diagnostics;

	public String toString() {
        return name;
    }

}
