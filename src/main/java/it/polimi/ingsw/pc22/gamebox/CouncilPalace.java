package it.polimi.ingsw.pc22.gamebox;

import it.polimi.ingsw.pc22.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//TODO: Nel gameMatch va controllata la lista di playersInCouncilPalace
// all'inizio di ogni nuovo turno e va SVUOTATA all'inizio del nuovo turno.
public class CouncilPalace implements Serializable
{

	private CouncilPalaceCell[] councilPalaceCells;
	private List<Player> playersInCouncilPalace = new ArrayList<>();
	
	public List<Player> getPlayersInCouncilPalace() {
		return playersInCouncilPalace;
	}

	public void setPlayersInCouncilPalace(List<Player> playersInCouncilPalace) {
		this.playersInCouncilPalace = playersInCouncilPalace;
	}

	public CouncilPalaceCell[] getCouncilPalaceCells() {
		return councilPalaceCells;
	}

	public void setCouncilPalaceCells(CouncilPalaceCell[] councilPalaceCells) {
		this.councilPalaceCells = councilPalaceCells;
	}

	public int firstCellFree() {
		int i=0;
		while ( i < this.councilPalaceCells.length)
		{
			if ((councilPalaceCells[i].isEmpty()))
			{
				return i;
			}
			i++;
		}
		return -1;
	}
	
	public String toString() {
		StringBuilder output = new StringBuilder("COUNCIL PALACE\n"
				+ "FamilyMembers already in council palace:\n");
		for (int i=0; i<firstCellFree(); i++)
			output.append(i + ") " + councilPalaceCells[i].getFamilyMember().toString() + "\n");
		if (firstCellFree()>0 && firstCellFree()%4==0) output.append("\n");
		return output.toString();
	}
}