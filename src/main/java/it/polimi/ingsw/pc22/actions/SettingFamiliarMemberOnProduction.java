package it.polimi.ingsw.pc22.actions;


import it.polimi.ingsw.pc22.effects.DoProductionAction;
import it.polimi.ingsw.pc22.gamebox.*;
import it.polimi.ingsw.pc22.player.Player;



public class SettingFamiliarMemberOnProduction extends Action {



	public SettingFamiliarMemberOnProduction(FamilyMember familyMember)

	{

		super(familyMember);

	}

    public SettingFamiliarMemberOnProduction() {

    }



    @Override

	protected boolean isLegal(Player player, GameBoard gameBoard) {

		Production production = gameBoard.getProduction();

		if (familyMember.getValue() < 1)

			return false;

		for (ProductionCell productionCell : production.getProductionCell()){

			FamilyMember currFamilyMember = productionCell.getFamilyMember();

			if (currFamilyMember == null) continue;

			if

			(

				currFamilyMember.getColor()==ColorsEnum.NEUTER ||

				familyMember.getColor() == ColorsEnum.NEUTER

			)

				break;

			if (familyMember.getColor().equals(currFamilyMember.getColor()) && !player.isDontCareOccupiedPlaces()) 

				return false;

		}

		return true;

	}



	@Override

	public boolean executeAction(Player player, GameBoard gameBoard) {

		Production production = gameBoard.getProduction();

		DoProductionAction doProductionAction = new DoProductionAction();


		if (isLegal(player, gameBoard) && !(player.isDontCareOccupiedPlaces())) 

		{


			production.getProductionCell()[production.firstCellFree()].setFamilyMember(familyMember);

			familyMember.setPlayed(true);


			if (production.firstCellFree()>0)
			{
				familyMember.setValueModifier(familyMember.getValueModifier()-3);
				
				doProductionAction.setValue(familyMember.getValue()-3);
				
			}
			
			else
			
			{	
				
				doProductionAction.setValue(familyMember.getValue());
			}
			
			doProductionAction.executeEffects(player, gameBoard);

			return true;

		}

		

		else if (isLegal(player, gameBoard) && player.isDontCareOccupiedPlaces())

		{

			production.getProductionCell()[production.firstCellFree()].setFamilyMember(familyMember);
			
			familyMember.setPlayed(true);

			doProductionAction.setValue(familyMember.getValue());
			
			doProductionAction.executeEffects(player, gameBoard);

			return true;

		}

		

		else

		{

			return false;

		}

	}

		

}