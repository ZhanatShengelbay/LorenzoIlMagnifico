package it.polimi.ingsw.pc22.actions;

import it.polimi.ingsw.pc22.effects.DoProductionAction;
import it.polimi.ingsw.pc22.gamebox.*;
import it.polimi.ingsw.pc22.player.Player;

/**
 * This action is used to play a familiar on the the Production section
 * it checks that the requirements are satisfied and executes
 * the action
 */

public class SettingFamiliarMemberOnProduction extends Action
{
	private DoProductionAction doProductionAction;

	public SettingFamiliarMemberOnProduction(FamilyMember familyMember)
	{
		super(familyMember);
	}

    public SettingFamiliarMemberOnProduction() {}

    @Override
    public boolean isLegal(Player player, GameBoard gameBoard)
	{
		Production production = gameBoard.getProduction();

		if (familyMember.getValue() < 1)

			return false;

		if (production.getProductionCell()[0].getFamilyMember() != null &&
				production.getProductionCell()[1].isABlockedCell())
		{
			return false;
		}

		for (ProductionCell productionCell : production.getProductionCell())
		{
			FamilyMember currFamilyMember = productionCell.getFamilyMember();

			if (currFamilyMember == null)
				continue;

			if
			(
				currFamilyMember.getColor()==ColorsEnum.NEUTER ||
				familyMember.getColor() == ColorsEnum.NEUTER
			)
				break;

			if (familyMember.getColor().equals(currFamilyMember.getColor())
					&& !player.isDontCareOccupiedPlaces())

				return false;
		}

		return true;
	}

	@Override
	public boolean executeAction(Player player, GameBoard gameBoard)
	{
		Production production = gameBoard.getProduction();

		if (!isLegal(player, gameBoard))
			return false;

		if (!(player.isDontCareOccupiedPlaces()))
		{
			if (production.firstCellFree() > 0)
			{
				familyMember.setValueModifier(familyMember.getValueModifier() - 3);

				doProductionAction.setValue(familyMember.getValue() - 3);
			}
			else
			{
				doProductionAction.setValue(familyMember.getValue());
			}

			boolean executed = doProductionAction.executeEffects(player, gameBoard);

			if (!executed)
				return false;

			production.getProductionCell()[production.firstCellFree()].setFamilyMember(familyMember);

			familyMember.setPlayed(true);

			player.setFamiliarPositioned(true);

			return true;
		}
		else
		{
			doProductionAction.setValue(familyMember.getValue());

			boolean executed = doProductionAction.executeEffects(player, gameBoard);

			if (!executed) return false;

			production.getProductionCell()[production.firstCellFree()].setFamilyMember(familyMember);
			
			familyMember.setPlayed(true);

			player.setFamiliarPositioned(true);

			return true;
		}
	}

	public void setDoProductionAction(DoProductionAction doProductionAction)
	{
		this.doProductionAction = doProductionAction;
	}
}