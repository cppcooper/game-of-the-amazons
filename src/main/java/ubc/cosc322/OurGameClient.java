package ubc.cosc322;

import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;

public class OurGameClient extends GameClient {

    public OurGameClient(String handle, String passwd, GamePlayer delegate) {
        super(handle, passwd, delegate);
    }

    public OurGameClient(String handle, String passwd) {
        super(handle, passwd);
    }

    @Override
    public void joinRoom(String roomName) {
        super.joinRoom(roomName);
    }

    @Override
    public void leaveCurrentRoom() {
        super.leaveCurrentRoom();
    }
}
