package org.jwat.tools.gui.explorer;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * GUI enabling browsing of compressed ARC/WARC contents.
 * 
 * @author Roger G. Coram
 * @version 0.1451
 */

public class ArchiveExplorer extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = -1104609882336524154L;

    private JTree tree;

    private JTextPane outputPane;

    private JTextPane headerPane;

    private JScrollPane outputView;

    private JScrollPane headerView;

    private File file;

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
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(
                "Archive Explorer");
        // TODO
        this.createNodes(top, entries);
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
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
            // TODO
            //this.showArchiveRecord(entry);
        }
    }

    private void createNodes(DefaultMutableTreeNode top, List<ArchiveEntry> entries) {
        try {
            ArchiveEntry dirEntry = new ArchiveEntry();
            dirEntry.name = "Ze file";
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(dirEntry);
            top.add(root);

        	ArchiveEntry entry;
            for (int i=0; i<entries.size(); ++i) {
            	entry = entries.get(i);
                entry.name = entry.uri;
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry);
                root.add(node);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /*
    private void showArchiveRecord(ArchiveEntry entry) {
        InputStream input = this.getPayloadStream(entry);
        try {
            if (entry.name.toLowerCase().matches("^.+\\.(jpg|gif|png|bmp)$")) {
                StyledDocument doc = (StyledDocument) outputPane.getDocument();
                Style style = doc.addStyle("StyleName", null);

                byte[] image;
                if (entry.name.matches("^.+\\.bmp$")) {
                    BufferedImage bmp = ImageIO.read(input);
                    ByteArrayOutputStream jpg = new ByteArrayOutputStream();
                    ImageIO.write(bmp, "jpg", jpg);
                    image = jpg.toByteArray();
                } else {
                    image = IOUtils.toByteArray(input);
                }

                StyleConstants.setIcon(style, new ImageIcon(image));
                outputPane.setText("");
                doc.insertString(0, "ignored text", style);
            } else {
                outputPane.read(input, null);
            }
            outputPane.setCaretPosition(0);
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                LOGGER.error(e.toString(), e);
            }
        }
    }
    */

    /*
    private InputStream getPayloadStream(ArchiveEntry entry) {
        Long offset = entry.header.getOffset();
        RandomAccessFile random = null;
        InputStream in = null;
        StringBuilder headers = new StringBuilder();
        try {
            random = new RandomAccessFile(file, "r");
            random.seek(offset);
            if (random.getFilePointer() != offset) {
                throw new IOException("Failed to seek to " + offset);
            }
            if (archiveReader.isCompressed()) {
                in = new GZIPMembersInputStream(new FileInputStream(
                        random.getFD()));
                ((GZIPMembersInputStream) in).setEofEachMember(true);
            } else {
                in = new FileInputStream(random.getFD());
                in = new BufferedInputStream(in, 65536);
            }
            headers.append(WARCRecordUtils.getHeaders(in, false));
            if (!archiveReader.isCompressed()) {
                in = new FixedLengthInputStream(in, entry.header.getLength());
            }
            if (entry.header.getHeaderValue(HEADER_KEY_TYPE) != null) {
                headers.append(WARCRecordUtils.getHeaders(in, true));
            }
            headerPane.setText(headers.toString());
        } catch (Exception e) {
            LOGGER.error(e.toString(), e);
        }
        return in;
    }
    */

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
