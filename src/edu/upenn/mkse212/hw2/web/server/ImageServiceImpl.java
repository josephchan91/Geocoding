package edu.upenn.mkse212.hw2.web.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import edu.upenn.mkse212.hw2.web.client.ImageService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ImageServiceImpl extends RemoteServiceServlet implements
		ImageService {
	
	Set<String> landmarks; 
	
	public ImageServiceImpl() throws IOException {
		final File[] files = new File("output").listFiles();
		
		landmarks = new HashSet<String>(); 
		
		for (File f : files) {
			if (!f.getName().startsWith("part"))
				continue;
			
			BufferedReader fr = new BufferedReader(new FileReader(f));
			
			String line;
			while ((line = fr.readLine()) != null) {
				int partition = line.indexOf('\t');
				String pos = line.substring(0, partition);
				int partition2 = line.indexOf('\t', partition+1);
				String key = line.substring(partition+1, partition2);
				String url = line.substring(partition2+1);
				
				double lat = Double.valueOf(pos.substring(1, pos.indexOf(',')));
				double lon = Double.valueOf(pos.substring(pos.indexOf(',')+1, pos.length() - 1));
				
				landmarks.add(key + "\t" + url + "\t" + lat + "\t" + lon);
			}
		}
	}

	@Override
	public Set<String> fetchImageResults(String name)
			throws IllegalArgumentException {
		return landmarks;
	}

}
