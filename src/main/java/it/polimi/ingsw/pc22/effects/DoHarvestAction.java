package it.polimi.ingsw.pc22.effects;

import it.polimi.ingsw.pc22.gamebox.Asset;
import it.polimi.ingsw.pc22.gamebox.BuildingCard;
import it.polimi.ingsw.pc22.gamebox.GameBoard;
import it.polimi.ingsw.pc22.player.Player;

public class DoHarvestAction implements Effect {

	private int value;

	public int getValue() {
		
		return value;
		
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public boolean isLegal(Player player, GameBoard gameBoard) {
		
		if (value < player.getPlayerBoard().getBonusTile().getHarvestActivationValue())
			
				return false;
		
		return true;
	}

	@Override
	public boolean executeEffects(Player player, GameBoard gameBoard) {
		
		value += player.getHarvestValueModifier(); //Serve per gestire il malus dell'excommunication card

		if (isLegal(player,gameBoard))
		{
				Asset servants = player.getAdapter().askServants(player);
				if (servants==null) return false;
				value += servants.getValue();
				player.addAsset(player.getPlayerBoard().getBonusTile().getHarvestServantBonus());
				player.addAsset(player.getPlayerBoard().getBonusTile().getHarvestCoinsBonus());
				player.addAsset(player.getPlayerBoard().getBonusTile().getHarvestMilitaryPointsBonus());
				player.addAsset(player.getPlayerBoard().getBonusTile().getHarvestStonesBonus());
				player.addAsset(player.getPlayerBoard().getBonusTile().getHarvestWoodsBonus());
				
		
				for (BuildingCard b : player.getPlayerBoard().getBuildings()){
					
					if(value >= b.getPermanentEffectActivationCost()){
						
						for (Effect e : b.getPermanentEffects())
						{
							e.executeEffects(player, gameBoard);
						}
					
					}
				}
				
			return true;
		}
		
		return false;
	}
		
	
	
}
