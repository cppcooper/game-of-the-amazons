package ubc.cosc322;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import ygraph.ai.smartfox.games.*;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class AIPlayer extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
    final public AtomicBoolean our_turn = new AtomicBoolean(false);
    final public AtomicInteger player_num = new AtomicInteger(-1);


	
    private String userName = null;
    private String passwd = null;

    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public AIPlayer(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);
    }

    @Override
    public void onLogin() {
    	System.out.println("Congratualations!!! "
    			+ "I am called because the server indicated that the login is successfully");
    	System.out.println("The next step is to find a room and join it: "
    			+ "the gameClient instance created in my constructor knows how!");
		//System.out.println(gameClient.getRoomList().toString());
		gameClient.joinRoom("Okanagan Lake");
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		System.out.printf("message type: %s\n",messageType);
		if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) {
			our_turn.set(true);
			gamegui.updateGameState(msgDetails);
			AICore.UpdateState(msgDetails);
			// todo (1): start new simulation, and interrupt AICore::run thread (needs to restart from current state, and also avoid re-simulating)
			// todo (2): detect if game is over, if yes then terminate necessary threads
		} else if (messageType.equals(GameMessage.GAME_STATE_BOARD)) {
			ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get("game-state");
			gamegui.setGameState(state); //doesn't keep the state reference
			AICore.SetState(state); //should be fine if we have this call stack save the state reference
			// todo (1): start AICore::run (should perform exhaustive breadth first search)
		} else if (messageType.equals(GameMessage.GAME_ACTION_START)) {
			if(userName.equals(msgDetails.get("player-white"))){
				our_turn.set(true);
			}
			player_num.set(our_turn.get() ? 1 : 2);
		} else if (messageType.equals("user-count-change")) {
			gamegui.setRoomInformation(this.gameClient.getRoomList());
		}
		return true;
	}
    
    
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return gamegui;
	}

	@Override
	public void connect() {
    	gameClient = new GameClient(userName, passwd, this);
	}

}//end of class
