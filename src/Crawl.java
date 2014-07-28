
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;




public class Crawl {
	static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, MalformedURLException
	{
		parseFileForExternals();
		//tagFile();
		//parseFile("data/wineries.txt");
		//parseWithKnowns();
		
		//buildEdgeSet();
	}
	
	public static void buildEdgeSet() throws FileNotFoundException, UnsupportedEncodingException
	{
		LinkedList<String> wineries = urlFromFile("data/wineryLinks.txt");
		LinkedList<String> links = urlFromFile("data/linkLinks.txt");
		
		PrintWriter writer = new PrintWriter("data/edgeSet.txt", "UTF-8");
		String link;
		String wine;
		for (int i = 0; i < links.size(); i++)
		{
			link = links.get(i);
			wine = wineries.get(i);
			
			String[] array = wine.split("\\s+");
			
			for (String w : array)
			{
				writer.println(link.replace(" ", "") + " " + w.replace(" ", ""));
			}
			
		}
		
		writer.close();
		
	}
	
	public static LinkedList<String> parseFile(String path) throws MalformedURLException
	{
		LinkedList<String> list = urlFromFile(path);
		
		for (int i = 0; i < list.size(); i++)
		{
			String next = list.get(i);
			try
			{
				next = next.replace("http://www.", "");
				
	    		
	    		if (!next.contains("www"))
	    			next = "http://www." + next;
	    		else
	    			next = "http://" + next;
	    		
	    		
	    		
	    		System.out.println(next);
	    		
			}
			catch (Exception e)
			{
				System.out.println(next + " did not parse");
				System.exit(0);
			}
		}
		
		
		return list;
		
	}
	
	
	public static void tagFile() throws FileNotFoundException, UnsupportedEncodingException, MalformedURLException
	{
		LinkedList<String> list = urlFromFile("data/wineryExternals.txt");
		
		GooglePlaces client = new GooglePlaces("AIzaSyAjia1NdqXrNmBVVwe8TTmd7YqvX5BYJRA");
		
		
		PrintWriter writer = new PrintWriter("data/taggedWineries.txt", "UTF-8");
        
	    int l = 0;
	    int count = 0;
	    
	    
	    for (String url : list)
        {
	    	String urlCopy = url;
	        String p = new URL(urlCopy).getProtocol();
	        String h = new URL(urlCopy).getHost();
//	        
//	        if (!h.contains("www."))
//    			urlCopy = p + "://www." + h;
//    		else
//    			urlCopy = p +"://" + h;
//	    	
	        
	        writer.print(url);
	    	
	    	
		    try
		    {
		    	
		    	url = new URL(url).getHost();
		    	
//	        	String url = list.get(l);
//	        	System.out.println(count);
//	        	
//	        	l++;
//	        	count++;
	        	
	        	List<Place> places = client.getPlacesByQuery(url, 1);
	        	
//	        	if (!url.contains("www"))
//        			writer.print("http://www." + url);
//        		else
//        			writer.print("http://" + url);
	        	
	        	
	        	if (!places.isEmpty())
	        	{
	        		
		        	Place top = (Place) places.get(0);
					
		        	//writer.print(url);
		        	
		        	
		        	String address = top.getAddress();
		        	
		        	//if ((!address.isEmpty() && address.contains("Canada")) || address.isEmpty())
		        	{
			        	String name = top.getName();
			        	writer.print("\t" + name);
			        	
			        	if (!address.isEmpty())
			        		writer.print("\t" + address + "\t");
			        	else
			        		writer.print("\t\t");
			        	
			        	
			        	Iterator<String> it = top.getTypes().iterator();
						while (it.hasNext())
						{
							writer.print(it.next());
							if (it.hasNext())
								writer.print(", ");
							
						}
						
						
						writer.print("\t" + top.getLatitude() + "\t" + top.getLongitude());
						
		        	}
		        	
		        	//writer.print("\t" + top.getLatitude() + "\t" + top.getLongitude());
	        	}
		    }
		    catch (Exception e)
     	    {
     	    	//writer.close();
     	    }
	        	
	        		
        	writer.print("\n");
	        	
	    }
        
        writer.close();
		
		
	}
	
