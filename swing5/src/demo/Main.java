package demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import twaver.Element;
import twaver.Follower;
import twaver.SelectableFilter;
import twaver.SendToTopFilter;
import twaver.TWaverUtil;
import twaver.network.TNetwork;
import twaver.network.background.ColorBackground;

public class Main extends JFrame {

    private int count = 5;
    private TNetwork network = new TNetwork();
    private Rectangle[] positions = createNodes();

    public Main() {
        this.setTitle("Swing第五刀——走马观花看世博");
        this.setSize(1000, 700);
        TWaverUtil.centerWindow(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        network.setNetworkBackground(new ColorBackground(Color.white));
        //can not select and move follower.
        network.addSelectableFilter(new SelectableFilter() {

            public boolean isSelectable(Element element) {
                return !(element instanceof Follower);
            }
        });
        network.setSendToTopFilter(new SendToTopFilter() {

            public boolean isSendToToppable(Element elmnt) {
                return false;
            }
        });
        network.addElementClickedActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if (source instanceof ImageNode) {
                    ImageNode node = (ImageNode) source;
                    move(node);
                }
            }
        });
        network.setToolbar(null);

        this.add(network);
        this.setVisible(true);

        Thread thread = new Thread() {

            @Override
            public void run() {
                while (true) {
                    try {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                moveOneStep(true);
                            }
                        });
                        Thread.sleep(3000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    private Rectangle[] createNodes() {
        Rectangle[] bounds = new Rectangle[count];
        int gap = 180;
        for (int i = 1; i <= 5; i++) {
            int size = 150;
            //add image node.
            String imageURL = "/demo/images/" + i + ".jpg";
            ImageNode node = new ImageNode("node" + i, imageURL);
            int location = gap * i;
            if (i == 2 || i == 4) {
                size = (int) (size * 1.4f);
                if (i == 2) {
                    location = gap + gap / 2;
                }
                if (i == 4) {
                    location = gap * 5 - gap / 2;
                }
            }
            if (i == 3) {
                size = size * 2;
            }
            node.setSize(size, (int) (size * 5f / 8f));
            location -= 50;
            node.setCenterLocation(location, 300);
            node.setToolTipText("<html><b>中国馆</b>"
                    + "<br>中国馆你没见过吗？不会吧童鞋?"
                    + "<br>点我，闲着也是闲着");
            bounds[i - 1] = node.getBounds();
            network.getDataBox().addElement(node);
            network.getDataBox().addElement(node.getShadowNode());
            node.setUserObject(i);

            if (i == 1 || i == 5) {
                sendToBottom(node);
            }
            if (i == 3) {
                sendToTop(node);
            }
        }
        return bounds;
    }

    private void moveTwoSteps(final boolean unclockwise) {
        Mover mover = createNodeMover(unclockwise, new Runnable() {

            public void run() {
                Mover mover = createNodeMover(unclockwise, null);
                mover.start();
            }
        });
        mover.start();
    }

    private void moveOneStep(boolean unclockwise) {
        Mover mover = createNodeMover(unclockwise, null);
        mover.start();
    }

    private Mover createNodeMover(boolean unclockwise, final Runnable followingAction) {
        ArrayList<MoverInfo> infos = new ArrayList();
        Iterator it = network.getDataBox().iterator();
        final TreeMap<Integer, ImageNode> sortedNodes = new TreeMap();
        ImageNode firstNode = null;
        ImageNode lastNode = null;
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof ImageNode) {
                ImageNode node = (ImageNode) o;
                int index = (Integer) node.getUserObject();

                if (index == 1) {
                    firstNode = node;
                }
                if (index == count) {
                    lastNode = node;
                }

                if (unclockwise) {
                    index++;
                } else {
                    index--;
                }
                if (index > count) {
                    index = index % count;
                }
                if (index < 1) {
                    index = 5;
                }

                //setup orders.
                if (index == 3) {
                    sortedNodes.put(5, node);
                }
                if (index == 2) {
                    sortedNodes.put(4, node);
                }
                if (index == 4) {
                    sortedNodes.put(3, node);
                }
                if (index == 1) {
                    sortedNodes.put(2, node);
                }
                if (index == 5) {
                    sortedNodes.put(0, node);
                }

                Rectangle newBounds = positions[index - 1];
                infos.add(new MoverInfo(node, newBounds));
                node.setUserObject(index);
            }
        }
        if (unclockwise) {
            sendToBottom(firstNode);
            sendToBottom(lastNode);
        } else {
            sendToBottom(lastNode);
            sendToBottom(firstNode);
        }

        Runnable action = new Runnable() {

            public void run() {
                Iterator<Integer> iterator = sortedNodes.keySet().iterator();
                while (iterator.hasNext()) {
                    int index = iterator.next();
                    ImageNode node = sortedNodes.get(index);
                    sendToTop(node);
                }
                if (followingAction != null) {
                    followingAction.run();
                }
            }
        };

        Mover mover = new Mover(infos, action);
        return mover;
    }

    private void sendToTop(ImageNode node) {
        network.getDataBox().sendToTop(node);
        network.getDataBox().sendToTop(node.getShadowNode());
    }

    private void sendToBottom(ImageNode node) {
        network.getDataBox().sendToBottom(node);
        network.getDataBox().sendToBottom(node.getShadowNode());
    }

    private void move(ImageNode node) {
        if (!Mover.isMoving()) {
            int index = (Integer) node.getUserObject();
            if (index == 1) {
                moveTwoSteps(true);
            }
            if (index == 2) {
                moveOneStep(true);
            }
            if (index == 4) {
                moveOneStep(false);
            }
            if (index == 5) {
                moveTwoSteps(false);
            }
        }
    }

    public static void main(String[] args) {
        Main ui = new Main();
        ui.setVisible(true);
    }
}
