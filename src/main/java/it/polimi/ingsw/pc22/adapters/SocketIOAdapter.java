package it.polimi.ingsw.pc22.adapters;

import it.polimi.ingsw.pc22.exceptions.GenericException;
import it.polimi.ingsw.pc22.messages.LoginMessage;
import it.polimi.ingsw.pc22.messages.Message;
import it.polimi.ingsw.pc22.player.Player;
import it.polimi.ingsw.pc22.utils.ConsoleReader;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fandroid95 on 30/05/2017.
 */
public class SocketIOAdapter extends IOAdapter implements Runnable
{
    private Socket socket;

    private BufferedReader in;
    private ObjectOutputStream out;

    private static final Logger LOGGER = Logger.getLogger(SocketIOAdapter.class.getName());

    public SocketIOAdapter(Socket socket, Long timeout)
    {
        this.socket = socket;
        super.setTimeout(timeout);

        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new ObjectOutputStream(socket.getOutputStream());
        }
            catch (IOException e)
        {
            throw new GenericException(e);
        }
    }

    public SocketIOAdapter() {
    }

    @Override
    public void run()
    {
        Player player = null;

        boolean started = false;

        try
        {
            while (player == null)
            {
                String authentication = getMessage();

                if (authentication == null) continue;

                player = authenticate(authentication);
            }

            if (player.isInMatch())
            {
                printMessage(new LoginMessage(true, true, player));

                return;
            }

            printMessage(new LoginMessage(true, false, player));

            while (!started)
            {
                String match = getMessage();

                if (match == null) continue;

                started = gameHandling(match);
            }

            printMessage(new LoginMessage(true, true, player));

        }
            catch (IOException e)
        {
            throw new GenericException("Cannot authenticate client", e);
        }
    }

    @Override
    public void endConnection(Player player) throws IOException
    {
        if (player != null)
        {
            player.setLogged(false);
        }

        PrintWriter printWriter =
                new PrintWriter(socket.getOutputStream(), true);

        printWriter.println("EXIT");

        socket.close();
    }

    @Override
    public void printMessage(Message message)
    {
        try
        {
            out.writeUnshared(message);

            out.flush();

            out.reset();
        }
            catch (IOException e)
        {
            LOGGER.log(Level.INFO, "CANNOT WRITE OBJECT", e);
        }
    }

    @Override
    public String getMessage()
    {
        ExecutorService ex = Executors.newSingleThreadExecutor();

        String answer;

        Future<String> result = ex.submit(new ConsoleReader(in));

        try
        {
            answer = result.get(this.getTimeout(), TimeUnit.MILLISECONDS);

        }
            catch (ExecutionException | InterruptedException e)
        {
            answer = null;

            LOGGER.log(Level.INFO, "Cannot read from input", e);

        }
            catch (TimeoutException e)
        {
            result.cancel(true);

            answer = null;

            LOGGER.log(Level.INFO, "Timeout EXPIRED", e);
        }

        return  answer;
    }
}
