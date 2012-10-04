/*
 * Created on 21/11/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package org.jwat.tools.gui.library;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JProgressBar;

import org.jwat.common.UriProfile;
import org.jwat.tools.core.ValidatorPlugin;
import org.jwat.tools.gui.explorer.ArchiveEntry;
import org.jwat.tools.tasks.test.TestFile2;
import org.jwat.tools.tasks.test.TestFileResult;
import org.jwat.tools.tasks.test.TestFileUpdateCallback;

public abstract class ArchiveFileBase implements TestFileUpdateCallback {

	/*
	 * Static.
	 */

	public File file;

	public String path;

	public String filename;

	public long fileSize;

	double ratio = 0.0;

	/*
	 * Dynamic.
	 */

	public JProgressBar progressBar = new JProgressBar();

	public long consumed = 0;

	public int records = 0;

	public int errors = 0;

	public int warnings = 0;

	public int exceptions = 0;

	/*
	 * Listeners.
	 */

	protected Set<ArchiveFileChangeListener> listeners = new HashSet<ArchiveFileChangeListener>();

	public void addListener(ArchiveFileChangeListener listener) {
		synchronized ( listeners ) {
			listeners.add( listener );
		}
	}

	public void removeListener(ArchiveFileChangeListener listener) {
		synchronized ( listeners ) {
			listeners.remove( listener );
		}
	}

	/*
	 * Events.
	 */

	long lastListenerEvent = 0;

	public void listenerEvent() {
		lastListenerEvent = System.currentTimeMillis();
		Iterator<ArchiveFileChangeListener> listenersIter = listeners.iterator();
		ArchiveFileChangeListener listener;
		while ( listenersIter.hasNext() ) {
			listener = listenersIter.next();
			listener.archiveFileChangeEvent( this );
		}
	}

	public void update(TestFileResult result, long consumed) {
		if (System.currentTimeMillis() > lastListenerEvent + 1000) {
			progressBar.setValue( (int)((double)consumed * ratio) );
			records = result.arcRecords + result.warcRecords + result.gzipEntries;
			errors = result.arcErrors + result.warcErrors + result.gzipErrors;
			warnings = result.arcWarnings + result.warcWarnings + result.gzipWarnings;
			exceptions = result.runtimeErrors;
			listenerEvent();
		}
	}

	public void finalUpdate(TestFileResult result, long consumed) {
		progressBar.setValue( (int)((double)consumed * ratio) );
		records = result.arcRecords + result.warcRecords + result.gzipEntries;
		errors = result.arcErrors + result.warcErrors + result.gzipErrors;
		warnings = result.arcWarnings + result.warcWarnings + result.gzipWarnings;
		exceptions = result.runtimeErrors;
		listenerEvent();
	}

	public void validate()  {
		long fileSize = file.length();
		progressBar.setMinimum(0);
		progressBar.setMaximum(Integer.MAX_VALUE);
		progressBar.setValue(0);
		ratio = (double)Integer.MAX_VALUE / (double)fileSize;

		TestFile2 testFile = new TestFile2();
		testFile.bShowErrors = false;
		testFile.uriProfile = UriProfile.RFC3986_ABS_16BIT_LAX;
		testFile.validatorPlugins = new ArrayList<ValidatorPlugin>();
		testFile.callback = this;

		TestFileResult result = testFile.processFile(file);
	}

	public List<ArchiveEntry> index() {
		List<ArchiveEntry> entries = new ArrayList<ArchiveEntry>();


		return entries;
	}

}
