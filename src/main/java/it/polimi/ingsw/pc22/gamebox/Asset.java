package it.polimi.ingsw.pc22.gamebox;

import java.io.Serializable;

/**
 * This class represents the basic resource of the game.
 * Every asset has a certain value (integer value), and is 
 * associated to a certain type. The list of all available
 * type is included in the enumeration "AssetType".
 */

public class Asset implements Serializable
{
	private int value;
	private AssetType type;
	
	public Asset(int value, AssetType type) {
		this.value = value;
		this.type = type;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public AssetType getType() {
		return type;
	}
	
	public void setType(AssetType type) {
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + value;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Asset other = (Asset) obj;
		if (type != other.type)
			return false;
		if (value != other.value)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return value + " " + type;
	}
}
