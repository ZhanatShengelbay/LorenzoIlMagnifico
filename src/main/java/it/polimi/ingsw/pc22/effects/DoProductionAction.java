package it.polimi.ingsw.pc22.effects;

import it.polimi.ingsw.pc22.adapters.IOAdapter;
import it.polimi.ingsw.pc22.adapters.SocketIOAdapter;
import it.polimi.ingsw.pc22.connection.GameMatch;
import it.polimi.ingsw.pc22.connection.GameServer;
import it.polimi.ingsw.pc22.gamebox.BuildingCard;
import it.polimi.ingsw.pc22.gamebox.GameBoard;
import it.polimi.ingsw.pc22.messages.ChooseServantsMessage;
import it.polimi.ingsw.pc22.player.Player;

/**
 * This effect will execute a production action with the
 * value specified in the attribute 'value'.
 * The value can also be increased by the player with the 
 * sacrifice of a certain number of servants.
 */

public class DoProductionAction extends ServantsAction implements Effect
{
	private int value;

	public int getValue()
	{
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public boolean isLegal(Player player, GameBoard gameBoard)
	{
		if (value < player.getPlayerBoard().getBonusTile().getProductionActivationValue())
			return false;

		return true;
	}

	@Override
	public boolean executeEffects(Player player, GameBoard gameBoard)
	{
		String gameName = gameBoard.getGameMatchName();

		GameMatch gameMatch = GameServer.getGameMatchMap().get(gameName);

		gameMatch.setCurrEffect(this);

		IOAdapter adapter = player.getAdapter();

		adapter.printMessage(new ChooseServantsMessage(player));

		if (adapter instanceof SocketIOAdapter)
			new Thread(new ReceiveServantsDecisionThread(gameName)).start();

		if (!super.waitForResult())
			return false;

		value += player.getProductionValueModifier();

		value += super.getServants().getValue();

		if (!isLegal(player, gameBoard))
			return false;

		player.addAsset(player.getPlayerBoard().getBonusTile().getProductionCoinsBonus());
		player.addAsset(player.getPlayerBoard().getBonusTile().getProductionMilitaryPointsBonus());
		player.addAsset(player.getPlayerBoard().getBonusTile().getProductionServantBonus());

		for (BuildingCard card : player.getPlayerBoard().getBuildings()) {
			if (value < card.getPermanentEffectActivationCost())
				continue;

			for (Effect effect : card.getPermanentEffects()) {
				effect.executeEffects(player, gameBoard);
			}
		}

		return true;
	}
}
