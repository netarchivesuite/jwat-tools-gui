/*
 * Created on 17/10/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package org.jwat.tools.gui.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class JTextFieldValidFile extends JTextField {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -762826282823463884L;

	protected ExistsThreadRunner existsThreadRunner;

	protected Thread existsThread;

	protected Color defaultBackground = getBackground();

	public JTextFieldValidFile(int cols) {
		super( cols );
		setFocusTraversalKeysEnabled( false );
		existsThreadRunner = new ExistsThreadRunner();
		existsThread = new Thread( existsThreadRunner );
		existsThread.start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		existsThreadRunner.exit();
		super.finalize();
		existsThreadRunner = null;
	}

	@Override
	protected void processKeyEvent(KeyEvent e) {
		if ( !e.isConsumed() ) {
			int id = e.getID();
			if ( id == KeyEvent.KEY_PRESSED ) {
				keyPressed(e);
			}
			else if ( id == KeyEvent.KEY_RELEASED ) {
				keyReleased(e);
			}
			else if ( id == KeyEvent.KEY_TYPED ) {
				keyTyped(e);
			}
	}
		super.processKeyEvent( e );
	}

	private void keyPressed(KeyEvent e) {
	}

	private void keyReleased(KeyEvent e) {
		if ( (e.getModifiers() & (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK | KeyEvent.ALT_DOWN_MASK | KeyEvent.ALT_GRAPH_DOWN_MASK | KeyEvent.META_DOWN_MASK )) == 0  ) {
			int c = e.getKeyCode();
			if ( c == KeyEvent.VK_TAB ) {
				// debug
				//System.out.println( "tab" );

				String pathname = getText();

				File parent = new File( pathname );
				if ( !parent.exists() ) {
					String partial = parent.getName();
					parent = parent.getParentFile();

					if ( parent.exists() ) {
						String[] listed = parent.list();
						List<String> files = new ArrayList<String>();
						int idx = 0;
						while ( idx < listed.length ) {
							if ( listed[ idx ].startsWith( partial ) ) {
								files.add( listed[ idx ] );
							}
							++idx;
						}

						setCaretPosition( pathname.length() );

						String matched = "";
						idx = 0;
						if ( files.size() > 0 ) {
							matched = files.get( idx++ );
							while ( idx < files.size() ) {
								matched = matchStarts( matched, files.get( idx++ ) );
							}
						}

						if ( matched.length() > partial.length() ) {
							try {
								matched = matched.substring( partial.length() );
								getDocument().insertString( pathname.length(), matched, null );
								setCaretPosition( pathname.length() + matched.length() );
							}
							catch (BadLocationException e1) {
								e1.printStackTrace();
							}
						}

						// debug
						//System.out.println( matched );
					}
				}

				e.consume();
			}
		}
	}

	private void keyTyped(KeyEvent e) {
	}

	private String matchStarts(String current, String compare) {
		int idx = 0;
		while ( idx < current.length() && idx < compare.length() && current.charAt( idx ) == compare.charAt( idx ) ) {
			++idx;
		}
		return current.substring( 0, idx );
	}

	protected Document createDefaultModel() {
		return new DocumentImpl( this );
	}

	class DocumentImpl extends PlainDocument {

		/**
		 * UID.
		 */
		private static final long serialVersionUID = 1L;

		private JTextField tf;

		public DocumentImpl(JTextField tf) {
			this.tf = tf;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			super.insertString(offs, str, a);
			/*
			if (str == null) {
				return;
			}
			char[] upper = str.toCharArray();
			for (int i = 0; i < upper.length; i++) {
				upper[i] = Character.toUpperCase(upper[i]);
			}
			*/

			setBackground( Color.RED );

			String text = tf.getText();
			queue_exists( text );
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			super.remove(offs, len);

			if ( tf.getText().length() == 0 ) {
				setBackground( defaultBackground );
			}
			else {
				setBackground( Color.RED );

				String text = tf.getText();
				queue_exists( text );
			}
		}

		private void queue_exists(String text) {
			if ( text != null && text.length() > 0 ) {
				File file = new File( text );
				/*
				if ( file.exists() ) {
					setBackground( Color.GREEN );
				}
				*/
				if ( "\\".compareTo( text ) == 0 ) {
					file = null;
				}
				else if ( text.startsWith( "\\\\" ) ) {
					//  && text.charAt( text.length() - 1 ) == '\\'
					if ( text.indexOf( "\\", 2 ) == -1 ) {
						file = null;
					}
					else if ( text.indexOf( "\\", 2 ) == text.lastIndexOf( "\\" ) ) {
						file = null;
					}
				}
				if ( file != null ) {
					existsThreadRunner.exists( file );
				}
			}
		}

	}

	class ExistsThreadRunner implements Runnable {

		boolean bExit = false;

		File existsFile = null;

		public void exit() {
			synchronized ( this ) {
				bExit = true;
				this.notify();
			}
		}

		public void exists(File existsFile) {
			// debug
			//System.out.println( "Exists: " + existsFile.getPath() );
			synchronized ( this ) {
				this.existsFile = existsFile;
				this.notify();
			}
		}

		@Override
		public void run() {
			File tmpFile;
			while ( !bExit ) {
				try {
					tmpFile = null;
					synchronized ( this ) {
						this.wait( 1000 );
						tmpFile = existsFile;
					}
					if ( !bExit && tmpFile != null ) {
						if ( tmpFile.exists() ) {
							setBackground( Color.GREEN );
						}
						// debug
						//System.out.println( "Checked: " + tmpFile.getPath() );
					}
					existsFile = null;
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
