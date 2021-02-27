package ubc.cosc322;


import java.util.ArrayList;
import java.util.Map;

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
    public ArrayList<Integer> state = null;
	
    private String userName = null;
    private String passwd = null;
 
	
    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     * @throws Exception 
     */
    public static void main(String[] args){
    	if(args.length >= 2) {
			Player player = new Player(args[0], args[1]);

			if (player.getGameGUI() == null) {
				player.Go();
			} else {
				BaseGameGUI.sys_setup();
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						player.Go();
					}
				});
			}
			
		} else {
			System.out.println("Command line arguments missing.");
		}
    	
    }
	
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
		if (messageType.equals("cosc322.game-state.board")) {
			state = (ArrayList<Integer>) msgDetails.get("game-state");
			gamegui.setGameState(state);
		}
		if (messageType.equals("cosc322.game-action.move")) {
			gamegui.updateGameState(msgDetails);
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

	private void read_state(){
	}
 
}//end of class
