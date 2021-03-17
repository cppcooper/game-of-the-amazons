package ubc.cosc322;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GamePlayer;

public class OurGameGUI extends BaseGameGUI {

    public OurGameGUI(GamePlayer player) {
        super(player);
    }

    @Override
    public void dispose() {
        super.dispose();
        AICore.TerminateThreads();
    }
}
