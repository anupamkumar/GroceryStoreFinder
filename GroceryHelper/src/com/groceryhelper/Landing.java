package com.groceryhelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.groceryhelper.places.LookUpPlaces;

import data.GroceryShop;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Landing extends FragmentActivity implements 
ConnectionCallbacks, OnConnectionFailedListener,
LocationListener, OnMarkerClickListener, OnMapLongClickListener, OnClickListener {
	
	private static final int GPS_ERRORDIALOG_REQUEST = 999;
	private GoogleMap mMap;	
	private LocationClient mLocationClient;
	private Geocoder gc;
	private Marker marker;
	private ArrayList<Address> userMarkers;
	private ArrayList<Marker> manualMarkers;
	private HashMap<String, String> shopListMap;
	private int shopIndex=0;
	private boolean groceryStoresMapped=false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        if(servicesOK())
        {
        	setContentView(R.layout.activity_map);
        	if(initMap())
        	{
        		mMap.setIndoorEnabled(true);
        		mLocationClient = new LocationClient(this, this, this);
        		mLocationClient.connect();
        		gc = new Geocoder(this);
        		userMarkers = new ArrayList<Address>();
        		manualMarkers = new ArrayList<Marker>();
        		shopListMap = new HashMap<String, String>();
        	}
        	else
        	{
        		
        	}
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing, menu);
        return true;
    }
    
    public boolean servicesOK()
    {
    	int isAvail = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	if(isAvail == ConnectionResult.SUCCESS)
    	{
    		return true;
    	}
    	else if(GooglePlayServicesUtil.isUserRecoverableError(isAvail))
    	{
    		Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvail, 
    				this, GPS_ERRORDIALOG_REQUEST);
    		dialog.show();
    		return false;
    	}
    	else
    	{
    		Toast.makeText(this, "Can't connect to google play services", Toast.LENGTH_SHORT).show();
    		return false;
    	}
    }

    private boolean initMap()
	{
		if(mMap == null)
		{
			SupportMapFragment mapFrag = (SupportMapFragment) 
					getSupportFragmentManager().findFragmentById(R.id.mapF);
			mMap = mapFrag.getMap();
		}
		if(mMap != null)
		{	
			mMap.setOnMarkerClickListener(this);
			mMap.setOnMapLongClickListener(this);
		}
		return (mMap != null);
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		MapStateManager mgr = new MapStateManager(this);
		mgr.saveMapState(mMap);
		mgr.saveGroceryShops(manualMarkers);
		mgr.saveShoppingList(shopListMap);
	}
	
	protected void onResume()
	{
		super.onResume();
		MapStateManager mgr = new MapStateManager(this);		
		CameraPosition position = mgr.getSavedCameraPosition();
		int mapType = mgr.getSavedMapType();
		if(mapType != -1)
		{
			mMap.setMapType(mapType);
		}
		if(position != null)
		{
			gotoLocation(position);
		}
		ArrayList<MarkerOptions> mo = mgr.getGroceryShops();
		if(mo != null)
		{
			for(MarkerOptions m : mo)
			{
				Marker marker = mMap.addMarker(m);
				manualMarkers.add(marker);
			}
			groceryStoresMapped=true;
		}		
		shopListMap = mgr.getShoppingListData();
		if(shopListMap == null)
			shopListMap = new HashMap<String, String>();
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.id.current_loc:
			gotoLocation();
			break;
		case R.id.cycle_shops:
			cycleShops();
			break;
		}
		return true;
	}
	
	public void cycleShops()
	{
		if(shopIndex == manualMarkers.size())
			shopIndex = 0;
		Marker m = manualMarkers.get(shopIndex);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(m.getPosition(), 20);
		mMap.animateCamera(update);
		m.showInfoWindow();
		shopIndex++;
	}
	
	public void clearManualMarkers()
	{
		if(manualMarkers != null)
		{
			for(Marker m : manualMarkers)
			{
				m.remove();
			}
			userMarkers.clear();
			manualMarkers.clear();
		}
		
	}

	public void gotoLocation(CameraPosition p)
	{
		CameraUpdate update = CameraUpdateFactory.newCameraPosition(p);
		mMap.moveCamera(update);
		LatLng ll = p.target;
		try {
			List<Address> locs = gc.getFromLocation(ll.latitude, ll.longitude, 1);
			Address l = locs.get(0);
			String address = l.getAddressLine(0);
			placeMarkerOnTheMap(address, ll);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	public void gotoLocation()
	{
		Location curLoc = mLocationClient.getLastLocation();
		if(curLoc != null)
		{
			LatLng ll = new LatLng(curLoc.getLatitude(), curLoc.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 20);
			mMap.animateCamera(update);
			try {
				List<Address> locs = gc.getFromLocation(ll.latitude, ll.longitude, 1);
				Address l = locs.get(0);
				String address = l.getAddressLine(0);
				placeMarkerOnTheMap(address, ll);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
		else
			Toast.makeText(this, "Error finding location", Toast.LENGTH_LONG).show();
	}
	
	public void placeMarkerOnTheMap(String title, LatLng position)
	{
		MarkerOptions op = new MarkerOptions();
		op.title(title);
		op.position(position)
		  .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
		  .snippet("You are here!");
		if(marker != null)
		{
			marker.remove();					
			marker = mMap.addMarker(op);
			
		}
		else
		{
			marker = mMap.addMarker(op);
		}
	}
	
	public void manualAddMarkerOnMap(LatLng ll,String s)
	{
		if(userMarkers == null)
		{
			userMarkers = new ArrayList<Address>();			
		}
		if(manualMarkers == null)
		{
			manualMarkers = new ArrayList<Marker>();
		}
		try {
			List<Address> locs = gc.getFromLocation(ll.latitude, ll.longitude, 1);
			Address l = locs.get(0);	
			userMarkers.add(l);
			String address = l.getAddressLine(0);
			MarkerOptions op = new MarkerOptions();
			op.title(address);
			op.snippet(s);
			op.position(ll);
			Marker temp = mMap.addMarker(op);
			temp.showInfoWindow();
			manualMarkers.add(temp);			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public void manualAddMarkerOnResume(Address add)
	{	
		String address = add.getAddressLine(0);
		MarkerOptions op = new MarkerOptions();
		op.title(address);
		op.position(new LatLng(add.getLatitude(),add.getLongitude()));
		Marker temp = mMap.addMarker(op);
		manualMarkers.add(temp);
	}
	
	public void addShopMarker(GroceryShop g)
	{		
		if(manualMarkers == null)
		{
			manualMarkers = new ArrayList<Marker>();
		}
		MarkerOptions op = new MarkerOptions();
		op.title(g.name);
		op.snippet(g.address);
		op.position(g.location);
		op.icon(BitmapDescriptorFactory.fromResource(R.drawable.shopping_cart));
		Marker m = mMap.addMarker(op);
		manualMarkers.add(m);		
	}
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {		
		Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onConnected(Bundle arg0) {		
		LocationRequest req = LocationRequest.create();
		req.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		req.setInterval(60000);
		req.setFastestInterval(1500);
		mLocationClient.requestLocationUpdates(req, this);
		if(!groceryStoresMapped)
		{
			LatLng ll = new LatLng(mLocationClient.getLastLocation().getLatitude(),mLocationClient.getLastLocation().getLongitude());
			LookUpPlaces lp = new LookUpPlaces();
			try {
				ArrayList<GroceryShop> gs = lp.getShops(ll, 1000);
				for(GroceryShop s : gs)
				{
					addShopMarker(s);
				}
				Toast.makeText(this, "We have found grocery shops for you nearby. Click on 'Visit Shop' to cycle through the grocery shops.", Toast.LENGTH_LONG).show();
				groceryStoresMapped = true;
			} catch (JSONException e) {			
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}


	@Override
	public void onDisconnected() {		
		Toast.makeText(this, "DisConnected", Toast.LENGTH_LONG).show();
	}


	@Override
	public void onLocationChanged(Location arg0) {
		gotoLocation();
	}

	private Dialog shopDialog;
	Marker deletableMarker;
	@Override
	public boolean onMarkerClick(Marker arg0) {
		deletableMarker = arg0;
		shopDialog = new Dialog(this);
		shopDialog.setContentView(R.layout.activity_grocery_list);
		shopDialog.setTitle("Shopping List");
		TextView shopname = (TextView) shopDialog.findViewById(R.id.shopName);
		Button addButton = (Button) shopDialog.findViewById(R.id.addItem);
		Button cb = (Button) shopDialog.findViewById(R.id.clearList);
		Button removeMarker = (Button) shopDialog.findViewById(R.id.removeMarker);
		EditText shoppingList = (EditText) shopDialog.findViewById(R.id.shoppingList2);		
		addButton.setOnClickListener(this);
		cb.setOnClickListener(this);
		removeMarker.setOnClickListener(this);
		shopname.setText(arg0.getTitle());
		if(shopListMap.containsKey(arg0.getTitle()))
		{	
			shoppingList.setText(Html.fromHtml(shopListMap.get(arg0.getTitle())));
		}
		shopDialog.show();	
		return false;
	}
	
	public void addToListView(View v)
	{	
		EditText edTxt = (EditText) shopDialog.findViewById(R.id.itemInput);
		EditText shoppingList = (EditText) shopDialog.findViewById(R.id.shoppingList2);
		TextView shopName = (TextView) shopDialog.findViewById(R.id.shopName);
		String sn = shopName.getText().toString();		
		String s = Html.toHtml(shoppingList.getText());		
		s = s + "&#8226;  "+ edTxt.getText().toString() + "<br/>";
		shoppingList.setText(Html.fromHtml(s));
		shopListMap.put(sn, s);
		edTxt.setText("");
	}
	Address customShopName;
	Dialog shopNameDialog;
	@Override
	public void onMapLongClick(LatLng ll) {
		try {
			List<Address> la =gc.getFromLocation(ll.latitude, ll.longitude, 1);
			customShopName = la.get(0);
			shopNameDialog = new Dialog(this);
			shopNameDialog.setContentView(R.layout.activity_request_shop_name);
			shopNameDialog.setTitle("Grocery Store Name");
			Button addButton = (Button) shopNameDialog.findViewById(R.id.addNewShop);
			addButton.setOnClickListener(this);
			shopNameDialog.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addCustomShopMarker()
	{
		shopNameDialog.dismiss();
		EditText csn = (EditText) shopNameDialog.findViewById(R.id.customShopName);
		MarkerOptions mo = new MarkerOptions();
		mo.title(csn.getText().toString());
		mo.snippet(customShopName.getAddressLine(0));
		mo.position(new LatLng(customShopName.getLatitude(), customShopName.getLongitude()));
		mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.shopping_cart));
		Marker m = mMap.addMarker(mo);
		manualMarkers.add(m);		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.addItem:
				addToListView(v);
				break;
			case R.id.clearList:
				clearShoppingListForShoppingCenter();
				break;
			case R.id.addNewShop:
				addCustomShopMarker();
				break;
			case R.id.removeMarker:
				removeMarker();
				break;
		}
	}
	
	private void clearShoppingListForShoppingCenter()
	{
		
		TextView shopName = (TextView) shopDialog.findViewById(R.id.shopName);
		shopListMap.remove(shopName.getText().toString());
		EditText shoppingList = (EditText) shopDialog.findViewById(R.id.shoppingList2);
		shoppingList.setText("");
	}
	
	private void removeMarker()
	{
		shopDialog.dismiss();
		manualMarkers.remove(deletableMarker);
		shopListMap.remove(deletableMarker.getTitle());
		deletableMarker.remove();
	}


	
	
}
