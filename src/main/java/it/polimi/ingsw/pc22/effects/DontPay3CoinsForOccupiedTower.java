package it.polimi.ingsw.pc22.effects;

import it.polimi.ingsw.pc22.gamebox.GameBoard;
import it.polimi.ingsw.pc22.player.Player;

public class DontPay3CoinsForOccupiedTower extends Effect {

	@Override
	protected boolean isLegal(Player player, GameBoard gameBoard) {
		return true;
	}

	@Override
	public void executeEffect(Player player, GameBoard gameBoard) {
	
		player.setDontPayThreeCoinsInTowers(true);
		
	}

}
