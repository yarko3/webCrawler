
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Crawl {
	static Scanner scan = new Scanner(System.in);
	
    public static void main(String[] args) throws IOException 
    {
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
		        		
		        		System.out.println("Traversed " + next);
		        		
			        	if (next.contains(url) && !traversed.contains(next))
			        	{
			        		traverse(next, linkSet, traversed);
			        	}
			    
			        	traversed.add(next);
			        	
			        	hosts.add(new URL(next).getHost());
		        	}
		        }
		        
		        //dump current hosts to file
		        PrintWriter writer = new PrintWriter("data/results.txt", "UTF-8");
		        
		       
		        for (String h : hosts)
		        	writer.println(h);
		        
		        writer.close();
	        }
	        catch (HttpStatusException e)
	    	{
	    		//Oh no, 404
	    	}
	    	catch (UnsupportedMimeTypeException e)
	    	{
	    		//Oh no, some other exception
	    	}
	        catch (UnknownHostException e)
	        {
	        	//website is down, yo
	        }
	        catch (MalformedURLException e)
	    	{
	        	//website name is wrong
	    	}
	        catch (Exception e)
	        {
	        	//everything else happened
	        }
        }
        

    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
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
	        	 URL temp = new URL(next);
	        	 next = temp.getProtocol() + "://" + temp.getHost() + temp.getPath();
	        	 
	        	 if (!traversed.contains(next) && (next.startsWith("http://") || next.startsWith("https://")))
	        	 {
	        		 linkSet.add(link.attr("abs:href"));
	        	 }
	         }
    	}
    	catch (HttpStatusException e)
    	{
    		//Oh no, 404
    	}
    	catch (UnsupportedMimeTypeException e)
    	{
    		//Oh no, some other exception
    	}
    	 catch (UnknownHostException e)
        {
        	//website is down, yo
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

		while (scan.hasNext()) {
			list.add(scan.next());
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