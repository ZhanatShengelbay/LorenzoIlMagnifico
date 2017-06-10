package it.polimi.ingsw.pc22.effects;

import java.util.List;

import it.polimi.ingsw.pc22.adapters.IOAdapter;
import it.polimi.ingsw.pc22.gamebox.Asset;
import it.polimi.ingsw.pc22.gamebox.AssetType;
import it.polimi.ingsw.pc22.gamebox.GameBoard;
import it.polimi.ingsw.pc22.player.Player;

public class PickTwoCouncilPrivilege implements Effect{

	private List<Asset> chosenAsset;

	@Override
	public boolean isLegal(Player player, GameBoard gameBoard) 
	{
		return true;
	}

	@Override
	public boolean executeEffects(Player player, GameBoard gameBoard)
	{
		IOAdapter adapter = player.getAdapter();

		chosenAsset = adapter.chooseAssets(2);

		if (chosenAsset.isEmpty()) return false;

		for (Asset asset : chosenAsset)
			player.addAsset(asset);

		return true;
	}

	@Override
	public String toString() {
		return "Pick Two Council Privilege";
	}
}
