package es.orange.inventory.data;

public class ReservationItems {

	private String clientId;
	private String type;
	private int quantity;

	public ReservationItems() {
	}

	public ReservationItems(String clientId, String type, int quantity) {
		this.clientId = clientId;
		this.type = type;
		this.quantity = quantity;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}