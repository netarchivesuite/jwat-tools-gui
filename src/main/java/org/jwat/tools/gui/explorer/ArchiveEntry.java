package org.jwat.tools.gui.explorer;

import java.util.Date;

import org.jwat.common.ContentType;
import org.jwat.common.Diagnosis;
import org.jwat.common.Diagnostics;

public class ArchiveEntry {

	public int index;

	public boolean bCompressed;

	public long offset;

	public String uri;

	public Date date;

	public long contentLength;

	public ContentType contentType;

	public String name;

	public Diagnostics<Diagnosis> diagnostics;

	public String toString() {
        return name;
    }

}
