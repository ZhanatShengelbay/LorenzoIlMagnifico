package it.polimi.ingsw.pc22.effects;

import it.polimi.ingsw.pc22.gamebox.GameBoard;
import it.polimi.ingsw.pc22.player.Player;

/**
 * This effect, when activated, will make possible for
 * the player to occupy spaces already occupied by other
 * players. 
 * This is a Leader Card's effect.
 */

public class DontCareOccupiedPlaces implements Effect {

	@Override
	public boolean isLegal(Player player, GameBoard gameBoard) {
		return true;
	}

	@Override
	public boolean executeEffects(Player player, GameBoard gameBoard) 
	{
		player.setDontCareOccupiedPlaces(true);
		
		return true;
	}

}
