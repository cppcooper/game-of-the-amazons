package ygraph.ai.smartfox.games.amazons;

import data.structures.GameState;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class OurGameGUI extends GameGUIBase {
    public final AtomicBoolean is_closed = new AtomicBoolean(false);

    public OurGameGUI() {
        super();
        sys_setup();
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("window closing");
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
        configureControlPanel(getControlPanel());
        pack();
        setVisible(true);
    }
}
