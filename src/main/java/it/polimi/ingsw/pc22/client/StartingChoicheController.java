package it.polimi.ingsw.pc22.client;

import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import it.polimi.ingsw.pc22.exceptions.GenericException;
import it.polimi.ingsw.pc22.rmi.RMIAuthenticationService;
import it.polimi.ingsw.pc22.rmi.RMIClientStreamService;
import it.polimi.ingsw.pc22.states.LoginState;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class StartingChoicheController {

	@FXML
    private RadioButton GUI;
    @FXML
    private RadioButton CLI;
    @FXML
    private RadioButton Socket;
    @FXML
    private RadioButton RMI;

    private static final int RMI_PORT = 5252;

	private static final int SOCKET_PORT = 9001;
	
    private Client client;
    
    private String choice;
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    //Predefinito: GUI + Socket
    @FXML
    private void handleConfirmButton() {
    	
    	if (RMI.isSelected())
    	{
    		loadRMIConnection();
    		choice = "rmi";
    	}
    	
    	if (Socket.isSelected())
    	{
    		loadSocketConnection();
    		choice = "socket";
    	}
    	
    	if (CLI.isSelected())
    	{
    		//Client client = new Client(choice);
    		//Thread thread = new Thread(client);
    		//thread.start();
    		//System.exit(0);
    		
    	}
    	
    	client.launchStartingChoice(choice);
    	/*Stage primaryStage = main.getPrimaryStage();
    	primaryStage.close();
    	LoginGUI loginGUI = new LoginGUI();
    	loginGUI.start(primaryStage);*/
    	//primaryStage.setTitle("Prova");
    	//main.initStartingChoiche();
    	//primaryStage.show();
    	
    	//else 
    		//Vai su Login
    	
    }
    
    private void loadSocketConnection()
	{
		Socket socket;

		try
		{
			System.out.println("Client running.");

			socket = new Socket("localhost", SOCKET_PORT);

			Client.setGenericState(new LoginState());
			
			ReceiveThread receiveThread = new ReceiveThread(socket);
			Thread thread2 = new Thread(receiveThread);
			thread2.start();

			if (CLI.isSelected())
			{
				ViewThread viewThread = new ViewThread(socket);
				Thread thread = new Thread(viewThread);
				thread.start();
				Stage stage = (Stage) client.getPrimaryStage().getScene().getWindow();
				stage.close();
			}
			
			

		}
		catch (Exception e)
		{
			throw new GenericException(e);
		}
	}
    
    private void loadRMIConnection()
	{
		try
		{
			Registry registry = LocateRegistry.getRegistry(RMI_PORT);

			RMIAuthenticationService authenticationService = (RMIAuthenticationService)
					registry.lookup("auth");

			//TODO TIMEOUT

			RMIClientStreamServiceImpl streamService =
					new RMIClientStreamServiceImpl(30000L);

			RMIClientStreamService stub = (RMIClientStreamService)
					UnicastRemoteObject.exportObject(streamService, 0);

			registry.rebind("client", stub);

			//authenticationService.login();
			
			if (CLI.isSelected())
			{
				authenticationService.login();
				Stage stage = (Stage) client.getPrimaryStage().getScene().getWindow();
				stage.close();
			}

		} catch (RemoteException | NotBoundException e)
		{
			throw new GenericException(e);
		}
	}

}
