package org.jwat.tools.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jwat.arc.ArcHeader;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcRecordBase;
import org.jwat.archive.ArchiveParser;
import org.jwat.archive.ArchiveParserCallback;
import org.jwat.common.ContentType;
import org.jwat.common.HttpHeader;
import org.jwat.common.UriProfile;
import org.jwat.gzip.GzipEntry;
import org.jwat.gzip.GzipReader;
import org.jwat.tools.gui.explorer.ArchiveEntry;
import org.jwat.warc.WarcHeader;
import org.jwat.warc.WarcReader;
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
	public void apcArcRecordStart(ArcRecordBase arcRecord, long startOffset, boolean compressed) throws IOException {
		ArcHeader header = arcRecord.header;
		ArchiveEntry entry = new ArchiveEntry();
		entry.index = index++;
		entry.bCompressed = compressed;
		entry.offset = startOffset;
		entry.offsetStr = Long.toHexString(startOffset) + " / " + Long.toString(startOffset);
		entry.uri = arcRecord.header.urlStr;
		entry.date = arcRecord.header.archiveDate;
		entry.contentLength = arcRecord.header.archiveLength;
		entry.contentType = header.contentType;
		// TODO
        if (entry.contentType != null
                && header.contentType.contentType.equals("application")
                && header.contentType.mediaType.equals("http")) {
            String value = header.contentType.getParameter("msgtype");
            HttpHeader httpHeader = arcRecord.getHttpHeader();
            if ("response".equalsIgnoreCase(value)) {
            	if (httpHeader != null && httpHeader.contentType != null) {
            		ContentType contentType = ContentType.parseContentType(httpHeader.contentType);
            		if (contentType != null) {
                		entry.contentType = contentType;
            		}
            	}
            }
        }
        arcRecord.close();
		entry.diagnostics = arcRecord.diagnostics;
		entries.add(entry);
	}

	@Override
	public void apcWarcRecordStart(WarcRecord warcRecord, long startOffset,
			boolean compressed) throws IOException {
		WarcHeader warcHeader = warcRecord.header;
		ArchiveEntry entry = new ArchiveEntry();
		entry.index = index++;
		entry.bCompressed = compressed;
		entry.offset = startOffset;
		entry.offsetStr = Long.toHexString(startOffset) + " / " + Long.toString(startOffset);
		entry.uri = warcRecord.header.warcTargetUriStr;
		entry.date = warcRecord.header.warcDate;
		entry.contentLength = warcRecord.header.contentLength;
		entry.contentType = warcRecord.header.contentType;
        if (entry.contentType != null
                && warcHeader.contentType.contentType.equals("application")
                && warcHeader.contentType.mediaType.equals("http")) {
            String value = warcHeader.contentType.getParameter("msgtype");
            HttpHeader httpHeader = warcRecord.getHttpHeader();
            if ("response".equalsIgnoreCase(value)) {
            	if (httpHeader != null && httpHeader.contentType != null) {
            		ContentType contentType = ContentType.parseContentType(httpHeader.contentType);
            		if (contentType != null) {
                		entry.contentType = contentType;
            		}
            	}
            }
        }
        warcRecord.close();
		entry.diagnostics = warcRecord.diagnostics;
		entries.add(entry);
	}

	@Override
	public void apcRuntimeError(Throwable t, long offset, long consumed) {
	}

	@Override
	public void apcDone(GzipReader gzipReader, ArcReader arcReader, WarcReader warcReader) {
	}

}
