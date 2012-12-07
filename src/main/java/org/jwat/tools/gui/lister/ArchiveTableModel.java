package org.jwat.tools.gui.lister;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.jwat.tools.gui.explorer.ArchiveEntry;

public class ArchiveTableModel implements TableModel {

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
	private String[] columnNames = { "Index", "Errors", "Warnings", "Offset", "Target-URI", "WARC-Date", "Content-Length" };
	/** Array of column classes. */
	private Class<?>[] columnClasses = { Integer.class, Integer.class, Integer.class, Long.class, String.class, Date.class, Long.class };

	/** Backend list of table entries. */
	private List<ArchiveEntry> rows = new ArrayList<ArchiveEntry>();

	public ArchiveTableModel(List<ArchiveEntry> entries) {
		TableModelEvent event = null;

		synchronized ( rows ) {
			event = new TableModelEvent( this, rows.size(), rows.size() + entries.size(), TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT );
			rows.addAll( entries );
		}

		if ( event != null ) {
			eventToListeners( event );
		}
	}

	public ArchiveEntry getAtRow(int rowIndex) {
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
		ArchiveEntry archiveEntry = null;
		synchronized ( rows ) {
			if ( rowIndex < rows.size() ) {
				archiveEntry = rows.get( rowIndex );
			}
			else {
				return null;
			}
		}
		switch ( columnIndex ) {
			case 0:
				return archiveEntry.index;
			case 1:
				return archiveEntry.diagnostics.getErrors().size();
			case 2:
				return archiveEntry.diagnostics.getWarnings().size();
			case 3:
				return archiveEntry.offset;
			case 4:
				return archiveEntry.uri;
			case 5:
				return archiveEntry.date;
			case 6:
				return archiveEntry.contentLength;
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

}
