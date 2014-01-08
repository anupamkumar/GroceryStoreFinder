package com.groceryhelper;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;

public class MapStateManager {
	private static final String LONGITUDE = "longitude";
	private static final String LATITUDE = "latitude";
	private static final String ZOOM = "zoom";
	private static final String BEARING = "bearing";
	private static final String TILT = "tilt";
	private static final String MAPTYPE = "MAPTYPE";
	@SuppressWarnings("unused")
	private static final String MARKER = "marker";
	
	private static final String PREFS_NAME = "mapCameraState";
	
	private SharedPreferences mapStatePrefs;
	
	public MapStateManager(Context context)
	{
		mapStatePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveMapState(GoogleMap map)
	{
		SharedPreferences.Editor editor = mapStatePrefs.edit();
		CameraPosition position = map.getCameraPosition();
		editor.putFloat(LONGITUDE, (float) position.target.longitude);
		editor.putFloat(LATITUDE, (float) position.target.latitude);
		editor.putFloat(ZOOM, position.zoom);
		editor.putFloat(TILT, position.tilt);
		editor.putFloat(BEARING, position.bearing);
		editor.putInt(MAPTYPE, map.getMapType());		
		editor.commit();
		
	}
	
	public void saveUserMarkers(ArrayList<Address> userMarkers)
	{
		if(userMarkers != null)
		{
			SharedPreferences.Editor ed = mapStatePrefs.edit();
			int i=0;
			for(Address add : userMarkers)
			{				
				String details = add.getAddressLine(0)+";;"
								+add.getAddressLine(1)+";;"
								+add.getAdminArea()+";;"
								+add.getCountryCode()+";;"
								+add.getCountryName()+";;"
								+add.getFeatureName()+";;"
								+add.getLatitude()+";;"
								+add.getLocality()+";;"
								+add.getLongitude()+";;"
								+add.getMaxAddressLineIndex()+";;"
								+add.getPhone()+";;"
								+add.getPostalCode()+";;"
								+add.getPremises()+";;"
								+add.getSubAdminArea()+";;"
								+add.getSubLocality()+";;"
								+add.getSubThoroughfare()+";;"
								+add.getThoroughfare()+";;"
								+add.getUrl();
				ed.putString("userMarker"+i, details);
				i++;
			}
			ed.putInt("NumberOfUserMarkers", i);
			ed.commit();
		}
	}
	
	public ArrayList<Address> getUserMarkers()
	{
		int noOfMarkers = mapStatePrefs.getInt("NumberOfUserMarkers", -1);
		if(noOfMarkers == -1)
		{
			return null;
		}
		else
		{
			ArrayList<Address> op = new ArrayList<Address>();
			for(int i=0;i<noOfMarkers;i++)
			{
				Address add = new Address(null);
				String s = mapStatePrefs.getString("userMarker"+i, null);
				String[] address = s.split(";;");
				add.setAddressLine(0, address[0]);
				add.setAddressLine(1, address[1]);
				add.setAdminArea(address[2]);
				add.setCountryCode(address[3]);
				add.setCountryName(address[4]);
				add.setFeatureName(address[5]);
				add.setLatitude(Double.parseDouble(address[6]));
				add.setLocality(address[7]);
				add.setLongitude(Double.parseDouble(address[8]));
				add.setPhone(address[9]);
				add.setPostalCode(address[10]);
				add.setPremises(address[11]);
				add.setSubAdminArea(address[12]);
				add.setSubLocality(address[13]);
				add.setSubThoroughfare(address[14]);
				add.setThoroughfare(address[15]);
				add.setUrl(address[16]);
				op.add(add);				
			}
			return op;
		}
	}
	
	
	public CameraPosition getSavedCameraPosition()
	{
		double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
		if(latitude == 0)
		{
			return null;
		}
		else
		{
			double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);
			LatLng target = new LatLng(latitude, longitude);
			float zoom = mapStatePrefs.getFloat(ZOOM, 0);
			float tilt = mapStatePrefs.getFloat(TILT, 0);
			float bearing = mapStatePrefs.getFloat(BEARING, 0);
			
			CameraPosition position = new CameraPosition(target, zoom, tilt, bearing);
			return position;
		}
	}
	
	public int getSavedMapType()
	{
		int mapType = mapStatePrefs.getInt(MAPTYPE, 0);
		if(mapType == 0)
		{
			return -1;
		}
		else
		{
			return mapType;
		}		
	}

	public void saveGroceryShops(ArrayList<Marker> manualMarkers) {
		if(manualMarkers != null)
		{
			SharedPreferences.Editor ed = mapStatePrefs.edit();
			int i=0;
			for(Marker m : manualMarkers)
			{
				ed.putString("Name"+i, m.getTitle());
				ed.putString("Address"+i, m.getSnippet());
				ed.putFloat("Lat"+i, (float) m.getPosition().latitude);
				ed.putFloat("Lng"+i, (float) m.getPosition().longitude);
				i++;
			}
			ed.putInt("NumberOfShops", i);
			ed.commit();
		}
	}

	public ArrayList<MarkerOptions> getGroceryShops() {
		int noOfMarkers = mapStatePrefs.getInt("NumberOfShops", -1);
		if(noOfMarkers == -1)
		{
			return null;
		}
		else
		{
			ArrayList<MarkerOptions> am = new ArrayList<MarkerOptions>();
			for(int i=0;i<noOfMarkers;i++)
			{
				String name = mapStatePrefs.getString("Name"+i, null);
				String address = mapStatePrefs.getString("Address"+i, null);
				double lat = mapStatePrefs.getFloat("Lat"+i, -1);
				double lng = mapStatePrefs.getFloat("Lng"+i, -1);
				MarkerOptions mo = new MarkerOptions();
				mo.title(name);
				mo.snippet(address);
				mo.position(new LatLng(lat, lng));
				mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.shopping_cart));
				am.add(mo);
			}
			return am;
		}		
	}

	public void saveShoppingList(HashMap<String, String> shopListMap) {		
		if(shopListMap != null)
		{
			SharedPreferences.Editor ed = mapStatePrefs.edit();
			int i=0;
			for(String shoppingCenter : shopListMap.keySet())
			{
				String shoppingCenterItemList = shopListMap.get(shoppingCenter);
				ed.putString("SL"+i, shoppingCenter+";;"+shoppingCenterItemList);
				i++;
			}
			ed.putInt("NoOfShoppingCenters", i);
			ed.commit();
		}
	}

	public HashMap<String, String> getShoppingListData() {
		int noOfShoppingCenters = mapStatePrefs.getInt("NoOfShoppingCenters", -1);
		if(noOfShoppingCenters == -1)
		{
			return null;
		}
		else
		{
			HashMap<String,String> op = new HashMap<String, String>();
			for(int i=0;i<noOfShoppingCenters;i++)
			{
				String temp = mapStatePrefs.getString("SL"+i, null);
				String[] keyVal = temp.split(";;");
				op.put(keyVal[0], keyVal[1]);
			}
			return op;
		}
	}
}
