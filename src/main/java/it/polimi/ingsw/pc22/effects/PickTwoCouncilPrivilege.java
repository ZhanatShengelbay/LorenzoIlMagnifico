package it.polimi.ingsw.pc22.effects;

import it.polimi.ingsw.pc22.gamebox.Asset;
import it.polimi.ingsw.pc22.player.Player;

public class PickTwoCouncilPrivilege implements Effect{
	private Asset choosenAsset1;
	private Asset choosenAsset2;
	
	

	@Override
	public boolean isLegal(Player player) 
	{
		if (choosenAsset1.getType().equals(choosenAsset2.getType()))
		{
			return false;
		}
		return true;
	}

	@Override
	public void executeEffect(Player player) 
	{
		if (isLegal(player))
		{
			player.addAsset(choosenAsset1);
			player.addAsset(choosenAsset2);
		}
		
	}

}
