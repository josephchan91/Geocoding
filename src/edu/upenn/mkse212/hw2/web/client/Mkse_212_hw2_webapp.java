package edu.upenn.mkse212.hw2.web.client;

import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/*
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Mkse_212_hw2_webapp implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side image-search service.
	 */
	private final ImageServiceAsync imageSearchService = GWT
			.create(ImageService.class);
	
  // GWT module entry point method.
  public void onModuleLoad() {
   /*
    * Asynchronously loads the Maps API.
    *
    * The first parameter should be a valid Maps API Key to deploy this
    * application on a public server, but a blank key will work for an
    * application served from localhost.
   */
   Maps.loadMapsApi("", "2", false, new Runnable() {
      public void run() {
        buildUi();
      }
    });
  }

  private void buildUi() {
    // Open a map centered on Philadelphia, PA USA
    LatLng philaCity = LatLng.newInstance(39.88, -75.25);

    final MapWidget map = new MapWidget(philaCity, 13);
    map.setSize("100%", "100%");
//    map.setZoomLevel(13);
    // Add some controls for the zoom level
    map.addControl(new LargeMapControl());

	imageSearchService.fetchImageResults("",
			new AsyncCallback<Set<String>>() {
				public void onFailure(Throwable caught) {
					Window.alert(SERVER_ERROR);
				}

				public void onSuccess(Set<String> results) {
					for (String line : results) {
						int keyUrl = line.indexOf('\t');
						String key = line.substring(0, keyUrl);
						int urlLat = line.indexOf('\t', keyUrl+1);
						int latLon = line.indexOf('\t', urlLat+1);
						String url = line.substring(keyUrl+1, urlLat);
						
						double lat = Double.valueOf(line.substring(urlLat+1, latLon));
						double lon = Double.valueOf(line.substring(latLon+1));
						
					    final InfoWindowContent content = 
					    	new InfoWindowContent("<b>"+key+"</b><br><img src=\"" + url +
					    			"\">");
	
					    LatLng newPoint = LatLng.newInstance(lat, lon);
					    // Add a marker
					    final Marker m = new Marker(newPoint);
					    m.addMarkerClickHandler(new MarkerClickHandler() {
						      public void onClick(MarkerClickEvent event) {
						        InfoWindow info = map.getInfoWindow();
						        info.open(m,
						            content);
						      }
						    });
						    
					    
					    map.addOverlay(m);
					}
					
				}
			});
    
    final DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
    dock.addNorth(map, 500);

    // Add the map to the HTML host page
    RootLayoutPanel.get().add(dock);
  }
}
