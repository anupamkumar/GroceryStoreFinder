package com.groceryhelper.places;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import data.GroceryShop;

public class LookUpPlaces {

	String url = "https://maps.googleapis.com/maps/api/place/search/json?";
	public String doSearch(LatLng ll, int radius) throws IOException
	{
		String loc=ll.latitude+","+ll.longitude;		
		String params = "location="+loc+"&radius="+radius+"&sensor=true&types=grocery_or_supermarket&key=AIzaSyCJisEayRISTNW_vEcEyPdnttVhFJV2jps";
		url = url + params;
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if(responseCode == 200)
		{
			StringBuilder op = new StringBuilder();
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				op.append(inputLine);
			}
			in.close();
			return op.toString();
		}
		else
		{
			return null;
		}		
	}
	
	public String doSearch(LatLng ll) throws IOException
	{
		String loc=ll.latitude+","+ll.longitude;		
		String params = "location="+loc+"&rankby=distance&sensor=true&types=grocery_or_supermarket&key=AIzaSyCJisEayRISTNW_vEcEyPdnttVhFJV2jps";
		url = url + params;
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		if(responseCode == 200)
		{
			StringBuilder op = new StringBuilder();		
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				op.append(inputLine);
			}
			in.close();
			return op.toString();
		}
		else
		{
			return null;
		}
	}
	
	public ArrayList<GroceryShop> getShops(LatLng ll, int radius) throws JSONException, IOException
	{
		ArrayList<GroceryShop> shops = new ArrayList<GroceryShop>();
		JSONObject json = new JSONObject(doSearch(ll,radius));
		JSONArray results = json.getJSONArray("results");
		for(int i=0;i< results.length();i++)
		{
			JSONObject obj = results.getJSONObject(i);
			JSONObject location = obj.getJSONObject("geometry").getJSONObject("location");			
			GroceryShop shop = new GroceryShop();
			shop.name = obj.getString("name");
			shop.address = obj.getString("vicinity");
			LatLng loc = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
			shop.location = loc;
			shops.add(shop);
		}
		return shops;
	}
}


