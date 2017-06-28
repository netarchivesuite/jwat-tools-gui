/*
 * Created on 24/11/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package org.jwat.tools.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jwat.archive.FileIdent;
import org.jwat.tools.gui.library.ArchiveFileBase;
import org.jwat.tools.gui.library.ArchiveFileImpl;

import com.antiaction.multithreading.concurrent.Multex;
import com.antiaction.multithreading.concurrent.Mutex;


public class ScannerThread implements Runnable {

	/** Shutdown boolean. */
	private boolean exit = false;

	private Mutex lock = new Mutex( 0 );
	private Multex queued = new Multex( 0 );
	private List<File> events = new ArrayList<File>();

	public ScannerThread() {
		Thread t = new Thread( this );
		t.start();
	}

	public void exit() {
		exit = true;
	}

	public void add(File archiveFile) {
		lock.obtainSemaphore();
		events.add( archiveFile );
		lock.releaseSemaphore();
		queued.releaseSemaphore();
	}

	public void run() {
		File archiveFile;
		while ( !exit ) {
			queued.obtainSemaphore();

			// Retrieve job.
			lock.obtainSemaphore();
			archiveFile = events.remove( 0 );
			lock.releaseSemaphore();

			// Process job.
			if ( archiveFile != null ) {
				addFile( archiveFile );
			}
		}
	}

	private void addFile(File file) {
		ArchiveFileBase archiveFile;
		FileIdent fileIdent;
		if ( file.isDirectory() ) {
			File[] files = file.listFiles();
			for ( int i=0; i<files.length; ++i ) {
				if ( files[ i ].isDirectory() ) {
					addFile( files[ i ] );
				}
				else if ( files[ i ].isFile() ) {
					fileIdent = FileIdent.ident(files[ i ]);
					if (file.length() > 0) {
						// debug
						//System.out.println(fileIdent.filenameId + " " + fileIdent.streamId + " " + srcFile.getName());
						/*
						if (fileIdent.filenameId != fileIdent.streamId) {
						}
						*/
						switch (fileIdent.streamId) {
						case FileIdent.FILEID_GZIP:
						case FileIdent.FILEID_ARC:
						case FileIdent.FILEID_ARC_GZ:
						case FileIdent.FILEID_WARC:
						case FileIdent.FILEID_WARC_GZ:
							archiveFile = new ArchiveFileImpl();
							archiveFile.file = files[ i ];
							archiveFile.path = files[ i ].getParent();
							archiveFile.filename = files[ i ].getName();
							archiveFile.fileSize = files[ i ].length();
							Desktop.archiveLibraryFrame.addFile( archiveFile );
							Desktop.validatorThread.add( archiveFile );
							break;
						default:
							break;
						}
					} else {
						switch (fileIdent.filenameId) {
						case FileIdent.FILEID_GZIP:
						case FileIdent.FILEID_ARC:
						case FileIdent.FILEID_ARC_GZ:
						case FileIdent.FILEID_WARC:
						case FileIdent.FILEID_WARC_GZ:
							break;
						default:
							break;
						}
					}
				}
			}
		}
		else if ( file.isFile() ) {
			fileIdent = FileIdent.ident(file);
			if (file.length() > 0) {
				// debug
				//System.out.println(fileIdent.filenameId + " " + fileIdent.streamId + " " + srcFile.getName());
				/*
				if (fileIdent.filenameId != fileIdent.streamId) {
				}
				*/
				switch (fileIdent.streamId) {
				case FileIdent.FILEID_GZIP:
				case FileIdent.FILEID_ARC:
				case FileIdent.FILEID_ARC_GZ:
				case FileIdent.FILEID_WARC:
				case FileIdent.FILEID_WARC_GZ:
					archiveFile = new ArchiveFileImpl();
					archiveFile.file = file;
					archiveFile.path = file.getParent();
					archiveFile.filename = file.getName();
					archiveFile.fileSize = file.length();
					Desktop.archiveLibraryFrame.addFile( archiveFile );
					Desktop.validatorThread.add( archiveFile );
					break;
				default:
					break;
				}
			} else {
				switch (fileIdent.filenameId) {
				case FileIdent.FILEID_GZIP:
				case FileIdent.FILEID_ARC:
				case FileIdent.FILEID_ARC_GZ:
				case FileIdent.FILEID_WARC:
				case FileIdent.FILEID_WARC_GZ:
					break;
				default:
					break;
				}
			}
		}
	}

}
