package org.jwat.tools.gui.library;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JProgressBar;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/*
 * Created on 21/11/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class ArchiveLibraryTableModel implements TableModel, ArchiveFileChangeListener {

	/**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
	public int getColumnCount() {
		return columnNames.length;
	}

	/** Array of column names. */
	private String[] columnNames = { "Records", "Errors", "Warnings", "Exceptions", "Progress", "Size", "Name",  "Path" };
	/** Array of column classes. */
	private Class<?>[] columnClasses = { Integer.class, Integer.class, Integer.class, Integer.class, JProgressBar.class, String.class, String.class, String.class };

	/** Backend list of table entries. */
	private List<ArchiveFileBase> rows = new ArrayList<ArchiveFileBase>();

	public void add(ArchiveFileBase archiveFile) {
		TableModelEvent event = null;

		synchronized ( rows ) {
			event = new TableModelEvent( this, rows.size(), rows.size(), TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT );
			rows.add( archiveFile );

			archiveFile.addListener( this );
		}

		if ( event != null ) {
			eventToListeners( event );
		}
	}

	public ArchiveFileBase getAtRow(int rowIndex) {
		return rows.get(rowIndex);
	}

	/**
     * Returns the name of the column at <code>columnIndex</code>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param	columnIndex	the index of the column
     * @return  the name of the column
     */
	public String getColumnName(int columnIndex) {
		return columnNames[ columnIndex ];
	}

    /**
     * Returns the most specific superclass for all the cell values 
     * in the column.  This is used by the <code>JTable</code> to set up a 
     * default renderer and editor for the column.
     *
     * @param columnIndex  the index of the column
     * @return the common ancestor class of the object values in the model.
     */
	public Class<?> getColumnClass(int columnIndex) {
		return columnClasses[ columnIndex ];
	}

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
	public int getRowCount() {
		return rows.size();
	}

	/**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     *
     * @param	rowIndex	the row whose value is to be queried
     * @param	columnIndex 	the column whose value is to be queried
     * @return	the value Object at the specified cell
     */
	public Object getValueAt(int rowIndex, int columnIndex) {
		ArchiveFileBase archiveFile = null;
		synchronized ( rows ) {
			if ( rowIndex < rows.size() ) {
				archiveFile = rows.get( rowIndex );
			}
			else {
				return null;
			}
		}
		switch ( columnIndex ) {
			case 0:
				return archiveFile.records;
			case 1:
				return archiveFile.errors;
			case 2:
				return archiveFile.warnings;
			case 3:
				return archiveFile.exceptions;
			case 4:
				return archiveFile.progressBar;
			case 5:
				// TODO Ineffective!
				return Long.toString(archiveFile.fileSize);
			case 6:
				return archiveFile.filename;
			case 7:
				return archiveFile.path;
			default:
				return null;
		}
	}

    /**
     * Returns true if the cell at <code>rowIndex</code> and
     * <code>columnIndex</code>
     * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
     * change the value of that cell.
     *
     * @param	rowIndex	the row whose value to be queried
     * @param	columnIndex	the column whose value to be queried
     * @return	true if the cell is editable
     * @see #setValueAt
     */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

    /**
     * Sets the value in the cell at <code>columnIndex</code> and
     * <code>rowIndex</code> to <code>aValue</code>.
     *
     * @param	aValue		 the new value
     * @param	rowIndex	 the row whose value is to be changed
     * @param	columnIndex 	 the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	/** Set of registered listeners. */
	private Set<TableModelListener> listenerSet = new HashSet<TableModelListener>();

    /**
     * Adds a listener to the list that is notified each time a change
     * to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
	public void addTableModelListener(TableModelListener l) {
		synchronized ( listenerSet ) {
			listenerSet.add( l );
			//System.out.println( "+TableModelListener: " + l );
		}
	}

    /**
     * Removes a listener from the list that is notified each time a
     * change to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
	public void removeTableModelListener(TableModelListener l) {
		synchronized ( listenerSet ) {
			listenerSet.remove( l );
			//System.out.println( "-TableModelListener: " + l );
		}
	}

	/**
	 * Send <code>TableModelEvent</code> to all registered listeners.
	 * @param event <code>TableModelEvent</code> object.
	 */
	public void eventToListeners(TableModelEvent event) {
		if ( event != null ) {
			Iterator<TableModelListener> listeners = listenerSet.iterator();
			TableModelListener listener;
			while ( listeners.hasNext() ) {
				listener = listeners.next();
				listener.tableChanged( event );
			}
		}
	}

	public void archiveFileChangeEvent(ArchiveFileBase archiveFile) {
		boolean b;
		int idx;
		ArchiveFileBase tmpjob;
		TableModelEvent event;
		synchronized ( rows ) {
			b = true;
			idx = 0;
			while ( b ) {
				if ( idx < rows.size() ) {
					tmpjob = rows.get( idx );
					if ( tmpjob == archiveFile ) {
						event = new TableModelEvent( this, idx, idx, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE );
						eventToListeners( event );
					}
				}
				else {
					b = false;
				}
				++idx;
			}
		}
	}

}
