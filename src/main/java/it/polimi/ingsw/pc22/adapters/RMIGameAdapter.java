package it.polimi.ingsw.pc22.adapters;

import it.polimi.ingsw.pc22.actions.Action;
import it.polimi.ingsw.pc22.connection.GameServer;
import it.polimi.ingsw.pc22.connection.User;
import it.polimi.ingsw.pc22.gamebox.Asset;
import it.polimi.ingsw.pc22.gamebox.CardTypeEnum;
import it.polimi.ingsw.pc22.gamebox.FamilyMember;
import it.polimi.ingsw.pc22.player.Player;
import it.polimi.ingsw.pc22.rmi.RMIClientStreamService;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * Created by fandroid95 on 30/05/2017.
 */
public class RMIGameAdapter implements GameAdapter
{
    Registry registry;

    RMIClientStreamService streamService;

    public RMIGameAdapter(Registry registry)
    {
        this.registry = registry;

        try
        {
            streamService = (RMIClientStreamService) registry.lookup("client");
        }
        catch (RemoteException | NotBoundException e)
        {
                e.printStackTrace();
        }
    }

    @Override
    public FamilyMember askFamiliarMember(Player player, Long timeout) {
        return null;
    }

    @Override
    public Asset askServants(Player player, Long timeout) {
        return null;
    }

    @Override
    public void endConnection(Player player) throws IOException
    {
        String userName = player.getName();

        User user = GameServer.getUsersMap().get(userName);

        user.setLogged(false);
    }

    @Override
    public void printMessage(String message)
    {
        try
        {
            streamService.printMessage(message);
        }
            catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getMessage()
    {
        String message = null;

        try
        {
            message = streamService.getMessage();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }

        return message;
    }
    
	@Override
	public Action askAction(FamilyMember familyMember, Asset servant ,Long timeout)
    {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int askFloor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CardTypeEnum askForCardType() {
		// TODO Auto-generated method stub
		return null;
	}
}
