
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
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
    	HashSet<String> traversed = new HashSet<String>();
    	TreeSet<String> linkSet = new TreeSet<String>();
    	HashSet<String> hosts = new HashSet<String>();
    	
        System.out.println("Enter a url to traverse: ");
        String url = scan.next();
        traversed.add(url);
        
        
        Document doc = Jsoup.connect(url).timeout(0).get();
        Elements links = doc.select("a[href]");

        //print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            linkSet.add(link.attr("abs:href"));
        }
        
        
        url = new URL(url).getHost();
        hosts.add(url);
        
        while (!linkSet.isEmpty())
        {
        	String next = linkSet.first();
        	linkSet.remove(next);
        	if (next != "")
        	{
	        	if (next.contains(url) && !traversed.contains(next))
	        	{
	        		//System.out.println(next);
	        		traverse(next, linkSet, traversed);
	        	}
	        	
//	        	if (!next.contains(url))
//	        		System.out.println(next);
	        	traversed.add(next);
	        	
	        	hosts.add(new URL(next).getHost());
        	}
        }
        
        System.out.println();
        for (String h : hosts)
        	System.out.println(h);

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
    	

    }


}