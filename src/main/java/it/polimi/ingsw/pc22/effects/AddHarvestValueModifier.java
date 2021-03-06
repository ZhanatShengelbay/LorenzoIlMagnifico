package it.polimi.ingsw.pc22.effects;

import it.polimi.ingsw.pc22.gamebox.GameBoard;
import it.polimi.ingsw.pc22.player.Player;

/**
 * This class is used to store bonus/malus associated to the harvest action
 * It is useful for Leader Cards effects.
 */

public class AddHarvestValueModifier implements Effect{

	private int value;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public boolean isLegal(Player player, GameBoard gameBoard) 
	{
		return true;
	}

	@Override
	public boolean executeEffects(Player player, GameBoard gameBoard) 
	{
		player.setHarvestValueModifier(player.getHarvestValueModifier() + value);
		
		return true;
	}
	
}
