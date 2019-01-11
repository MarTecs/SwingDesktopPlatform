package free;

import free.test.CompositionDemo;
import free.test.GoogleStockDemo;
import free.test.PopulationDemo;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import twaver.TWaverUtil;

public class Shell extends JFrame {

    private String menuBarXML = "/free/menubar.xml";
    private String toolbarXML = "/free/toolbar.xml";
    private String outlookPaneXML = "/free/outlook.xml";
    private ActionListener defaultAction = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            command(command);
        }
    };
    private String productName = "2BizBox";
    private String companyName = "Serva Software";
    private FreeMenuBar menubar = FreeUtil.loadMenuBar(menuBarXML, defaultAction);
    private FreeContentPane contentPane = new FreeContentPane();
    private FreeStatusBar statusBar = new FreeStatusBar();
    private FreeMemoryBar memoryBar = new FreeMemoryBar();
    private FreeStatusMessageLabel lbStatusMessage = new FreeStatusMessageLabel();
    private FreeStatusTimeLabel lbStatusTime = new FreeStatusTimeLabel();
    private FreeStatusLabel lbServer = createStatusLabel("218.83.152.220", "/free/test/server.png");
    private FreeStatusLabel lbUser = createStatusLabel("admin", "/free/test/user.png");
    private FreeStatusLabel lbVersion = createStatusLabel("v3.0.0", null);
    private FreeListPane shortcutPane = new FreeListPane();
    private FreeOutlookPane outlookPane = new FreeOutlookPane(shortcutPane);
    private FreeTabbedPane tab = new FreeTabbedPane();

    public Shell() {
        initSwing();
        initOutlookPane();
        initTab();
        initShortcutList();
        initStatusBar();
        initMockers();
    }

    private void initSwing() {
        this.setTitle(productName + " v3.0 - Best Free ERP in the World [" + companyName + "]");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1024, 768);
        this.setIconImage(TWaverUtil.getImage("/free/test/logo.png"));

        this.setContentPane(contentPane);
        TWaverUtil.centerWindow(this);
        contentPane.add(menubar, BorderLayout.NORTH);
        contentPane.add(statusBar, BorderLayout.SOUTH);

        JPanel centerPane = new JPanel(new BorderLayout());
        centerPane.setOpaque(true);
        centerPane.setBackground(FreeUtil.CONTENT_PANE_BACKGROUND);
        centerPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        centerPane.add(shortcutPane, BorderLayout.EAST);
        centerPane.add(outlookPane, BorderLayout.WEST);
        contentPane.add(centerPane, BorderLayout.CENTER);
        centerPane.add(tab, BorderLayout.CENTER);
        lbStatusMessage.setText("Server is connected");
    }

    private void initOutlookPane() {
        FreeUtil.loadOutlookToolBar(toolbarXML, outlookPane.getHeader(), defaultAction);

        ActionListener nodeActionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source instanceof FreeNetwork) {
                    FreeNetwork network = (FreeNetwork) source;
                    FreeOutlookBar bar = outlookPane.getBarByNetwork(network);
                    bar.setSelected(true);

                    //if parent is null, means network is not showed in tab.
                    if (network.getParent() == null) {
                        String title = bar.getTitle();
                        tab.addTab(title, createPage(network));
                    } else {
                        FreePagePane pagePane = FreeUtil.getPagePane(network);
                        tab.setSelectedComponent(pagePane);
                    }
                }
            }
        };
        ActionListener nodeButtonAction = defaultAction;
        ActionListener shortcutAction = defaultAction;
        FreeUtil.loadOutlookPane(outlookPaneXML, outlookPane,
                nodeActionListener, nodeButtonAction, shortcutAction);
    }

    private void initTab() {
        //double click tab title, shink left/right side bars.
        tab.addMouseListener(new MouseAdapter() {

            private boolean isMaximized() {
                return outlookPane.isShrinked() && shortcutPane.isShrinked();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    TabbedPaneUI ui = tab.getUI();
                    int tabIndex = ui.tabForCoordinate(tab, e.getX(), e.getY());
                    //only when tab header is clicked.
                    if (tabIndex != -1) {
                        boolean maxed = isMaximized();
                        outlookPane.setShrink(!maxed);
                        shortcutPane.setShrink(!maxed);
                    }
                }
            }
        });

        tab.addTab("All Purchase Orders", createReportPage());
        tab.addTab("Dashboard", createPage(new CompositionDemo()));
        tab.addTab("Google Stock", createPage(new GoogleStockDemo()));
        tab.addTab("Global Customers", createPage(new PopulationDemo()));
    }

    private FreeReportPage createReportPage() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Price");
        model.addColumn("Title");
        model.addColumn("Publisher");
        model.addColumn("Comments");

        for (int i = 0; i < 100; i++) {
            Vector row = new Vector();
            row.add("5223");
            row.add("Bill Gates");
            row.add("$12,444.00");
            row.add("Sales Manager");
            row.add("Doubleday");
            row.add("director or manager misappropriates company funds or takes company funds and lends them to");
            model.addRow(row);
        }

        FreeReportPage page = new FreeReportPage();
        page.getTable().setModel(model);
        page.setDescription("All Work Order Items by Part Number. Created " + new Date().toString());
        setupPageToolbar(page);

        return page;
    }

    private void initShortcutList() {
        shortcutPane.setTitle("Start a Task");
    }

    private void initStatusBar() {
        statusBar.getLeftPane().add(lbStatusMessage, BorderLayout.CENTER);
        statusBar.addSeparator();
        statusBar.getRightPane().add(memoryBar);
        statusBar.addSeparator();
        statusBar.getRightPane().add(new FreeGarbageCollectButton());
        statusBar.addSeparator();
        statusBar.getRightPane().add(lbServer);
        statusBar.addSeparator();
        statusBar.getRightPane().add(lbUser);
        statusBar.addSeparator();
        statusBar.getRightPane().add(lbStatusTime);
        statusBar.addSeparator();
        statusBar.getRightPane().add(lbVersion);
        statusBar.addSeparator();
        statusBar.getRightPane().add(createStatusLabel("Powered by " + productName, null));
    }

    public void setServer(String server) {
        lbServer.setText(server);
    }

    public void setUser(String username) {
        lbUser.setText(username);
    }

    public void setVersion(String version) {
        lbVersion.setText(version);
    }

    private void initMockers() {

        Thread thread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            Thread.sleep(5000);
                            if (i == 0) {
                                lbStatusMessage.setGreenLight();
                                lbStatusMessage.setText("Server is connected");
                            }
                            if (i == 1) {
                                lbStatusMessage.setOrangeLight();
                                lbStatusMessage.setText("Server connection is slow");
                            }
                            if (i == 2) {
                                lbStatusMessage.setRedLight();
                                lbStatusMessage.setText("Server connection is broken");
                            }
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };
        thread.start();
    }

    private FreePagePane createPage(JComponent pageContent) {
        FreePagePane page = new FreePagePane(pageContent);
        setupPageToolbar(page);
        return page;
    }

    private void setupPageToolbar(FreePagePane page) {
        page.getLeftToolBar().add(createButton("/free/test/home.png", "Click to go home", false));
        page.getLeftToolBar().add(createButton("/free/test/left.png", "Click to go left", false));
        page.getLeftToolBar().add(createButton("/free/test/right.png", "Click to go right", false));

        FreeSearchTextField txtSearch = new FreeSearchTextField();
        txtSearch.setColumns(10);
        page.getRightToolBar().add(txtSearch);
        page.getRightToolBar().add(createButton("/free/test/add.png", "Click to go right", true));
        page.getRightToolBar().add(createButton("/free/test/update.png", "Click to go right", true));
        page.getRightToolBar().add(createButton("/free/test/refresh.png", "Click to go right", true));
        page.getRightToolBar().add(createButton("/free/test/print.png", "Click to go right", true));
    }

    private FreeToolbarButton createButton(String icon, String tooltip, boolean rover) {
        FreeToolbarButton button = null;
        if (rover) {
            button = new FreeToolbarRoverButton();
        } else {
            button = new FreeToolbarButton();
        }
        button.setIcon(TWaverUtil.getIcon(icon));
        button.setToolTipText(tooltip);
        return button;
    }

    private FreeStatusLabel createStatusLabel(String text, String imageURL) {
        if (imageURL != null) {
            return new FreeStatusLabel(text, TWaverUtil.getIcon(imageURL));
        } else {
            return new FreeStatusLabel(text);
        }
    }

    public void command(String action) {
        String message = "Perform action " + action + ".";
        this.lbStatusMessage.setText(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                FreeUtil.setupLookAndFeel();
                Shell shell = new Shell();
                shell.setVisible(true);
            }
        });
    }
}