	public static void parseWithKnowns()
	{
		//fill hashmap with known links
		LinkedList<String> known = urlFromFile("data/links.txt");
		HashMap<String, HashSet<String>> hash = new HashMap<String, HashSet<String>>();
		//PrintWriter notFound = new PrintWriter("data/notFound.txt", "UTF-8");
		
		
		for (String s : known)
		{
			if (hash.get(s) == null)
				hash.put(s, new HashSet<String>());
			else
				System.out.println(s + " already exists");
		}
		
		HashSet<String> traversed;
    	TreeSet<String> linkSet;
    	HashSet<String> hosts = new HashSet<String>();
    	
        //System.out.println("Enter a url to traverse: ");
        //String url = scan.next();
        
        LinkedList<String> list = urlFromFile("data/wineries.txt");
        int count = 0;
        
        while (!list.isEmpty())
        {
        	count++;
        	traversed = new HashSet<String>();
        	linkSet = new TreeSet<String>();
        	
        	
        	String url = list.removeFirst();

        	System.out.println("working on " + url);
        	
	        traversed.add(url);
	        
	        try
	        {
		        Document doc = Jsoup.connect(url).timeout(0).get();
		        
		        
		        Elements links = doc.select("a[href]");
		
		        //print("\nLinks: (%d)", links.size());
		        for (Element link : links) {
		            linkSet.add(link.attr("abs:href"));
		        }
		        
		        String urlCopy = url;
		        String p = new URL(urlCopy).getProtocol();
		        String h = new URL(urlCopy).getHost();
		        
		        if (!h.contains("www."))
        			urlCopy = p + "://www." + h;
        		else
        			urlCopy = p +"://" + h;
		        
		        url = new URL(url).getHost();
		        //hosts.add(url);
		        
		        while (!linkSet.isEmpty() && traversed.size() < 100)
		        {
		        	String next = linkSet.first();
		        	linkSet.remove(next);
		        	
		        	if (next != "")
		        	{
		        		//normalize url
		        		URL temp = new URL(next);
		        		next = temp.getProtocol() + "://" + temp.getHost() + temp.getPath();
		        		
		        		//internal link, dig deeper
			        	if (next.contains(url) && !traversed.contains(next))
			        	{
			        		traverse(next, linkSet, traversed);
			        	}
			        	
			        	if (traversed.add(next))
			        		System.out.println("Traversed " + next);
			        	
			        	if (!(new URL(next).getHost().contains(url.replace("www.", ""))))
			        	{
			        		String host = new URL(next).getHost();
			        		String protocol = new URL(next).getProtocol();
			        		
			        		if (!host.contains("www."))
			        			host = "http://www." + host;
			        		else
			        			host = "http://" + host;
			        		
			        		host = host.toLowerCase();
			        		
			        		hosts.add(host);
			        		
			        		if (hash.get(host) == null)
				        	{
				        		System.out.println(host + " not found");
			        			//notFound.println(urlCopy + " " + host);
				        	}
			        		else
			        			hash.get(host).add(urlCopy);
			        	}
		        	}
		        }
		       
		        
		        
		        //dump current hash to file
		        
		        PrintWriter writer = new PrintWriter("data/resultsKnowns.txt", "UTF-8");
		   
		       
		        for (int i = 0; i < known.size(); i++)
		        {	
		        	String k = known.get(i);
		        	writer.println();
		        	try
		        	{
		        	
		        	for (String s : hash.get(k))
		        		writer.print(s + " ");
		        	}
		        	catch (Exception e)
		 	        {
		 	        	System.out.println("Something happened");
		 	        	e.printStackTrace();
		 	        }
		        	
		        }
		        
		        writer.close();
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
	        
        }
        //notFound.close();
	}
	
	
    public static void parseFileForExternals()
    {
    	//traversed urls
    	HashSet<String> traversed;
    	//set of links from the given winery to be traversed
    	TreeSet<String> linkSet;
    	//found hosts of externals
    	HashSet<String> hosts = new HashSet<String>();
    	
    	
    	//store winery adjacency list
    	HashMap<String, Set<String>> wineryMap = new HashMap<String, Set<String>>();
    	
    	//store regular adjacency list
    	HashMap<String, Set<String>> externalMap = new HashMap<String, Set<String>>();
    	
    
    	
    	
    	
        
        LinkedList<String> wineries = urlFromFile("data/wineries.txt");
        int count = 0;
        
        
        
        for (int i = 0 ; i < wineries.size(); i++)
        {
        	
        	count++;
        	traversed = new HashSet<String>();
        	linkSet = new TreeSet<String>();
        	
        	
        	String url = wineries.get(i).toLowerCase();

        	System.out.println("working on " + url);
        	
	        traversed.add(url);
	        
	        try
	        {
		        Document doc = Jsoup.connect(url).timeout(0).get();
		        
		        
		        Elements links = doc.select("a[href]");
		
		        //print("\nLinks: (%d)", links.size());
		        for (Element link : links) {
		            linkSet.add(link.attr("abs:href"));
		        }
		        
		        
//		        //normalize url
//		        if (url.contains("www"))
//		        	url = "http://" + new URL(url).getHost();
//		        else
//		        	url = "http://www." + new URL(url).getHost();
		        
		        String host = new URL(url).getHost().replace("www.", "");
		        
		        while (!linkSet.isEmpty() && traversed.size() < 1000)
		        {
		        	String next = linkSet.first();
		        	linkSet.remove(next);
		        	
		        	if (!next.equals(""))
		        	{
		        		//normalize url
		        		URL temp = new URL(next.toLowerCase());
		        		next = temp.getProtocol() + "://" + temp.getHost() + temp.getPath();
		        		
		        		
		        		
			        	if (next.contains(host) && !traversed.contains(next))
			        	{
			        		traverse(next, linkSet, traversed);
			        	}
			    
			        	if (traversed.add(next))
			        		System.out.println("Traversed " + next);
			        	
			        	if (!next.contains(host))
			        	{
			        		next = new URL(next).getHost();
			        		
			        		if (!next.isEmpty())
			        		{
				        		//normalize next
				        		if (!next.contains("www"))
					        		next = "http://www." + next;
				        		else
				        			next = "http://" + next;
				        		
				        		//add to 
				        		//hosts.add(next);
				        		
				        		
				        		if (wineries.contains(next))
				        		{
				        			if (!wineryMap.containsKey(url))
				        			{
				        				wineryMap.put(url, new HashSet<String>());
				        			}
				        			wineryMap.get(url).add(next);
				        		}
				        		
				        		
				        		//do not need for winery-winery traversal
//				        		if (!externalMap.containsKey(url))
//				        			externalMap.put(url, new HashSet<String>());
//				        		
//				        		externalMap.get(url).add(next);
			        		}
			        	}
			        		
		        	}
		        }
		        PrintWriter writer;
		        
		                
//		        HashSet<String> externals = new HashSet<String>();
//		        //dump current hosts to file
//		        writer = new PrintWriter("data/edgeSet.txt", "UTF-8");
//		        
//		       
//		        for (String h : externalMap.keySet())
//		        {
//		        	for (String link : externalMap.get(h))
//		        	{
//		        		writer.println(h + " " + link);
//		        		externals.add(link);
//		        	}
//		        }
//		        
//		        writer.close();
		        
		        
		        writer = new PrintWriter("data/wineryEdgeSet.txt", "UTF-8");
		        
			       
		        for (String h : wineryMap.keySet())
		        {
		        	for (String link : wineryMap.get(h))
		        		writer.println(h + " " + link);
		        }
		        
		        writer.close();
		        
//		        //write externals to file
//		        writer = new PrintWriter("data/wieryExternals.txt", "UTF-8");
//		        
//		        for (String s : externals)
//		        {
//		        	writer.println(s);
//		        }
//		        
//		        writer.close();
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
        }
        
        

    }

    
    private static void traverse(String url, TreeSet<String> linkSet, HashSet<String> traversed) throws IOException
    {
    	try
    	{
	    	 Document doc = Jsoup.connect(url).timeout(0).get();
	         Elements links = doc.select("a[href]");
	         
	         
	         //print("\nLinks: (%d)", links.size());
	         for (Element link : links) {
	        	 String next = link.attr("abs:href");

	        	 //normalize url
	        	 URL temp = new URL(next.toLowerCase());
	        	 next = temp.getProtocol() + "://" + temp.getHost() + temp.getPath();
	        	 
	        	 if (!traversed.contains(next) && next!= "http://www.")
	        	 {
	        		 linkSet.add(next);
	        	 }
	         }
    	}
    	catch (Exception e)
    	{
    		//everything else happened
    	}
    }

    private static LinkedList<String> urlFromFile(String filename)
    {
    	LinkedList<String> list = new LinkedList<String>();
    	
    	FileReader file = null;
		try {
			file = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}

		Scanner scan = new Scanner(file);

		while (scan.hasNextLine()) {
			list.add(scan.nextLine().toLowerCase());
		}
		try {
			scan.close();
			file.close();
		} catch (IOException e) {
			System.out.println("File " + filename + " could not be found.");
			e.printStackTrace();
		}
		
		return list;
    }
    
    

}