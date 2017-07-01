package it.polimi.ingsw.pc22.adapters;

import it.polimi.ingsw.pc22.connection.GameServer;
import it.polimi.ingsw.pc22.exceptions.GenericException;
import it.polimi.ingsw.pc22.messages.Message;
import it.polimi.ingsw.pc22.player.Player;
import it.polimi.ingsw.pc22.rmi.RMIClientStreamService;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fandroid95 on 30/05/2017.
 */
public class RMIIOAdapter extends IOAdapter
{
    private RMIClientStreamService streamService;

    private static final Logger LOGGER = Logger.getLogger(RMIIOAdapter.class.getName());

    public RMIIOAdapter(RMIClientStreamService streamService, Long timeout)
    {
        super.setTimeout(timeout);

        this.streamService = streamService;
    }

    @Override
    public void endConnection(Player player) throws IOException
    {
        String userName = player.getUsername();

        Player user = GameServer.getPlayersMap().get(userName);

        user.setLogged(false);
    }

    @Override
    public void printMessage(Message message)
    {
        try
        {
            streamService.printMessage(message);

            System.out.println(streamService);
        }
            catch (RemoteException e)
        {
            throw new GenericException("cannot write to RMI", e);
        }
    }

    @Override
    public String getMessage()
    {
        String message;

        try
        {
            message = streamService.getMessage();
        }
            catch (RemoteException e)
        {
            message = null;

            LOGGER.log(Level.INFO, "cannot read from RMI", e);

        }

        return message;
    }
}
