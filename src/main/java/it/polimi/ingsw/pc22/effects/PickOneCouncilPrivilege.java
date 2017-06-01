package it.polimi.ingsw.pc22.effects;

import it.polimi.ingsw.pc22.gamebox.Asset;
import it.polimi.ingsw.pc22.player.Player;

public class PickOneCouncilPrivilege implements Effect{
	private Asset choosenAsset;

	@Override
	public boolean isLegal(Player player) {
		
		return true;
	}

	@Override
	public void executeEffect(Player player)
	{
		
		player.addAsset(choosenAsset);
		
	}
	
}
