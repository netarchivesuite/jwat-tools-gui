package org.jwat.tools.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jwat.arc.ArcRecordBase;
import org.jwat.common.UriProfile;
import org.jwat.gzip.GzipEntry;
import org.jwat.tools.core.ArchiveParser;
import org.jwat.tools.core.ArchiveParserCallback;
import org.jwat.tools.gui.explorer.ArchiveEntry;
import org.jwat.warc.WarcRecord;

public class Indexer implements ArchiveParserCallback {

	protected List<ArchiveEntry> entries = new ArrayList<ArchiveEntry>();

	protected int index;

	public Indexer() {
	}

	public List<ArchiveEntry> index(File file) {
		index = 0;
		ArchiveParser archiveParser = new ArchiveParser();
		archiveParser.uriProfile = UriProfile.RFC3986_ABS_16BIT_LAX;
		archiveParser.bBlockDigestEnabled = false;
		archiveParser.bPayloadDigestEnabled = false;
		//long consumed = archiveParser.parse(file, this);
		archiveParser.parse(file, this);
		return entries;
	}

	@Override
	public void apcFileId(File file, int fileId) {
	}

	@Override
	public void apcUpdateConsumed(long consumed) {
	}

	@Override
	public void apcGzipEntryStart(GzipEntry gzipEntry, long startOffset) {
	}

	@Override
	public void apcArcRecordStart(ArcRecordBase arcRecord, long startOffset,
			boolean compressed) throws IOException {
		ArchiveEntry entry = new ArchiveEntry();
		entry.index = index++;
		entry.bCompressed = compressed;
		entry.offset = startOffset;
		entry.uri = arcRecord.header.urlStr;
		entry.date = arcRecord.header.archiveDate;
		entry.length = arcRecord.header.archiveLength;
		entries.add(entry);
	}

	@Override
	public void apcWarcRecordStart(WarcRecord warcRecord, long startOffset,
			boolean compressed) throws IOException {
		ArchiveEntry entry = new ArchiveEntry();
		entry.index = index++;
		entry.bCompressed = compressed;
		entry.offset = startOffset;
		entry.uri = warcRecord.header.warcTargetUriStr;
		entry.date = warcRecord.header.warcDate;
		entry.length = warcRecord.header.contentLength;
		entries.add(entry);
	}

	@Override
	public void apcRuntimeError(Throwable t, long offset, long consumed) {
	}

	@Override
	public void apcDone() {
	}

}
