package es.orange.inventory.payload;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class TypeInventoryPayload {
	private String type;
	private int count;

	public TypeInventoryPayload() {
	}

	public TypeInventoryPayload(String type, int count) {
		super();
		this.type = type;
		this.count = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public boolean equals(Object other) {
		return Pojomatic.equals(this, other);
	}
}
