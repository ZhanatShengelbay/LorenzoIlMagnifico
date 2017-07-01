package it.polimi.ingsw.pc22.utils;

import it.polimi.ingsw.pc22.adapters.IOAdapter;
import it.polimi.ingsw.pc22.connection.GameMatch;
import it.polimi.ingsw.pc22.messages.ErrorMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fandroid95 on 01/07/2017.
 */
public class ReceiveExcommunicationDecisionThread implements Runnable
{
    @Override
    public void run()
    {
        Long timeout = GameMatch.getTimeout();

        Long timestamp = System.currentTimeMillis();

        IOAdapter adapter = GameMatch.getCurrentPlayer().getAdapter();

        while (System.currentTimeMillis() < timestamp + timeout)
        {
            String excommunicationMessage = adapter.getMessage();

            Pattern costMessage = Pattern.compile("^[1-2]$");

            Matcher matcher = costMessage.matcher(excommunicationMessage);

            if (!matcher.find())
            {
                adapter.printMessage(new ErrorMessage("INVALID INSERTION RETRY"));

                continue;
            }

            Integer choiceInt = Integer.parseInt(excommunicationMessage);

            GameBoardUtils.getCurrentPlayer().setExcommunicationChoice(choiceInt);

            break;
        }
    }
}