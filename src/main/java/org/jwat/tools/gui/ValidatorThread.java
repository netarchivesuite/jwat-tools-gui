/*
 * Created on 23/11/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package org.jwat.tools.gui;

import java.util.ArrayList;
import java.util.List;

import org.jwat.tools.gui.library.ArchiveFileBase;

import com.antiaction.multithreading.concurrent.Multex;
import com.antiaction.multithreading.concurrent.Mutex;


public class ValidatorThread implements Runnable {

	/** Shutdown boolean. */
	private boolean exit = false;

	private Mutex lock = new Mutex( 0 );
	private Multex queued = new Multex( 0 );
	private List<ArchiveFileBase> events = new ArrayList<ArchiveFileBase>();

	public ValidatorThread() {
		Thread t = new Thread( this );
		t.start();
	}

	public void exit() {
		exit = true;
	}

	public void add(ArchiveFileBase archiveFile) {
		lock.obtainSemaphore();
		events.add( archiveFile );
		lock.releaseSemaphore();
		queued.releaseSemaphore();
	}

	public void run() {
		ArchiveFileBase archiveFile;
		while ( !exit ) {
			queued.obtainSemaphore();

			// Retrieve job.
			lock.obtainSemaphore();
			archiveFile = events.remove( 0 );
			lock.releaseSemaphore();

			// Process job.
			if ( archiveFile != null ) {
				archiveFile.validate();
			}
		}
	}

}
