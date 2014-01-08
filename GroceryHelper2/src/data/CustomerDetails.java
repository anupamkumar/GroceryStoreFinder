package data;

public class CustomerDetails {
	
	private double locLat=0.00;
    private double locLng=0.00;
    private String address="";
    
    private int noOfShoppingMalls=0;    
    
    private String  nearestShoppingMall="";
    private double nearestShoppingMallLat=0.00;
    private double nearestShoppingMallLng=0.00;
    
    private String preferedShoppingMall="";
    private double preferedShoppingMallLat=0.00;
    private double preferedShoopingMallLng=0.00;
    
	public double getLocLat() {
		return locLat;
	}
	public void setLocLat(double locLat) {
		this.locLat = locLat;
	}
	public double getLocLng() {
		return locLng;
	}
	public void setLocLng(double locLng) {
		this.locLng = locLng;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getNoOfShoppingMalls() {
		return noOfShoppingMalls;
	}
	public void setNoOfShoppingMalls(int noOfShoppingMalls) {
		this.noOfShoppingMalls = noOfShoppingMalls;
	}
	public String getNearestShoppingMall() {
		return nearestShoppingMall;
	}
	public void setNearestShoppingMall(String nearestShoppingMall) {
		this.nearestShoppingMall = nearestShoppingMall;
	}
	public double getNearestShoppingMallLat() {
		return nearestShoppingMallLat;
	}
	public void setNearestShoppingMallLat(double nearestShoppingMallLat) {
		this.nearestShoppingMallLat = nearestShoppingMallLat;
	}
	public double getNearestShoppingMallLng() {
		return nearestShoppingMallLng;
	}
	public void setNearestShoppingMallLng(double nearestShoppingMallLng) {
		this.nearestShoppingMallLng = nearestShoppingMallLng;
	}
	public String getPreferedShoppingMall() {
		return preferedShoppingMall;
	}
	public void setPreferedShoppingMall(String preferedShoppingMall) {
		this.preferedShoppingMall = preferedShoppingMall;
	}
	public double getPreferedShoppingMallLat() {
		return preferedShoppingMallLat;
	}
	public void setPreferedShoppingMallLat(double preferedShoppingMallLat) {
		this.preferedShoppingMallLat = preferedShoppingMallLat;
	}
	public double getPreferedShoopingMallLng() {
		return preferedShoopingMallLng;
	}
	public void setPreferedShoopingMallLng(double preferedShoopingMallLng) {
		this.preferedShoopingMallLng = preferedShoopingMallLng;
	}	
}
