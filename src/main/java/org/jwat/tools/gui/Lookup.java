package org.jwat.tools.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;
import org.jwat.common.ByteCountingPushBackInputStream;
import org.jwat.common.Payload;
import org.jwat.common.PayloadWithHeaderAbstract;
import org.jwat.common.RandomAccessFileInputStream;
import org.jwat.common.UriProfile;
import org.jwat.gzip.GzipEntry;
import org.jwat.gzip.GzipReader;
import org.jwat.tools.core.FileIdent;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

public class Lookup {

	public UriProfile uriProfile = UriProfile.RFC3986_ABS_16BIT_LAX;

	public boolean bBlockDigestEnabled = false;

	public boolean bPayloadDigestEnabled = false;

	protected File file;

	protected RandomAccessFile raf = null;
	protected RandomAccessFileInputStream rafin;
	protected ByteCountingPushBackInputStream pbin = null;

	public GzipReader gzipReader = null;
	public ArcReader arcReader = null;
	public WarcReader warcReader = null;

	protected GzipEntry gzipEntry = null;
	protected ArcRecordBase arcRecord = null;
	protected WarcRecord warcRecord = null;

	public int fileId;

	public byte[] header;

	public Payload payload;

	public byte[] payloadHeader;

	public InputStream payload_inputstream;

	public int payload_length;

	public Lookup() {
	}

	public static Lookup getInstance(File file) throws IOException {
		Lookup lookup = new Lookup();
		lookup.file = file;
		lookup.fileId = FileIdent.FILEID_UNKNOWN;
		lookup.raf = new RandomAccessFile( file, "r" );
		lookup.rafin = new RandomAccessFileInputStream( lookup.raf ) {
			@Override
			public void close() {
			}
		};
		lookup.raf.seek(0);
		ByteCountingPushBackInputStream pbin = new ByteCountingPushBackInputStream(lookup.rafin, 16);
		ByteCountingPushBackInputStream in = null;
		GzipEntry gzipEntry = null;
		if (GzipReader.isGzipped(pbin)) {
			lookup.gzipReader = new GzipReader( pbin );
			if ( (gzipEntry = lookup.gzipReader.getNextEntry()) != null ) {
				in = new ByteCountingPushBackInputStream( new BufferedInputStream( gzipEntry.getInputStream(), 128 ), 16 );
				lookup.fileId = FileIdent.FILEID_GZIP;
			}
		}
		else {
			in = pbin;
		}
		if (ArcReaderFactory.isArcFile(in)) {
			if (lookup.fileId == FileIdent.FILEID_GZIP) {
				lookup.fileId = FileIdent.FILEID_ARC_GZ;
			} else {
				lookup.fileId = FileIdent.FILEID_ARC;
			}
		}
		else if ( WarcReaderFactory.isWarcFile(in) ) {
			if (lookup.fileId == FileIdent.FILEID_GZIP) {
				lookup.fileId = FileIdent.FILEID_WARC_GZ;
			} else {
				lookup.fileId = FileIdent.FILEID_WARC;
			}
		}
		else {
		}
		if (gzipEntry != null) {
			gzipEntry.close();
			gzipEntry = null;
		}
		if (pbin != null) {
			pbin.close();
			pbin = null;
		}
		return lookup;
	}

	public void lookup_entry(long offset) throws IOException {
		close_entry();
		raf.seek(offset);
		if (raf.getFilePointer() != offset) {
			throw new IllegalArgumentException("offset is invalid");
		}
		pbin = new ByteCountingPushBackInputStream(rafin, 16);
		ByteCountingPushBackInputStream in = null;
		switch (fileId) {
		case FileIdent.FILEID_GZIP:
		case FileIdent.FILEID_ARC_GZ:
		case FileIdent.FILEID_WARC_GZ:
			gzipReader = new GzipReader( pbin );
			if ( (gzipEntry = gzipReader.getNextEntry()) != null ) {
				in = new ByteCountingPushBackInputStream( new BufferedInputStream( gzipEntry.getInputStream(), 8192 ), 16 );
			}
			break;
		default:
			in = pbin;
			break;
		}
		header = new byte[0];
		payload = null;
		payloadHeader = new byte[0];
		payload_length = 0;
		switch (fileId) {
		case FileIdent.FILEID_ARC_GZ:
		case FileIdent.FILEID_ARC:
			arcReader = ArcReaderFactory.getReaderUncompressed();
			arcReader.setUriProfile(uriProfile);
			arcReader.setBlockDigestEnabled( bBlockDigestEnabled );
			arcReader.setPayloadDigestEnabled( bPayloadDigestEnabled );
			arcRecord = arcReader.getNextRecordFrom( in, offset );
			if (arcRecord != null) {
				//arcRecord.header.
				payload = arcRecord.getPayload();
				if (payload != null) {
					PayloadWithHeaderAbstract payloadHeaderObject = payload.getPayloadHeaderWrapped();
					if (payloadHeaderObject != null) {
						payloadHeader = payloadHeaderObject.getHeader();
					}
					payload_inputstream = payload.getInputStream();
					payload_length = (int)payload.getRemaining();
				}
			}
			break;
		case FileIdent.FILEID_WARC_GZ:
		case FileIdent.FILEID_WARC:
			warcReader = WarcReaderFactory.getReaderUncompressed();
			warcReader.setWarcTargerUriProfile(uriProfile);
			warcReader.setBlockDigestEnabled( bBlockDigestEnabled );
			warcReader.setPayloadDigestEnabled( bPayloadDigestEnabled );
			warcRecord = warcReader.getNextRecordFrom( in, offset );
			if (warcRecord != null) {
				header = warcRecord.header.headerBytes;
				payload = warcRecord.getPayload();
				if (payload != null) {
					PayloadWithHeaderAbstract payloadHeaderObject = payload.getPayloadHeaderWrapped();
					if (payloadHeaderObject != null) {
						payloadHeader = payloadHeaderObject.getHeader();
					}
					payload_inputstream = payload.getInputStream();
					payload_length = (int)payload.getRemaining();
				}
			}
			break;
		default:
			break;
		}
	}

	// TODO try catch, remove throws.
	public void close_entry() throws IOException {
		if (payload_inputstream != null) {
			payload_inputstream.close();
			payload_inputstream = null;
		}
		if (arcRecord != null) {
			arcRecord.close();
			arcRecord = null;
		}
		if (warcRecord != null) {
			warcRecord.close();
			warcRecord = null;
		}
		if (gzipEntry != null) {
			gzipEntry.close();
			gzipEntry = null;
		}
		if ( arcReader != null ) {
			arcReader.close();
			arcReader = null;
		}
		if ( warcReader != null ) {
			warcReader.close();
			warcReader = null;
		}
		if (gzipReader != null) {
			try {
				gzipReader.close();
				gzipReader = null;
			}
			catch (IOException e) {
			}
		}
		if (pbin != null) {
			try {
				pbin.close();
				pbin = null;
			}
			catch (IOException e) {
			}
		}
	}

	public void close() throws IOException {
		close_entry();
		if (raf != null) {
			try {
				raf.close();
			}
			catch (IOException e) {
			}
		}
	}

}
