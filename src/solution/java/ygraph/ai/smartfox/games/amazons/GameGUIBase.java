package ygraph.ai.smartfox.games.amazons;

import data.pod.Move;
import data.pod.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameGUIBase extends JFrame {
    protected AmazonsBoard game_board = null;
    private JPanel controlPanel;

    public GameGUIBase() {
        this.initComponents();
    }

    public void configureControlPanel(JPanel cPanel) {
        GroupLayout layout = new GroupLayout(cPanel);
        cPanel.setLayout(layout);
        GroupLayout.SequentialGroup g = layout.createSequentialGroup();
        layout.setHorizontalGroup(g);
        GroupLayout.SequentialGroup gv = layout.createSequentialGroup();
        layout.setVerticalGroup(gv);
    }

    public void setGameState(final ArrayList<Integer> gameS) {
        GameGUIBase.this.game_board.setGameState(gameS);
    }

    public void updateGameState(Move move) {
        GameGUIBase.this.game_board.updateGameState(move);
    }

    public void setTHandler(int index, MouseAdapter h) {
        Position p = new Position(index);
        game_board.setTHandler(p.row(), p.col(), h);
    }

    public JPanel getControlPanel() {
        return this.controlPanel;
    }

    protected AmazonsBoard createBoard() {
        return new AmazonsBoard();
    }

    private void initComponents() {
        this.setDefaultCloseOperation(3);
        this.setTitle("Game of the Amazons (" + this.getClass().getSimpleName() + ")");
        this.game_board = this.createBoard();
        this.controlPanel = new JPanel();
        this.controlPanel.setBackground(new Color(100, 150, 210));
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGap(25, 25, 25).addComponent(this.game_board, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 180, 32767).addComponent(this.controlPanel, -2, 150, -2)));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(19, 19, 19).addComponent(this.controlPanel, -2, -1, -2)).addGroup(layout.createSequentialGroup().addGap(19, 19, 19).addComponent(this.game_board, -2, -1, -2))).addContainerGap(-1, 32767)));
    }

    public static void sys_setup() {
        try {
            UIManager.LookAndFeelInfo[] var0 = UIManager.getInstalledLookAndFeels();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2) {
                UIManager.LookAndFeelInfo info = var0[var2];
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException var4) {
            Logger.getLogger(ygraph.ai.smartfox.games.BaseGameGUI.class.getName()).log(Level.SEVERE, (String) null, var4);
        } catch (InstantiationException var5) {
            Logger.getLogger(ygraph.ai.smartfox.games.BaseGameGUI.class.getName()).log(Level.SEVERE, (String) null, var5);
        } catch (IllegalAccessException var6) {
            Logger.getLogger(ygraph.ai.smartfox.games.BaseGameGUI.class.getName()).log(Level.SEVERE, (String) null, var6);
        } catch (UnsupportedLookAndFeelException var7) {
            Logger.getLogger(ygraph.ai.smartfox.games.BaseGameGUI.class.getName()).log(Level.SEVERE, (String) null, var7);
        }

    }
}
