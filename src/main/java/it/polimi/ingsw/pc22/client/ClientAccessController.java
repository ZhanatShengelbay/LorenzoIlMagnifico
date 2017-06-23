package it.polimi.ingsw.pc22.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import it.polimi.ingsw.pc22.exceptions.GenericException;
import it.polimi.ingsw.pc22.rmi.RMIAuthenticationService;
import it.polimi.ingsw.pc22.rmi.RMIClientStreamService;
import it.polimi.ingsw.pc22.states.StartMatchState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import static it.polimi.ingsw.pc22.client.Client.getOutSocket;

public class ClientAccessController implements Controller {
	@FXML
    private Button login;
    @FXML
    private Button register;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML 
    private Label output;
    
    private PrintWriter printWriter;
    
    @FXML
    private void handleLoginButton() throws InterruptedException {
    	String output = username.getText() + " " + password.getText() + " L";
    	getOutSocket().println(output);
    	//if (Client.getGenericState() instanceof StartMatchState)
    	//	Client.launchCreationMatch();
    }
    
    @FXML
    private void handleRegisterButton() {
    	String output = username.getText() + " " + password.getText() + " R";
    	getOutSocket().println(output);
        
    }

	@Override
	public void updateScene(String string) {
		output.setText(string);
	}
}
