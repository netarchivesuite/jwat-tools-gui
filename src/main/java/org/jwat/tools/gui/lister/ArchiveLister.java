package org.jwat.tools.gui.lister;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jwat.common.Diagnosis;
import org.jwat.tools.gui.Lookup;
import org.jwat.tools.gui.explorer.ArchiveEntry;

public class ArchiveLister extends JPanel implements KeyListener, MouseListener, WindowListener {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 5529956137811007295L;

	/** Hashing table model. */
	private ArchiveTableModel archiveTableModel;

	/** Content JTable object. */
	private JTable table;

	private JTextPane outputPane;

    private JTextPane headerPane;

    private JTextPane diagnosticsPane;

    private JScrollPane outputView;

    private JScrollPane headerView;

    private JScrollPane diagnosticsView;

    private File file;

    private Lookup lookup;

    public ArchiveLister(String archive, List<ArchiveEntry> entries) {
        super(new GridLayout(1, 0));

        file = new File(archive);
        try {
            lookup = Lookup.getInstance(file);
        }
        catch (IOException e) {
        	e.printStackTrace();
        }

        // Table

		archiveTableModel = new ArchiveTableModel( entries );

		table = new JTable( archiveTableModel );
		table.getColumnModel().getColumn( 0 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 1 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 2 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 3 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 4 ).setPreferredWidth( 32 * 10 );
		table.getColumnModel().getColumn( 5 ).setPreferredWidth( 8 * 10 );
		table.getColumnModel().getColumn( 6 ).setPreferredWidth( 8 * 10 );
		table.addMouseListener( this );
		table.addKeyListener( this );

		//table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		sorter.setSortsOnUpdates( true );

        JScrollPane tableView = new JScrollPane(table);

        outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputView = new JScrollPane(outputPane);

        headerPane = new JTextPane();
        headerPane.setEditable(false);
        headerView = new JScrollPane(headerPane);

        diagnosticsPane = new JTextPane();
        diagnosticsPane.setEditable(false);
        diagnosticsView = new JScrollPane(diagnosticsPane);

        JSplitPane splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane splitPane3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        splitPane1.setTopComponent(tableView);
        splitPane1.setBottomComponent(splitPane2);
        splitPane1.setResizeWeight(0.5d);

        splitPane2.setLeftComponent(splitPane3);
        splitPane2.setRightComponent(outputView);
        splitPane2.setResizeWeight(0.5d);

        splitPane3.setTopComponent(headerView);
        splitPane3.setBottomComponent(diagnosticsView);
        splitPane3.setResizeWeight(0.5d);

        Dimension minimumSize = new Dimension(100, 50);
        tableView.setMinimumSize(minimumSize);
        outputView.setMinimumSize(minimumSize);
        splitPane1.setDividerLocation(100);
        splitPane1.setPreferredSize(new Dimension(500, 300));
        add(splitPane1);

        JFrame frame = new JFrame("Archive Lister");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if ( e.getClickCount() == 1 ) {
			JTable table = (JTable)e.getSource();
			int rowIndex = table.rowAtPoint(e.getPoint());
			if (rowIndex != -1) {
				table.getSelectionModel().setSelectionInterval( rowIndex, rowIndex );

				rowIndex = table.getRowSorter().convertRowIndexToModel(rowIndex);
				final ArchiveEntry archiveEntry = archiveTableModel.getAtRow(rowIndex);
	            showArchiveRecord(archiveEntry);
			}
		}
		if ( e.isPopupTrigger() ) {
			//showPopup( e );
		}
	}

