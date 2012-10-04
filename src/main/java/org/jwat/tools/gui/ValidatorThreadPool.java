package org.jwat.tools.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jwat.tools.gui.library.ArchiveFileBase;
import org.jwat.tools.tasks.test.TestFile;

public class ValidatorThreadPool {

	/** ThreadPool executor. */
	private ExecutorService executor; 

	// TODO
	TestFile testFile = new TestFile();

	public ValidatorThreadPool() {
		executor = new ThreadPoolExecutor(4, 4, 20L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public void exit() {
		executor.shutdown();
	}

	public void add(ArchiveFileBase archiveFile) {
		Future<?> future = executor.submit(new TestRunnable(archiveFile));
	}

	class TestRunnable implements Runnable {
		ArchiveFileBase archiveFile;
		TestRunnable(ArchiveFileBase archiveFile) {
			this.archiveFile = archiveFile;
		}
		@Override
		public void run() {
			archiveFile.validate();
		}
	}

}
