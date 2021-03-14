import ygraph.ai.smartfox.games.Amazon;
import ygraph.ai.smartfox.games.BaseGameGUI;

public class ABadDemo {
    public static void main(String[] args) {
        Amazon a = new Amazon("tester506","password2");
        BaseGameGUI.sys_setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                a.Go();
            }
        });
    }
}
