package it.polimi.ingsw.pc22.actions;

import it.polimi.ingsw.pc22.gamebox.Asset;
import it.polimi.ingsw.pc22.gamebox.GameBoard;
import it.polimi.ingsw.pc22.player.Player;

/**
 * This is a wrapper for every action that can use servants,
 * we used the decorator pattern to add to every action the
 * possibility to sacrifice servants in order to 
 * increment the value of the single action
 * this action wraps the action to execute and increments
 * its value using the servants parameter
 */
public class ServantsHandler extends Action
{
    private Action action;

    private Asset servants;

    public ServantsHandler(Action action, Asset servants)
    {
        super();
        this.action = action;
        this.servants = servants;
    }

    @Override
    public boolean isLegal(Player player, GameBoard gameBoard)
    {
        double multiplier;

        if (player.isServantMalus())
            multiplier=1;

        else
            multiplier=0.5;
        
    	int servantsNumber = servants.getValue();

    	if(servantsNumber*(2*multiplier) > player.getServants())
			return false;

        return true;
    }

    @Override
    public boolean executeAction(Player player, GameBoard gameBoard)
    {
    	if (!isLegal(player, gameBoard))
    	    return false;

        int servantsNumber = servants.getValue();

        double multiplier;

        if (player.isServantMalus())
            multiplier=1;

        else
            multiplier=0.5;

        int familiarValue = action.getFamilyMember().getFamiliarValue();

        familiarValue = (int) (familiarValue + servantsNumber/(2*multiplier));

        action.getFamilyMember().setFamiliarValue(familiarValue);

        boolean executed = action.executeAction(player, gameBoard);

        if (!executed)
            return false;

		int currServants = player.getServants();

		servantsNumber = (int) (currServants - 2 * multiplier * servantsNumber);

		player.setServants(servantsNumber);

		return true;
    }
}
