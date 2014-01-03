package data;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableCustomerDetails implements Parcelable {
	private CustomerDetails details;

	public CustomerDetails getDetails() {
		return details;
	}

	public ParcelableCustomerDetails(CustomerDetails d)
	{
		super();
		details = d;
	}

	private ParcelableCustomerDetails(Parcel in)
	{
		details = new CustomerDetails();
		details.setAddress(in.readString());
		details.setLocLat(in.readDouble());
		details.setLocLng(in.readDouble());
		details.setNearestShoppingMall(in.readString());
		details.setNearestShoppingMallLat(in.readDouble());
		details.setNearestShoppingMallLng(in.readDouble());
		details.setNoOfShoppingMalls(in.readInt());
		details.setPreferedShoppingMall(in.readString());
		details.setPreferedShoppingMallLat(in.readDouble());
		details.setPreferedShoopingMallLng(in.readDouble());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(details.getAddress());
		dest.writeString(details.getNearestShoppingMall());
		dest.writeString(details.getPreferedShoppingMall());
		dest.writeInt(details.getNoOfShoppingMalls());
		dest.writeDouble(details.getLocLat());
		dest.writeDouble(details.getLocLng());
		dest.writeDouble(details.getNearestShoppingMallLat());
		dest.writeDouble(details.getNearestShoppingMallLng());
		dest.writeDouble(details.getPreferedShoopingMallLng());
		dest.writeDouble(details.getPreferedShoppingMallLat());
	}
	
	public static final Parcelable.Creator<ParcelableCustomerDetails> CREATOR = new Parcelable.Creator<ParcelableCustomerDetails>() {
		public ParcelableCustomerDetails createFromParcel(Parcel in)
		{
			return new ParcelableCustomerDetails(in);
		}
		
		public ParcelableCustomerDetails[] newArray(int size)
		{
			return new ParcelableCustomerDetails[size];
		}
	};
}
