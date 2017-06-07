package it.polimi.ingsw.pc22.adapters;

import it.polimi.ingsw.pc22.actions.Action;
import it.polimi.ingsw.pc22.actions.ActionFactory;
import it.polimi.ingsw.pc22.actions.ServantsHandler;
import it.polimi.ingsw.pc22.connection.GameServer;
import it.polimi.ingsw.pc22.connection.User;
import it.polimi.ingsw.pc22.gamebox.*;
import it.polimi.ingsw.pc22.player.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Created by fandroid95 on 30/05/2017.
 */
public class SocketGameAdapter implements GameAdapter
{
    public Socket socket;

    private BufferedReader in;
    private PrintWriter out;

    public SocketGameAdapter(Socket socket)
    {
        this.socket = socket;

        try {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

        }
            catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public FamilyMember askFamiliarMember(Player player, Long timeout)
    {
        Long timestamp = System.currentTimeMillis();

        List<FamilyMember> unusedFamiliarMembers =
                player.getUnusedFamiliarMembers();

        //TODO CALCORARLO MEGLIO
        while(System.currentTimeMillis() < timestamp + timeout)
        {
            this.printMessage("Scegli un familiare tra quelli disponibili:");

            //TODO SISTEMARE STA COSA; BISOGNA CHIAMARE UNA FUNZIONE STATICA ESTERNA!!
            
            this.printMessage(unusedFamiliarMembers.toString());

            String choice = this.getMessage();

            ColorsEnum color = ColorsEnum.getColorFromString(choice);

            if (color == null) continue;

            FamilyMember member = player.getUnusedFamilyMemberByColor(color);

            if (member == null) continue;

            return member;

        }

        return null;
    }

    @Override
    public Asset askServants(Player player, Long timeout)
    {
        Long timestamp = System.currentTimeMillis();

        while (System.currentTimeMillis() < timestamp + timeout)
        {
            this.printMessage("Voi sacrificare servitori per aumentare il valore dell'azione? \n" +
                    "Indica un numero da 0 a " + player.getServants());

            String value = getMessage();

            Integer servantNumber;

            try
            {
                servantNumber = Integer.parseInt(value);

            }
            catch (NumberFormatException e)
            {
                this.printMessage("inserire numero valido");

                continue;
            }

            if (servantNumber > player.getServants()) continue;

            return new Asset(servantNumber, AssetType.SERVANT);
        }

        return null;
    }

    @Override
    public void endConnection(Player player) throws IOException
    {
        String userName = player.getName();

        User user = GameServer.getUsersMap().get(userName);

        user.setLogged(false);

        PrintWriter printWriter =
                new PrintWriter(socket.getOutputStream(), true);

        printWriter.println("EXIT");

        socket.close();
    }

    @Override
    public void printMessage(String message)
    {
        out.println(message);
    }

    @Override
    public String getMessage()
    {
        String answer = null;

        try
        {
            answer = in.readLine();

        }
            catch (IOException e)
        {
            e.printStackTrace();
        }

        return  answer;
    }

	@Override
	public Action askAction(FamilyMember familyMember, Asset servant ,Long timeout)
    {
        Long timestamp = System.currentTimeMillis();

        Action action = null;

        while (System.currentTimeMillis() < timestamp + timeout)
        {
            this.printMessage("Che azione vuoi eseguire?");

            this.printMessage("Seleziona operazione:\n" +
                    "- Metti familiare su torre (es: set tower BUILDING 3 con numberOfFloor tra 0 e 3)\n" +
                    "- Metti familiare su market (es: set market ZONA con zona tra 0 e 3)\n" +
                    "- Metti familiare nella produzione (es: set production)\n" +
                    "- Metti familiare nella raccolto (es: set harvest)\n" +
                    "- Metti familiare nel concilio (es: set council)");

            String choice = getMessage();

            action = ActionFactory.createAction(familyMember, choice);

            break;
        }

        if (servant.getValue() > 0)
            return new ServantsHandler(action, servant);

        return action;
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