	public void mouseReleased(MouseEvent e) {
		if ( e.getClickCount() == 1 ) {
			JTable table = (JTable)e.getSource();
			int rowIndex = table.rowAtPoint(e.getPoint());
			if (rowIndex != -1) {
				table.getSelectionModel().setSelectionInterval( rowIndex, rowIndex );

				rowIndex = table.getRowSorter().convertRowIndexToModel(rowIndex);
				final ArchiveEntry archiveEntry = archiveTableModel.getAtRow(rowIndex);
	            showArchiveRecord(archiveEntry);
			}
		}
		if ( e.isPopupTrigger() ) {
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
		if ( event.getKeyCode() == KeyEvent.VK_ENTER ) {
			int idx = table.getSelectedRow();
			if (idx != -1) {
				idx = table.getRowSorter().convertRowIndexToModel(idx);
				ArchiveEntry archiveEntry = archiveTableModel.getAtRow(idx);
	            showArchiveRecord(archiveEntry);
			}
			event.consume();
		}
	}

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of 
     * a key released event.
     * @param event <code>KeyEvent</code> object.
     */
	public void keyReleased(KeyEvent event) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

    private void showArchiveRecord(ArchiveEntry entry) {
        InputStream input = null;
        try {
        	lookup.lookup_entry(entry.offset);
            input = lookup.payload_inputstream;
        	headerPane.setText(new String(lookup.header) + new String(lookup.payloadHeader));
            headerPane.setCaretPosition(0);

            StringBuilder sb = new StringBuilder();
            showDiagnosisList(entry.diagnostics.getErrors().iterator(), sb);
            showDiagnosisList(entry.diagnostics.getWarnings().iterator(), sb);
        	diagnosticsPane.setText(sb.toString());
        	diagnosticsPane.setCaretPosition(0);

            outputPane.setText("");
        	if (input != null) {
            	if (entry.contentType != null && "image".equalsIgnoreCase(entry.contentType.contentType) && entry.contentType.mediaType.toLowerCase().matches("^.+\\.(jpg|gif|png|bmp)$")) {
                    StyledDocument doc = (StyledDocument) outputPane.getDocument();
                    Style style = doc.addStyle("StyleName", null);

                    byte[] image;
                    if ("bmp".equalsIgnoreCase(entry.contentType.mediaType)) {
                        BufferedImage bmp = ImageIO.read(input);
                        ByteArrayOutputStream jpg = new ByteArrayOutputStream();
                        ImageIO.write(bmp, "jpg", jpg);
                        image = jpg.toByteArray();
                    } else {
                        //image = IOUtils.toByteArray(input);
                    	image = new byte[lookup.payload_length];
                    	int offset = 0;
                    	int numread = 0;
                    	while (numread != -1 && offset < image.length) {
                    		offset += numread;
                    		numread = input.read(image, offset, image.length - offset);
                    	}
                    }

                    StyleConstants.setIcon(style, new ImageIcon(image));
                    doc.insertString(0, "ignored text", style);
                } else {
                    outputPane.read(input, null);
                }
        	}
            outputPane.setCaretPosition(0);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                	e.printStackTrace();
                }
        	}
        }
    }

	public static void showDiagnosisList(Iterator<Diagnosis> diagnosisIterator, StringBuilder sb) {
		Diagnosis diagnosis;
		while (diagnosisIterator.hasNext()) {
			diagnosis = diagnosisIterator.next();
			sb.append( "         Type: " + diagnosis.type.name() );
			sb.append("\n");
			sb.append( "       Entity: " + diagnosis.entity );
			sb.append("\n");
			String[] labels = null;
			switch (diagnosis.type) {
			/*
			 * 0
			 */
			case EMPTY:
			case INVALID:
			case RECOMMENDED_MISSING:
			case REQUIRED_MISSING:
				labels = new String[0];
				break;
			/*
			 * 1
			 */
			case DUPLICATE:
			case INVALID_DATA:
			case RESERVED:
			case UNKNOWN:
				labels = new String[] {"        Value: "};
				break;
			case ERROR_EXPECTED:
				labels = new String[] {"     Expected: "};
				break;
			case ERROR:
				labels = new String[] {"  Description: "};
				break;
			case REQUIRED_INVALID:
			case UNDESIRED_DATA:
				labels = new String[] {"        Value: "};
				break;
			/*
			 * 2
			 */
			case INVALID_ENCODING:
				labels = new String[] {"        Value: ", "     Encoding: "};
				break;
			case INVALID_EXPECTED:
				labels = new String[] {"        Value: ", "     Expected: "};
				break;
			case RECOMMENDED:
				labels = new String[] {"  Recommended: ", "   Instead of: "};
				break;
			}
			if (diagnosis.information != null) {
				for (int i=0; i<diagnosis.information.length; ++i) {
					if (labels != null && i < labels.length) {
						sb.append( labels[i] + diagnosis.information[i] );
						sb.append("\n");
					}
					else {
						sb.append( "             : " + diagnosis.information[i] );
						sb.append("\n");
					}
				}
			}
		}
	}

}
