package ubc.cosc322;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class Player extends GamePlayer{

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
    public AtomicBoolean our_turn = new AtomicBoolean(false);
    public AtomicInteger player_num = new AtomicInteger(-1);


	
    private String userName = null;
    private String passwd = null;

    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public Player(String userName, String passwd) {
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
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.

    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document.
		//
		if (messageType.equals("cosc322.game-action.move")) {
			// todo (3): test if this executes for both player's turns
			//our_turn.set(true);
			gamegui.updateGameState(msgDetails);
			AICore.UpdateState(msgDetails);
		} else if (messageType.equals("cosc322.game-state.board")) {
			ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get("game-state");
			gamegui.setGameState(state); //doesn't keep the state reference
			AICore.SetState(state); //should be fine if we have this call stack save the state reference
		} else {
			if(userName.equals(msgDetails.get("player-white"))){
				our_turn.set(true);
			}
			player_num.set(our_turn.get() ? 1 : 2);
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
