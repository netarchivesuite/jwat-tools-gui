/*
 * Created on 23/11/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package org.jwat.tools.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.jwat.tools.gui.library.ArchiveFileBase;


public class ValidatorThread implements Runnable {

	/** Shutdown boolean. */
	private boolean exit = false;

	private Semaphore lock = new Semaphore( 1 );
	private Semaphore queued = new Semaphore( 0 );
	private List<ArchiveFileBase> events = new ArrayList<ArchiveFileBase>();

	public ValidatorThread() {
		Thread t = new Thread( this );
		t.start();
	}

	public void exit() {
		exit = true;
	}

	public void add(ArchiveFileBase archiveFile) {
		while (archiveFile != null) {
			try {
				lock.acquire();
				events.add( archiveFile );
				archiveFile = null;
				lock.release();
				queued.release();
			}
			catch (InterruptedException e) {
			}
		}
	}

	public void run() {
		ArchiveFileBase archiveFile;
		while ( !exit ) {
			try {
				// Wait for job.
				queued.acquire();
				archiveFile = null;
				// Retrieve job.
				while (archiveFile == null) {
					try {
						lock.acquire();
						archiveFile = events.remove( 0 );
						lock.release();
					}
					catch (InterruptedException e) {
					}
				}
				// Process job.
				if ( archiveFile != null ) {
					archiveFile.validate();
				}
			}
			catch (InterruptedException e) {
			}
		}
	}

}
