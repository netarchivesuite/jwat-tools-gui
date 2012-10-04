package org.jwat.tools.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.jwat.tools.gui.library.ArchiveLibraryFrame;


/*
 * Created on 21/11/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class Desktop extends JFrame implements WindowListener, ActionListener {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -8318458562722112425L;

	/** JDesktopPane, main window. */
	public static JDesktopPane desktop;

	private JMenuBar menuBar = new JMenuBar();

	public static ArchiveLibraryFrame archiveLibraryFrame;

	public static ScannerThread scannerThread;

	public static ValidatorThreadPool validatorThread;

	public Desktop() {
		super( "Java Web Archive eXplorer" );

		try {
			UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" );
			//UIManager.setLookAndFeel( "javax.swing.plaf.basic.BasicLookAndFeel" );
		}
		catch (Exception e) {
			System.err.println("Could not initialize java.awt Metal lnf");
		}

		JFrame.setDefaultLookAndFeelDecorated( true );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		addWindowListener( this );

		// Menus

		createMenus();
		setJMenuBar( menuBar );

		//GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//GraphicsDevice gd = ge.getDefaultScreenDevice();
		//GraphicsConfiguration gc = gd.getDefaultConfiguration();
		//Rectangle r = gc.getBounds();
		//Insets i = Toolkit.getDefaultToolkit().getScreenInsets( gc );

		setSize( 640, 480 );
		setExtendedState( java.awt.Frame.MAXIMIZED_BOTH );

		desktop = new JDesktopPane();
		//setJMenuBar( jMenuBar );
		setContentPane( desktop );
		desktop.setDragMode( JDesktopPane.OUTLINE_DRAG_MODE );

		setVisible( true );

		scannerThread = new ScannerThread();

		validatorThread = new ValidatorThreadPool();

		archiveLibraryFrame = new ArchiveLibraryFrame();
		desktop.add( archiveLibraryFrame );
		archiveLibraryFrame.setVisible( true );
	}

	private void createMenus() {
		menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu( "Files" );
		menu.setMnemonic( KeyEvent.VK_K );
		menuBar.add( menu );

		//menu.addSeparator();
		menuItem = new JMenuItem( "Quit" );
		menuItem.setMnemonic( KeyEvent.VK_Q );
		menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q, ActionEvent.ALT_MASK ) );
		menuItem.setActionCommand( "desktop.quit" );
		menuItem.addActionListener( this );
		menu.add(menuItem);

		menu = new JMenu( "View" );
		menu.setMnemonic( KeyEvent.VK_V );
		menuBar.add( menu );

		menuItem = new JMenuItem( "Hashing Queue" );
		menuItem.setMnemonic( KeyEvent.VK_H );
		menuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H, ActionEvent.ALT_MASK ) );
		menuItem.setActionCommand( "hashing.queue" );
		menuItem.addActionListener( this );
		menu.add(menuItem);

		menu = new JMenu( "Help" );
		menu.setMnemonic( KeyEvent.VK_H );
		menuBar.add( menu );
	}

	public void windowOpened(WindowEvent r) {
	}

	public void windowClosing(WindowEvent r) {
		desktopClose();
	}

	public void windowClosed(WindowEvent r) {
	}

	public void windowIconified(WindowEvent r) {
	}

	public void windowDeiconified(WindowEvent r) {
	}

	public void windowActivated(WindowEvent r) {
	}

	public void windowDeactivated(WindowEvent r) {
	}

	/**
	 * <code>ActionPerformed</code> event handler.
	 * @param e <code>ActionEvent</code>
	 */
	public void actionPerformed(ActionEvent e) {
		// debug
		//System.out.println( e.getActionCommand() );
		String cmd = e.getActionCommand();
		if ( cmd == null || cmd.length() == 0) {
			return;
		}
		if ( "desktop.quit".equals( cmd ) ) {
			desktopClose();
		}
	}

	private void desktopClose() {
		setVisible( false );
		dispose();

		validatorThread.exit();
	}

	public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Couldn't use system look and feel.");
        }

        //Security.insertProviderAt( new BouncyCastleProvider(), 2 );
		//Security.addProvider( new BouncyCastleProvider() );

		new Desktop();
	}

}
