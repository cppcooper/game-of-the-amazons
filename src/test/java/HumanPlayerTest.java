import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

public class HumanPlayerTest {
    public static void main(String[] args) {
        HumanPlayer p = new HumanPlayer();
        BaseGameGUI.sys_setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                p.Go();
            }
        });
    }
}
