//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ygraph.ai.smartfox.games.amazons;

import data.pod.Move;
import data.pod.Position;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

public final class AmazonsBoard extends JPanel {
    private static final long serialVersionUID = 2L;
    private JLabel[][] tileArray;
    public static final Color[] bgColor;
    private ImageIcon whitequeen = null;
    private ImageIcon blackqueen = null;
    private ImageIcon[] icon = null;

    public AmazonsBoard() {
        this.setup();
    }

    public void setGameState(final ArrayList<Integer> gameS) {
        for(int i = 1; i < 11; ++i) {
            for(int j = 1; j < 11; ++j) {
                AmazonsBoard.this.tileArray[i][j].setBackground(AmazonsBoard.bgColor[Math.abs(i - j) % 2]);
                int a = (Integer)gameS.get(11 * i + j);
                if (a == 0) {
                    AmazonsBoard.this.tileArray[i][j].setIcon((Icon)null);
                } else if (a == 1) {
                    AmazonsBoard.this.tileArray[i][j].setIcon(AmazonsBoard.this.blackqueen);
                } else if (a == 2) {
                    AmazonsBoard.this.tileArray[i][j].setIcon(AmazonsBoard.this.whitequeen);
                } else if (a == 3) {
                    AmazonsBoard.this.tileArray[i][j].setBackground(AmazonsBoard.bgColor[a]);
                }
            }
        }
    }

    public void updateGameState(Move move) {
        Position[] move_steps = move.makePackage();
        this.tileArray[move_steps[1].row()][move_steps[1].col()].setIcon(this.tileArray[move_steps[0].row()][move_steps[0].col()].getIcon());
        this.tileArray[move_steps[0].row()][move_steps[0].col()].setIcon((Icon)null);
        this.tileArray[move_steps[2].row()][move_steps[2].col()].setBackground(bgColor[3]);
    }

    private void setup() {
        this.initGameBoard();
        GroupLayout jDesktopPane1Layout = new GroupLayout(this);
        this.setLayout(jDesktopPane1Layout);
        SequentialGroup g = jDesktopPane1Layout.createSequentialGroup();

        for(int j = 0; j < 11; ++j) {
            ParallelGroup gc = jDesktopPane1Layout.createParallelGroup(Alignment.LEADING);

            for(int i = 10; i >= 0; --i) {
                gc = gc.addComponent(this.tileArray[i][j]);
            }

            g = g.addGroup(gc);
        }

        jDesktopPane1Layout.setHorizontalGroup(g);
        SequentialGroup gv = jDesktopPane1Layout.createSequentialGroup();

        for(int i = 10; i >= 0; --i) {
            ParallelGroup gr = jDesktopPane1Layout.createParallelGroup(Alignment.LEADING);

            for(int j = 0; j < 11; ++j) {
                gr = gr.addComponent(this.tileArray[i][j]);
            }

            gv.addGroup(gr);
        }

        jDesktopPane1Layout.setVerticalGroup(gv);
    }

    public void setQueenImage(ImageIcon[] images) {
        this.icon[1] = images[0];
        this.icon[2] = images[1];
    }

    protected void setTHandler(int i, int j, MouseAdapter h) {
        this.tileArray[i][j].addMouseListener(h);
    }

    private void initGameBoard() {
        this.setBackground(new Color(1.0F, 0.5F, 0.6F));
        this.setMinimumSize(new Dimension(532, 530));
        this.setPreferredSize(new Dimension(532, 530));
        this.icon = new ImageIcon[4];
        URL imageURL = AmazonsBoard.class.getResource("images/white-queen.png");
        if (imageURL != null) {
            this.whitequeen = new ImageIcon(imageURL);
            this.icon[2] = this.whitequeen;
        }

        imageURL = AmazonsBoard.class.getResource("images/black-queen.png");
        if (imageURL != null) {
            this.blackqueen = new ImageIcon(imageURL);
            this.icon[1] = this.blackqueen;
        }

        this.tileArray = new JLabel[11][11];

        for(int i = 0; i < 11; ++i) {
            this.tileArray[i][0] = new JLabel(Integer.toString(i));
            this.tileArray[0][i] = new JLabel(Character.toString(i + 96));
            this.tileArray[0][i].setHorizontalAlignment(0);
            this.tileArray[0][i].setMinimumSize(new Dimension(50, 50));
            this.tileArray[0][i].setOpaque(true);
            this.tileArray[i][0].setMinimumSize(new Dimension(30, 50));
            this.tileArray[i][0].setOpaque(true);
        }

        Dimension dimension = new Dimension(50, 50);

        for(int i = 1; i < 11; ++i) {
            for(int j = 1; j < 11; ++j) {
                this.tileArray[i][j] = new JLabel();
                this.tileArray[i][j].setMinimumSize(dimension);
                this.tileArray[i][j].setMaximumSize(dimension);
                this.tileArray[i][j].setOpaque(true);
                this.tileArray[i][j].setBackground(bgColor[Math.abs(i - j) % 2]);
                this.tileArray[i][j].setEnabled(true);
            }
        }

    }

    static {
        bgColor = new Color[]{Color.DARK_GRAY, Color.WHITE, null, Color.CYAN};
    }
}
