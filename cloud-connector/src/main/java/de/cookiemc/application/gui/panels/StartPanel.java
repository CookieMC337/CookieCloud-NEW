
package de.cookiemc.application.gui.panels;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import de.cookiemc.application.elements.data.CloudUpdateInfo;
import de.cookiemc.application.elements.StartPanelInfoBox;
import de.cookiemc.application.elements.event.CommitHistoryLoadedEvent;
import de.cookiemc.application.gui.Application;
import de.cookiemc.common.scheduler.Scheduler;
import de.cookiemc.document.Bundle;
import de.cookiemc.document.Document;
import de.cookiemc.document.DocumentFactory;
import de.cookiemc.document.IEntry;
import de.cookiemc.driver.CloudDriver;
import de.cookiemc.driver.event.EventListener;
import de.cookiemc.driver.event.IEventManager;
import de.cookiemc.driver.node.INode;
import de.cookiemc.driver.node.INodeManager;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.kohsuke.github.GHCommit;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StartPanel extends JPanel {

    private JTable updateTable;


    JProgressBar highestCpuUsageBar = new JProgressBar();
    JProgressBar lowestCpuUsageBar = new JProgressBar();


    JLabel highestCpuUsageLabel = new JLabel();
    JLabel lowestCpuUsageLabel = new JLabel();

    private int boxStartX = 65;
    private int boxStartY = 65;

    private Map<Integer, JOptionPane> boxPanes = new HashMap<>();

    public StartPanel(Application instance) throws IOException {

        setLayout(null);

        JLabel boxLabel = new JLabel("Dashboard");
        boxLabel.setIcon(new FlatSVGIcon("de/cookiemc/img/CookieCloud.svg", 20, 20));
        boxLabel.setBounds(400, -10, 300, 100);
        boxLabel.setFont(new Font("Arial", Font.PLAIN, 30));

        add(boxLabel);

        for (StartPanelInfoBox box : instance.getBoxes().values()) {
            createBox(box);
        }

        JLabel tableLabel = new JLabel("Update History");
        tableLabel.setIcon(new FlatSVGIcon("de/cookiemc/img/history.svg"));
        tableLabel.setBounds( 65, 130, 300, 100);
        tableLabel.setFont(new Font("Arial", Font.PLAIN, 30));

        add(tableLabel);

        JScrollPane scrollPane = new JScrollPane();

        updateTable = new JTable();

        updateTable.setBorder(new EtchedBorder());
        updateTable.setShowVerticalLines(true);
        updateTable.setAutoCreateRowSorter(true);
        updateTable.setCellSelectionEnabled(false);

        scrollPane.setViewportView(updateTable);
        scrollPane.setBounds(65, 200, 850, 350);

        try {
            this.updateTable(updateTable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        add(scrollPane);


        highestCpuUsageLabel.setText("Highest CPU-Usage: %node%");
        highestCpuUsageLabel.setBounds(65, 565, 300, 15);
        highestCpuUsageBar.setFont(new Font("Arial", Font.BOLD, 12));

        add(highestCpuUsageLabel);

        highestCpuUsageBar.setOrientation(SwingConstants.HORIZONTAL);
        highestCpuUsageBar.setStringPainted(true);
        highestCpuUsageBar.setValue(60);
        highestCpuUsageBar.setBounds(65, 600, 300, 50);

        add(highestCpuUsageBar);


        lowestCpuUsageLabel.setText("Lowest CPU-Usage: %node%");
        lowestCpuUsageLabel.setBounds(610, 565, 300, 15);
        lowestCpuUsageLabel.setFont(new Font("Arial", Font.BOLD, 12));

        add(lowestCpuUsageLabel);

        lowestCpuUsageBar.setOrientation(SwingConstants.HORIZONTAL);
        lowestCpuUsageBar.setStringPainted(true);
        lowestCpuUsageBar.setValue(20);
        lowestCpuUsageBar.setBounds(610, 600, 300, 50);

        add(lowestCpuUsageBar);

        CloudDriver.getInstance().getProviderRegistry().getUnchecked(IEventManager.class).registerListener(this);

        Scheduler.runTimeScheduler().scheduleRepeatingTask(() -> {
            for (Integer boxId : boxPanes.keySet()) {
                JOptionPane pane = boxPanes.get(boxId);
                StartPanelInfoBox infoBox = instance.getInfoBox(boxId);
                infoBox.setContent(infoBox.getTextUpdater().get());

                pane.setMessage(new Object[]{
                        infoBox.getContent(),
                        "==> " + infoBox.getTitle()
                });
            }

            Collection<INode> nodes = CloudDriver.getInstance().getProviderRegistry().getUnchecked(INodeManager.class).getAllCachedNodes();

            INode highestCpuNode = nodes.stream().max(Comparator.comparingDouble(n -> n.getLastCycleData().getCpuUsage())).orElse(null);
            INode lowestCpuNode = nodes.stream().min(Comparator.comparingDouble(n -> n.getLastCycleData().getCpuUsage())).orElse(null);

            if (highestCpuNode != null) {
                highestCpuUsageBar.setValue((int) highestCpuNode.getLastCycleData().getCpuUsage());
                highestCpuUsageLabel.setText("Highest CPU-Usage: " + highestCpuNode.getName());
            }

            if (lowestCpuNode != null) {
                lowestCpuUsageBar.setValue((int) lowestCpuNode.getLastCycleData().getCpuUsage());
                lowestCpuUsageLabel.setText("Lowest CPU-Usage: " + lowestCpuNode.getName());
            }
        }, 0L, TimeUnit.SECONDS.toMillis(1));
    }


    private void updateTable(JTable updateTable) throws Exception {

        InputStream input = new URL("https://raw.githubusercontent.com/CookieMC337/CookieCloud/master/application.json").openStream();

        Document document = DocumentFactory.newJsonDocument(input);
        Bundle updates = document.getBundle("updates");

        Object[][] tableContent = new Object[updates.size()][tableRows.length];

        for (int i = 0; i < updates.size(); i++) {

            IEntry entry = updates.getEntry(i);
            Document doc = entry.toDocument();
            CloudUpdateInfo updateInfo = doc.toInstance(CloudUpdateInfo.class);

            tableContent[i] = updateInfo.toArray();
        }
        this.setTableContent(tableContent, updateTable);
    }


    String[] tableRows = new String[]{"Date", "From", "Type", "Message", "New Version"};

    private void setTableContent(Object[][] tableContent, JTable updateTable) {
        updateTable.setModel(new DefaultTableModel(tableContent, tableRows) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
    }

    private void updateTableWithCommits(JTable updateTable, List<GHCommit> commits) throws Exception {

        Object[][] tableContent = new Object[commits.size()][tableRows.length];

        for (int i = 0; i < commits.size(); i++) {

            GHCommit commit = commits.get(i);

            tableContent[i] = new Object[]{
                    new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss").format(commit.getCommitDate()),
                    commit.getCommitter().getName(),
                    "ADD_CONTENT",
                    commit.getCommitShortInfo().getMessage(),
                    "???"
            };
        }

        this.setTableContent(tableContent, updateTable);
    }
    public void createBox(StartPanelInfoBox box) {

        int width = 200;
        int height = 75;

        JOptionPane pane = new JOptionPane();

        pane.setMessage(new Object[]{
                box.getContent(),
                "==> " + box.getTitle()
        });
        pane.setOptions(new Object[0]);


        if (box.getResourceIcon() != null && !box.getResourceIcon().trim().isEmpty()) {
            pane.setIcon(new FlatSVGIcon("cloud/CookieCloud/img/" + box.getResourceIcon() + ".svg", 20, 20));
        }
        pane.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        pane.setEnabled(true);
        pane.setVisible(true);
        pane.setBounds(boxStartX, boxStartY, width, height);
        pane.setName("CustomBox#" + box.getId());

        add(pane);
        boxPanes.put(box.getId(), pane);

        boxStartX = width + 20 + boxStartX;
    }

    @EventListener
    public void handle(CommitHistoryLoadedEvent event) {
        Collection<GHCommit> loadedCommits = event.getLoadedCommits();
        /*try {
            updateTableWithCommits(updateTable, (List<GHCommit>) loadedCommits);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}
