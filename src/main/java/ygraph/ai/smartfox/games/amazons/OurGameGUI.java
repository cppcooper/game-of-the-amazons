package ygraph.ai.smartfox.games.amazons;

import data.structures.GameState;
import main.AICore;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class OurGameGUI extends GameGUIBase {
    public final AtomicBoolean is_closed = new AtomicBoolean(false);
    private ArrayList<Integer> queenfrom = null;
    private ArrayList<Integer> queennew = null;
    private ArrayList<Integer> arrow = null;
    private int counter;

    public OurGameGUI() {
        super();
        sys_setup();
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("window closing");
                AICore.TerminateThreads();
                is_closed.set(true);
            }

            @Override
            public void windowOpened(WindowEvent e) {

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
        });
        ArrayList<Integer> state = new ArrayList<>();
        for(int tile : GameState.start){
            state.add(tile);
        }
        game_board.setGameState(state);
        for(int i = 1; i < 11; ++i) {
            for(int j = 1; j < 11; ++j) {
                PlayerMoveHandler moveHandler = new PlayerMoveHandler(i, j, this);
                game_board.setTHandler(i, j, moveHandler);
            }
        }
        configureControlPanel(getControlPanel());
        pack();
        setVisible(true);
    }

    class PlayerMoveHandler extends MouseAdapter {
        int idi = 0;
        int idj = 0;
        OurGameGUI gui;

        public PlayerMoveHandler(int idi, int idj, OurGameGUI gui) {
            this.idi = idi;
            this.idj = idj;
            this.gui = gui;
        }

        public void mousePressed(MouseEvent e) {
            boolean is_our_turn = true;
            if(is_our_turn) {
                if (this.gui.counter == 0) {
                    OurGameGUI.this.queenfrom = new ArrayList();
                    OurGameGUI.this.queenfrom.add(0, this.idi);
                    OurGameGUI.this.queenfrom.add(1, this.idj);
                    this.gui.counter++;
                } else if (this.gui.counter == 1) {
                    OurGameGUI.this.queennew = new ArrayList();
                    OurGameGUI.this.queennew.add(0, this.idi);
                    OurGameGUI.this.queennew.add(1, this.idj);
                    OurGameGUI.this.counter++;
                } else if (this.gui.counter == 2) {
                    OurGameGUI.this.arrow = new ArrayList();
                    OurGameGUI.this.arrow.add(0, this.idi);
                    OurGameGUI.this.arrow.add(1, this.idj);
                    OurGameGUI.this.counter++;
                }

                if (this.gui.counter == 3) {
                    this.gui.counter = 0;
                    gui.updateGameState(OurGameGUI.this.queenfrom, OurGameGUI.this.queennew, OurGameGUI.this.arrow);
                    //this.mn.getGameClient().sendMoveMessage(HumanPlayer.this.queenfrom, HumanPlayer.this.queennew, HumanPlayer.this.arrow);
                    OurGameGUI.this.queenfrom = null;
                    OurGameGUI.this.queennew = null;
                    OurGameGUI.this.arrow = null;
                }
            }
        }
    }
}
