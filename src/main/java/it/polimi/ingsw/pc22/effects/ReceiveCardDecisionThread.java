package it.polimi.ingsw.pc22.effects;

import it.polimi.ingsw.pc22.adapters.IOAdapter;
import it.polimi.ingsw.pc22.connection.GameMatch;
import it.polimi.ingsw.pc22.connection.GameServer;
import it.polimi.ingsw.pc22.gamebox.CardTypeEnum;
import it.polimi.ingsw.pc22.messages.ErrorMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Thread used to handle the choice of the card,
 * among the availables, after the execution of the effect
 * "PickTowerCard"
 */


public class ReceiveCardDecisionThread implements Runnable
{
    private String gameMatchName;

    public ReceiveCardDecisionThread(String gameMatchName)
    {
        this.gameMatchName = gameMatchName;
    }

    @Override
    public void run()
    {
        Long timeout = GameMatch.getTimeout();

        Long timestamp = System.currentTimeMillis();

        GameMatch gameMatch = GameServer.getGameMatchMap().get(gameMatchName);

        IOAdapter adapter = gameMatch.getCurrentPlayer().getAdapter();

        PickTowerCard effect =
                (PickTowerCard) gameMatch.getCurrEffect();

        CardTypeEnum currCardType = effect.getCardType();

        while (System.currentTimeMillis() < timestamp + timeout)
        {
            String cardMessage = adapter.getMessage();

            Pattern cardPattern;

            System.out.println(cardMessage);

            if (cardMessage == null)
            {
                adapter.printMessage(new ErrorMessage("INVALID INSERTION RETRY"));

                continue;
            }

            if (!currCardType.equals(CardTypeEnum.ANY))
            {
                cardPattern = Pattern.compile("^[0-3]$");
            }
            else
            {
                cardPattern = Pattern.compile("^(BUILDING|VENTURE|CHARACTER|TERRITORY) [0-3]$");
            }

            Matcher matcher = cardPattern.matcher(cardMessage);

            if (!matcher.find())
            {
                adapter.printMessage(new ErrorMessage("NO PLACE FOUND"));

                continue;
            }

            String[] choices = cardMessage.split(" ");

            Integer floor;

            if (!currCardType.equals(CardTypeEnum.ANY))
            {
                floor = Integer.parseInt(choices[0]);
            }
            else
            {
                floor = Integer.parseInt(choices[1]);

                CardTypeEnum type = CardTypeEnum.valueOf(choices[0]);

                effect.setCardType(type);
            }

            effect.setFloor(floor);

            break;
        }
    }
}
