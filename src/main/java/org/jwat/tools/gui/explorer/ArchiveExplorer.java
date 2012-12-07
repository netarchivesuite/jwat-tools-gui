package org.jwat.tools.gui.explorer;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.jwat.tools.gui.Lookup;

/**
 * GUI enabling browsing of compressed ARC/WARC contents.
 * 
 * @author Roger G. Coram
 * @version 0.1451
 */

public class ArchiveExplorer extends JPanel implements TreeSelectionListener {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -1104609882336524154L;

    private JTree tree;

    private JTextPane outputPane;

    private JTextPane headerPane;

    private JScrollPane outputView;

    private JScrollPane headerView;

    private File file;

    private Lookup lookup;

    /**
     * @param args Specifies the path to the input file.
     * 
     */
    /*
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                init(archive);
            }
        });
    }
    */

    public ArchiveExplorer(String archive, List<ArchiveEntry> entries) {
        super(new GridLayout(1, 0));

        file = new File(archive);
        try {
            lookup = Lookup.getInstance(file);
        }
        catch (IOException e) {
        	e.printStackTrace();
        }

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Archive Explorer");
        // TODO
        this.createNodes(top, entries);
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);
        /*
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tree.getRowForLocation(e.getX(), e.getY());
                    TreePath path = tree.getPathForRow(row);
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
                            .getPath()[2];
                    openExternal(node);
                }
            }
        });
        */
        JScrollPane treeView = new JScrollPane(tree);
        outputPane = new JTextPane();
        outputPane.setEditable(false);
        outputView = new JScrollPane(outputPane);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setResizeWeight(0.5d);

        JSplitPane splitBottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        headerPane = new JTextPane();
        headerPane.setEditable(false);
        headerView = new JScrollPane(headerPane);
        splitBottom.setLeftComponent(headerView);
        splitBottom.setRightComponent(outputView);
        splitBottom.setResizeWeight(0.5d);

        splitPane.setBottomComponent(splitBottom);

        Dimension minimumSize = new Dimension(100, 50);
        outputView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(100);
        splitPane.setPreferredSize(new Dimension(500, 300));
        add(splitPane);

        JFrame frame = new JFrame("Archive Explorer");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void valueChanged(TreeSelectionEvent select) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                .getLastSelectedPathComponent();
        if (node == null)
            return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            ArchiveEntry entry = (ArchiveEntry) nodeInfo;
            this.showArchiveRecord(entry);
        }
    }

    private void createNodes(DefaultMutableTreeNode top, List<ArchiveEntry> entries) {
        try {
            ArchiveEntry dirEntry = new ArchiveEntry();
            dirEntry.name = file.getPath();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(dirEntry);
            top.add(root);

        	ArchiveEntry entry;
        	StringBuilder sb = new StringBuilder();
            for (int i=0; i<entries.size(); ++i) {
            	entry = entries.get(i);
            	sb.setLength(0);
            	sb.append(entry.index);
            	sb.append("(");
            	sb.append(entry.offset);
            	sb.append(")");
            	sb.append(": ");
            	if (entry.uri != null) {
            		sb.append(entry.uri);
            	} else {
            		sb.append("N/A");
            	}
                entry.name = sb.toString();
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry);
                root.add(node);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    private void showArchiveRecord(ArchiveEntry entry) {
        InputStream input = null;
        try {
        	lookup.lookup_entry(entry.offset);
            input = lookup.payload_inputstream;
        	headerPane.setText(new String(lookup.header) + new String(lookup.payloadHeader));

            outputPane.setText("");
        	if (input != null) {
            	if (entry.name.toLowerCase().matches("^.+\\.(jpg|gif|png|bmp)$")) {
                    StyledDocument doc = (StyledDocument) outputPane.getDocument();
                    Style style = doc.addStyle("StyleName", null);

                    byte[] image;
                    if (entry.name.toLowerCase().matches("^.+\\.bmp$")) {
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

    /*
    private void openExternal(DefaultMutableTreeNode node) {
        if (node == null)
            return;

        Object nodeInfo = null;
        nodeInfo = node.getUserObject();
        ArchiveEntry entry = null;
        if (node.isLeaf()) {
            entry = (ArchiveEntry) nodeInfo;
        } else {
            entry = (ArchiveEntry) node.getLastLeaf().getUserObject();
        }
        InputStream input = this.getPayloadStream(entry);
        String url = entry.header.getUrl();
        String filename = url.substring(url.lastIndexOf("/") + 1);
        File tmp;
        try {
            String[] com = filename.split("\\.");
            if (com.length < 2) {
                tmp = File.createTempFile(com[0], ".txt");
            } else {
                tmp = File.createTempFile(filename.split("\\.")[0] + "___", "."
                        + filename.split("\\.")[1]);
            }
            tmp.deleteOnExit();
            FileOutputStream output = new FileOutputStream(tmp);
            IOUtils.copy(input, output);
            output.flush();
            output.close();
            Desktop desktop = Desktop.getDesktop();
            desktop.open(tmp);
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
        }
    }
    */

}
