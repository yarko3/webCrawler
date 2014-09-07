
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
import java.util.PriorityQueue;
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
		//parseFileForExternals();
		tagFile();
		//parseFile("data/wineries.txt");
		//parseWithKnowns();
		//buildEdgeSet();
		//rankDegrees();
		//partitionWineries();
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
		LinkedList<String> list = urlFromFile("data/wineries.txt");
		
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
			        	
			        	//for the winery province tab
			        	writer.print("\t");
			        	
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
	
	//USED FOR FINAL SCRUBBING
    public static void parseFileForExternals()
    {
    	//traversed urls
    	HashSet<String> traversed;
    	//set of links from the given winery to be traversed
    	TreeSet<myURL> linkSet;
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
        	linkSet = new TreeSet<myURL>();
        	
        	
        	String url = wineries.get(i).toLowerCase();

        	System.out.println("working on " + url);
        	
	        traversed.add(url);
	        
	        try
	        {
		        Document doc = Jsoup.connect(url).timeout(0).get();
		        
		        
		        Elements links = doc.select("a[href]");
		
		        //print("\nLinks: (%d)", links.size());
		        for (Element link : links) {
		            linkSet.add(new myURL(link.attr("abs:href"), 0));
		        }
		        
		        
//		        //normalize url
//		        if (url.contains("www"))
//		        	url = "http://" + new URL(url).getHost();
//		        else
//		        	url = "http://www." + new URL(url).getHost();
		        
		        String host = new URL(url).getHost().replace("www.", "");
		        
		        while (!linkSet.isEmpty() && traversed.size() < 1000)
		        {
		        	myURL n = linkSet.first();
		        	
		        	String next = n.url;
		        	
		        	int depth = n.depth;
		        	
		        	linkSet.remove(n);
		        	
		        	//link is contains something
		        	if (!next.equals("") || !next.equals(" "))
		        	{
		        		//normalize url
		        		URL temp = new URL(next.toLowerCase());
		        		next = temp.getProtocol() + "://" + temp.getHost() + temp.getPath();
		        		
		        		
		        		
			        	if (next.contains(host) && !traversed.contains(next))
			        	{
			        		traverse(next, linkSet, traversed, depth);
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
				        		if (!externalMap.containsKey(url))
				        			externalMap.put(url, new HashSet<String>());
				        		
				        		externalMap.get(url).add(next);
			        		}
			        	}
			        		
		        	}
		        }
		        PrintWriter writer;
		        
		                
		        HashSet<String> externals = new HashSet<String>();
		        //dump current hosts to file
		        writer = new PrintWriter("data/edgeSet.txt", "UTF-8");
//		        
//		       
		        for (String h : externalMap.keySet())
		        {
		        	for (String link : externalMap.get(h))
		        	{
		        		writer.println(h + " " + link);
		        		externals.add(link);
		        	}
		        }
		        
		        writer.close();
		        
		        
		        writer = new PrintWriter("data/wineryEdgeSet.txt", "UTF-8");
		        
			       
		        for (String h : wineryMap.keySet())
		        {
		        	for (String link : wineryMap.get(h))
		        		writer.println(h + " " + link);
		        }
		        
		        writer.close();
		        
		        //write externals to file
		        writer = new PrintWriter("data/wieryExternals.txt", "UTF-8");
		        
		        for (String s : externals)
		        {
		        	writer.println(s);
		        }
//		        
		        writer.close();
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
        }
        
        

    }

    
    private static void traverse(String url, TreeSet<myURL> linkSet, HashSet<String> traversed, int depth) throws IOException
    {
    	//choose depth bound to traverse
    	//if (depth < 1)
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
		        	 
		        	 myURL n = new myURL(next, depth+1);
		        	 
		        	 if (!traversed.contains(next) && next!= "http://www.")
		        	 {
		        		 linkSet.add(n);
		        	 }
		         }
	    	}
	    	catch (Exception e)
	    	{
	    		//everything else happened
	    	}
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
    
    private static void rankDegrees() throws FileNotFoundException
    {
    	HashMap<String, Integer> inDegree = new HashMap<String, Integer>();
    	HashMap<String, Integer> outDegree = new HashMap<String, Integer>();
    	
    	FileReader file = new FileReader("data/wineryEdgeList.txt");
    	
    	Scanner scan = new Scanner(file);
    	
    	while (scan.hasNextLine())
    	{
    		String a = scan.next();
    		String b = scan.next();
    		
    		//update outDegree
    		if (!outDegree.containsKey(a))
    		{
    			outDegree.put(a, 1);
    		}
    		else
    		{
    			outDegree.put(a, outDegree.get(a) + 1);
    		}
    		
    		//update inDegree

    		if (!inDegree.containsKey(b))
    		{
    			inDegree.put(b, 1);
    		}
    		else
    		{
    			inDegree.put(b, inDegree.get(b) + 1);
    		}
    	}
    	
    	scan.close();
    	
    	HashMap<Integer, LinkedList<String>> inDegHash = new HashMap<Integer, LinkedList<String>>();
    	HashMap<Integer, LinkedList<String>> outDegHash = new HashMap<Integer, LinkedList<String>>();
    	
    	//place vertices into hashmap
    	for (String s : inDegree.keySet())
    	{
    		int degree = inDegree.get(s);
    		
    		if (!inDegHash.containsKey(degree))
    		{
    			inDegHash.put(degree, new LinkedList<String>());
    		}
    		
    		inDegHash.get(degree).add(s);
    		
    	}
    	
    	for (String s : outDegree.keySet())
    	{
    		int degree = outDegree.get(s);
    		
    		if (!outDegHash.containsKey(degree))
    		{
    			outDegHash.put(degree, new LinkedList<String>());
    		}
    		
    		outDegHash.get(degree).add(s);
    		
    	}
    	
    	PriorityQueue<Integer> inQueue = new PriorityQueue<Integer>();
    	PriorityQueue<Integer> outQueue = new PriorityQueue<Integer>();
    	
    	inQueue.addAll(inDegHash.keySet());
    	outQueue.addAll(outDegHash.keySet());
    	
    	System.out.println("\nOrdered Second Column Degrees: ");
    	while (!inQueue.isEmpty())
    	{
    		int deg = inQueue.remove();
    		
    		//System.out.println("Degree : " + deg);
    		for (String s : inDegHash.get(deg + "\t" + deg))
    		{
    			System.out.println(s);
    		}
    	}
    	
    	System.out.println("Ordered First Column Degrees: ");
    	while (!outQueue.isEmpty())
    	{
    		int deg = outQueue.remove();
    		//System.out.println("Degree : " + deg);
    		for (String s : outDegHash.get(deg))
    		{
    			System.out.println(deg + "\t" + s);
    		}
    	}
    	
    }
    
    private static void partitionWineries() throws FileNotFoundException
    {
    	FileReader file = new FileReader("data/edgeList.txt");
    	
    	Scanner scan = new Scanner(file);
    	
    	HashMap<String, HashSet<String>> externalEdgeSet = new HashMap<String, HashSet<String>>();
    	HashMap<String, HashSet<String>> wineryEdgeSet = new HashMap<String, HashSet<String>>();
    	
    	
    	HashSet<String> wineries = new HashSet<String>();
    	
    	while (scan.hasNextLine())
    	{
    		String a = scan.next();
    		String b = scan.next();
    		
    		if (!externalEdgeSet.containsKey(b))
    		{
    			externalEdgeSet.put(b, new HashSet<String>());
    		}
    		if (!wineryEdgeSet.containsKey(a))
    		{
    			wineryEdgeSet.put(a, new HashSet<String>());
    		}
    		
    		wineryEdgeSet.get(a).add(b);
    		externalEdgeSet.get(b).add(a);
    		wineries.add(a);
    	}
    	
    	//print all the wineries linked to Twitter
    	System.out.println("Linked to Twitter: ");
    	for (String s : externalEdgeSet.get("http://www.facebook.com"))
    	{
    		System.out.println(s);
    	}
    	int wineryCount = wineries.size();
    	
    	wineries.removeAll(externalEdgeSet.get("http://www.facebook.com"));
    	
    	System.out.println("\nAll others: ");
    	for (String s : wineries)
    	{
    		System.out.println(s);
    	}
    	
    	//find average external link
    	double twitterAverage = 0;
    	double nonAverage = 0;
    	int twitterCount = 0;
    	int nonCount = 0;
    	
    	for (String winery : wineryEdgeSet.keySet())
    	{
    		if (wineries.contains(winery))
    		{
    			nonAverage += wineryEdgeSet.get(winery).size();
    			nonCount++;
    		}
    		else
    		{
    			twitterAverage += wineryEdgeSet.get(winery).size();
    			twitterCount++;
    		}
    	}
    	
//    	twitterAverage = twitterAverage / (wineryCount - wineries.size());
//    	nonAverage = nonAverage / wineries.size();
    	
    	twitterAverage /= twitterCount;
    	nonAverage /= nonCount;
    	
    	System.out.println("Average twitter winery external count: " + twitterAverage);
    	System.out.println("Average non twitter winery external count: " + nonAverage);
    	
    	
    }
    
    static class myURL implements Comparable<myURL>
    {
    	@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + depth;
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			myURL other = (myURL) obj;
			if (depth != other.depth)
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}

		String url;
    	int depth;
    	
    	myURL(String u, int d)
    	{
    		url = u;
    		depth = d;
    	}

		@Override
		public int compareTo(myURL arg0) {
			if (equals(arg0))
				return 0;
			else
				return -1;
		}
    }

}