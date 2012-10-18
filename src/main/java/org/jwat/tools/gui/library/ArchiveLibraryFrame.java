package org.jwat.tools.gui.library;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jwat.tools.gui.Desktop;
import org.jwat.tools.gui.Indexer;
import org.jwat.tools.gui.explorer.ArchiveEntry;
import org.jwat.tools.gui.explorer.ArchiveExplorer;

import com.antiaction.bittorrent.client.ui.JProgressBarTableCellRenderer;
import com.antiaction.bittorrent.client.ui.JTextFieldValidFile;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/*
 * Created on 21/11/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class ArchiveLibraryFrame extends JInternalFrame implements ActionListener, ItemListener, FocusListener, KeyListener, MouseListener {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -6666042833056936976L;

	/** Shutdown boolean. */
	//private boolean exit = false;

	private final EmptyBorder border5 = new EmptyBorder( 5, 5, 5, 5 );

	private final JLabel labelSource = new JLabel( "Source:" );
	private final JTextField tfSource = new JTextFieldValidFile( 24 );
	private final JButton buttonSelectSource = new JButton( "..." );
	private final JButton buttonAdd = new JButton( "Add" );

	/** Hashing table model. */
	private ArchiveLibraryTableModel libraryTableModel;

	/** Content JTable object. */
	private JTable table;

	private File sourceFile = null;

	public ArchiveLibraryFrame() {
		 //resizable, closable, maximizable, iconifiable
		super( "Archive Library", true, true, true, true );

		//Box box = Box.createVerticalBox();
		//box.setBorder( border5 );

		CellConstraints cc;

		// Layout

		JPanel layoutPane = new JPanel();
		layoutPane.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
		layoutPane.setLayout( new BoxLayout(layoutPane, BoxLayout.PAGE_AXIS) );

		// Add	

		tfSource.setEditable( true );
		tfSource.setActionCommand( "source.path" );
		tfSource.addActionListener( this );
		tfSource.addFocusListener( this );
		buttonSelectSource.setEnabled( true );
		buttonSelectSource.setActionCommand( "source.select" );
		buttonSelectSource.addActionListener( this );
		buttonAdd.setEnabled( false );
		buttonAdd.setActionCommand( "source.add" );
		buttonAdd.addActionListener( this );

		Box sourceBox = Box.createVerticalBox();
		Border sourceBorder = new TitledBorder( null, "Source Folder or File", TitledBorder.LEFT, TitledBorder.TOP );
		sourceBox.setBorder( new CompoundBorder( sourceBorder, border5 ) );

		FormLayout sourceLayout = new FormLayout(
				"right:pref, 4dlu, pref, 0dlu, left:pref, 4dlu, pref",			// columns
				"pref" );											// rows

		sourceLayout.setRowGroups( new int[][]{ { 1 } } );

		JPanel sourcePanel = new JPanel( sourceLayout );
		sourcePanel.setLayout( new BoxLayout(sourcePanel, BoxLayout.LINE_AXIS) );

		cc = new CellConstraints();
		sourcePanel.add( labelSource, cc.xy( 1, 1 ) );
		sourcePanel.add( tfSource, cc.xy( 3, 1 ) );
		sourcePanel.add( buttonSelectSource, cc.xy( 5, 1 ) );
		sourcePanel.add( buttonAdd, cc.xy( 7, 1) );

		//sourceBox.setPreferredSize( sourceBox.getPreferredSize() );

		sourceBox.add( sourcePanel );

		tfSource.setMaximumSize( tfSource.getPreferredSize() );

		//box.add( Box.createVerticalStrut( 8 ) );

		// Table

		libraryTableModel = new ArchiveLibraryTableModel();

		table = new JTable( libraryTableModel );
		table.getColumnModel().getColumn( 0 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 1 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 2 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 3 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 4 ).setPreferredWidth( 10 * 10 );
		table.getColumnModel().getColumn( 5 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 6 ).setPreferredWidth( 32 * 10 );
		table.getColumnModel().getColumn( 7 ).setPreferredWidth( 32 * 10 );
		table.addMouseListener( this );
		table.addKeyListener( this );

		table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		table.getColumn( "Progress" ).setCellRenderer( new JProgressBarTableCellRenderer() );

		JScrollPane scrollpane = new JScrollPane( table );

		layoutPane.add( sourceBox, BorderLayout.PAGE_START );
		layoutPane.add( scrollpane, BorderLayout.CENTER );

		// Content

		getContentPane().add( layoutPane );

		pack();
		setVisible( false );
	}

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		/*
		Component c = e.getComponent();
		// debug
		//System.out.println( c );
		if ( c == tfSource ) {
			String src = tfSource.getText();
			if ( src != null && src.length() > 0 ) {
				boolean bDialog = false;
				if ( torrent == null || torrent_source_file == null ) {
					bDialog = true;
				}
				else if ( torrent_source_file == null || src.compareTo( torrent_source_file.getPath() ) != 0 ) {
					bDialog = true;
				}
				if ( bDialog ) {
					int n = JOptionPane.showConfirmDialog( this, "Build torrent from path/file '" + src + "'?", "Build torrent", JOptionPane.YES_NO_OPTION );
					if ( n == JOptionPane.YES_OPTION ) {
						updateSource();
					}
				}
			}
		}
		else if ( c == cbTorrentPath ) {
			//torrentFile = new File( cbTorrentPath.getText() );
			torrentPath = new File( (String)cbTorrentPath.getSelectedItem() );
			if ( torrentPath != null && torrentPath.exists() && torrentFile != null ) {
				torrentFile = new File( torrentPath, torrentFile.getName() );
			}
			checkState();
		}
			*/
	}

	public void itemStateChanged(ItemEvent e) {
		/*
		Object o = e.getItemSelectable();
		if ( o instanceof JCheckBox ) {
			JCheckBox cb = (JCheckBox)o;
			int st = e.getStateChange();
			String cmd = cb.getActionCommand();
			if ( "tracker.private".equals( cmd ) ) {
				if ( ItemEvent.SELECTED == st ) {
					tracker_private = true;
				}
				else if ( ItemEvent.DESELECTED == st ) {
					tracker_private = false;
				}
			}
		}
		*/
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if ( e.getClickCount() == 1 ) {
		}
		if ( e.isPopupTrigger() ) {
			JTable table = (JTable)e.getSource();
			int rowIndex = table.rowAtPoint(e.getPoint());
			if (rowIndex != -1) {
				table.getSelectionModel().setSelectionInterval( rowIndex, rowIndex );

				final ArchiveFileBase archiveFile = libraryTableModel.getAtRow(rowIndex);

		        javax.swing.SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
						Indexer indexer = new Indexer();
						List<ArchiveEntry> entries = indexer.index(archiveFile.file);
						new ArchiveExplorer(archiveFile.file.getPath(), entries);
		            }
		        });

			}
			//showPopup( e );
		}
	}

	public void mouseReleased(MouseEvent e) {
		if ( e.getClickCount() == 1 ) {
		}
		if ( e.isPopupTrigger() ) {
			JTable table = (JTable)e.getSource();
			int rowIndex = table.rowAtPoint(e.getPoint());
			if (rowIndex != -1) {
				table.getSelectionModel().setSelectionInterval( rowIndex, rowIndex );

				final ArchiveFileBase archiveFile = libraryTableModel.getAtRow(rowIndex);

		        javax.swing.SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
						Indexer indexer = new Indexer();
						List<ArchiveEntry> entries = indexer.index(archiveFile.file);
						new ArchiveExplorer(archiveFile.file.getPath(), entries);
		            }
		        });

			}
			//showPopup( e );
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link java.awt.event.KeyEvent} for a definition of 
     * a key typed event.
     * @param event <code>KeyEvent</code> object.
     */
	public void keyTyped(KeyEvent event) {
	}

    /**
     * Invoked when a key has been pressed. 
     * See the class description for {@link KeyEvent} for a definition of 
     * a key pressed event.
     * @param event <code>KeyEvent</code> object.
     */
	public void keyPressed(KeyEvent event) {
	}

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of 
     * a key released event.
     * @param event <code>KeyEvent</code> object.
     */
	public void keyReleased(KeyEvent event) {
		/*
		if ( event.getKeyCode() == KeyEvent.VK_DELETE ) {
			torrents_remove();
		}
		*/
	}

	public void actionPerformed(ActionEvent e) {
		// debug
		//System.out.println( e.getActionCommand() );
		String cmd = e.getActionCommand();
		if ( cmd == null || cmd.length() == 0) {
			return;
		}
		else if ( "source.path".equals( cmd ) ) {
			//updateSource();
		}
		else if ( "source.select".equals( cmd ) ) {
			selectSource();
		}
		else if ( "source.add".equals( cmd ) ) {
			addSource();
		}
		else if ( "close".equals( cmd ) ) {
			setVisible( false );
		}
	}

	private void selectSource() {
		JFileChooser jfile = new JFileChooser();
		jfile.setDialogType( JFileChooser.OPEN_DIALOG );
		jfile.setDialogTitle( "Select source" );
		//jfile.setApproveButtonText( "Save" );
		//jfile.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		jfile.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
		jfile.setMultiSelectionEnabled( false );

		if ( sourceFile != null ) {
			if ( sourceFile.exists()) {
				if ( sourceFile.isDirectory() ) {
					jfile.setCurrentDirectory( sourceFile.getParentFile() );
					jfile.setSelectedFile( sourceFile );
				}
				else {
					jfile.setSelectedFile( sourceFile );
				}
			}
			else {
				jfile.setCurrentDirectory( sourceFile );
				jfile.setSelectedFile( sourceFile );
			}
		}

		// dialog
		int returnVal = jfile.showOpenDialog( null );
		if ( returnVal == JFileChooser.APPROVE_OPTION ) {
			sourceFile = jfile.getSelectedFile();
			// debug
			//System.out.println( selectedFile.getPath() );
			if ( !sourceFile.exists() ) {
				JOptionPane.showMessageDialog( this, "Source '" + sourceFile.getPath() + "' does not exist.", "Source does not exist", JOptionPane.ERROR_MESSAGE );
				sourceFile = null;
			}
			else {
				tfSource.setText( sourceFile.getPath() );
			}
		}
		if ( sourceFile != null && sourceFile.exists()) {
			buttonAdd.setEnabled( true );
		}
	}

	private void addSource() {
		if ( sourceFile != null && sourceFile.exists() ) {
			Desktop.scannerThread.add( sourceFile );
		}
	}

	public void addFile(ArchiveFileBase archiveFile) {
		libraryTableModel.add( archiveFile );
	}

}
