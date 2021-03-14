//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ygraph.ai.smartfox.games.amazons;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JFrame;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;

public class FakePlayer extends GamePlayer {
    public FakePlayer.GameGUI gm = new FakePlayer.GameGUI(this);
    private GameClient gameClient;
    private Map<String, ArrayList<Integer>> moveDetails;
    private ArrayList<Integer> queenfrom = null;
    private ArrayList<Integer> queennew = null;
    private ArrayList<Integer> arrow = null;
    private int counter;
    private String userName = "";
    private AmazonsBoard gameb = null;

    public FakePlayer() {
    }

    public void setGameGui(JFrame gameGui) {
        this.gm = (FakePlayer.GameGUI)gameGui;
    }

    public BaseGameGUI getGameGUI() {
        return this.gm;
    }

    public void postSetup() {
        super.postSetup();
        System.out.println("post-setup");
        this.counter = 0;
        this.gm.createBoard();

        for(int i = 1; i < 11; ++i) {
            for(int j = 1; j < 11; ++j) {
                FakePlayer.PlayerMoveHandler moveHandler = new FakePlayer.PlayerMoveHandler(i, j, this);
                this.gameb.setTHandler(i, j, moveHandler);
            }
        }

    }

    public void connect() {
        this.gameClient = new GameClient(this.userName, "cosc322", this);
    }

    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        System.out.println("message handling..");
        System.out.printf("message type: %s", messageType);
        if (messageType.equals("cosc322.game-state.board")) {
            ArrayList<Integer> gameS = (ArrayList)msgDetails.get("game-state");
            System.out.println("Game Board: " + gameS);
            this.gm.setGameState(gameS);
        } else if (messageType.equals("cosc322.game-action.start")) {
            if (((String)msgDetails.get("player-black")).equals(this.userName())) {
                System.out.println("Game State: " + msgDetails.get("player-black"));
            }

            System.out.println("Game Start:" + msgDetails.get("game-state"));
        } else if (messageType.equals("cosc322.game-action.move")) {
            System.out.println(msgDetails.get("queen-position-current"));
            this.gm.updateGameState(msgDetails);
        } else if (messageType.equals("user-count-change")) {
            this.gm.setRoomInformation(this.gameClient.getRoomList());
        }
        System.out.println("done.");

        return true;
    }

    public void onLogin() {
        System.out.println("Congratualations!!! I am called because the server indicated that the login is successfully");
        System.out.println("The next step is to find a room and join it: the gameClient instance created in my constructor knows how!");
        System.out.println(this.gameClient.getRoomList());
        this.userName = this.gameClient.getUserName();
        this.gm.setRoomInformation(this.gameClient.getRoomList());
    }

    public String userName() {
        return this.userName;
    }

    public GameClient getGameClient() {
        return this.gameClient;
    }

    public static void main(String[] args) {
        FakePlayer p = new FakePlayer();
        BaseGameGUI.sys_setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                p.Go();
            }
        });
    }

    private class GameGUI extends BaseGameGUI {
        public GameGUI(FakePlayer fakePlayer) {
            super(fakePlayer);
        }

        protected AmazonsBoard createBoard() {
            System.out.println("GameGUI");
            FakePlayer.this.gameb = new AmazonsBoard();
            return FakePlayer.this.gameb;
        }
    }

    class PlayerMoveHandler extends MouseAdapter {
        int idi = 0;
        int idj = 0;
        FakePlayer mn;

        public PlayerMoveHandler(int idi, int idj, GamePlayer mn) {
            this.idi = idi;
            this.idj = idj;
            this.mn = (FakePlayer)mn;
        }

        public void mousePressed(MouseEvent e) {
            if (this.mn.counter == 0) {
                FakePlayer.this.queenfrom = new ArrayList();
                FakePlayer.this.queenfrom.add(0, this.idi);
                FakePlayer.this.queenfrom.add(1, this.idj);
                this.mn.counter++;
            } else if (this.mn.counter == 1) {
                FakePlayer.this.queennew = new ArrayList();
                FakePlayer.this.queennew.add(0, this.idi);
                FakePlayer.this.queennew.add(1, this.idj);
                FakePlayer.this.counter++;
            } else if (this.mn.counter == 2) {
                FakePlayer.this.arrow = new ArrayList();
                FakePlayer.this.arrow.add(0, this.idi);
                FakePlayer.this.arrow.add(1, this.idj);
                FakePlayer.this.counter++;
            }

            if (this.mn.counter == 3) {
                this.mn.counter = 0;
                this.mn.gm.updateGameState(FakePlayer.this.queenfrom, FakePlayer.this.queennew, FakePlayer.this.arrow);
                this.mn.getGameClient().sendMoveMessage(FakePlayer.this.queenfrom, FakePlayer.this.queennew, FakePlayer.this.arrow);
                FakePlayer.this.queenfrom = null;
                FakePlayer.this.queennew = null;
                FakePlayer.this.arrow = null;
            }

        }
    }
}
