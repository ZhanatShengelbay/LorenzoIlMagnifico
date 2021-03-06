package it.polimi.ingsw.pc22.actions;

import it.polimi.ingsw.pc22.gamebox.CouncilPalace;
import it.polimi.ingsw.pc22.gamebox.CouncilPalaceCell;
import it.polimi.ingsw.pc22.gamebox.FamilyMember;
import it.polimi.ingsw.pc22.gamebox.GameBoard;
import it.polimi.ingsw.pc22.player.Player;

import java.util.List;

/**
 * This action is used to position a familiar on the council,
 * it controls that the player has the needed familiar value
 * and resource to execute the action
 * in particular the action asks the player which privilege he wants
 */
public class SettingFamiliarMemberOnCouncilPalace extends Action
{
	public SettingFamiliarMemberOnCouncilPalace(FamilyMember familyMember) {
		super(familyMember);
	}

    public SettingFamiliarMemberOnCouncilPalace() {}

    @Override
    public boolean isLegal(Player player, GameBoard gameBoard)
	{
		System.out.println("Council, the correct value is false: " + (familyMember.getValue() < 1));

		if (familyMember.getValue() < 1)
			return false;
		
		return true;
	}

	@Override
	public boolean executeAction(Player player, GameBoard gameBoard)
	{
		CouncilPalace councilPalace = gameBoard.getCouncilPalace();

		if (!isLegal(player, gameBoard))
			return false;

		List<Player> playersInCouncilPalace = councilPalace.getPlayersInCouncilPalace();

		//Aggiunge il player alla lista se non aveva ancora
		//messo alcun familiare all'interno del council palace

		if(!playersInCouncilPalace.contains(player))
			playersInCouncilPalace.add(player);

		int firstCellFree = councilPalace.firstCellFree();

		CouncilPalaceCell cell = councilPalace.getCouncilPalaceCells()[firstCellFree];

		boolean executed = cell.executeEffects(player, gameBoard);

		if (!executed)
			return false;

		cell.setFamilyMember(familyMember);

		familyMember.setPlayed(true);

		player.setFamiliarPositioned(true);

		return true;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
