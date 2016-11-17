package clientserver;

import stats.ClientServerStats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
//test again to see if working james
/**
 * Handles an incoming server request from a client.
 */
public class ServerRequestHandler implements Runnable {

    private final Socket clientRequestSocket;
    private final ClientServerStats stats;
    private PrintWriter out; // writers (for writing back to the machine and to destination)
    private BufferedReader in; // reader (for reading from the machine connected to)
    private String ChapterRequested; // communication strings

    public ServerRequestHandler(Socket clientRequestSocket, ClientServerStats stats) {
        this.clientRequestSocket = clientRequestSocket;
        this.stats = stats;

      //(A) Receive a TCP connection,
     

    @Override
    public void run() {
        //TODO Handle incoming request here
        // TODO: Reference the filestoserve folder created in ClientServer main()
    	   try 
       	{    	
       	   out = new PrintWriter(clientRequestSocket.getOutputStream(), true);
   		   in = new BufferedReader(new InputStreamReader(clientRequestSocket.getInputStream()));
       
   		}
       	
   		catch (IOException e) 
       	{
               System.err.println("Client failed to connect.");            
           }
       }
    
    	// Initial sends/receives
    	try
		{
	    		
    	//(B) read the chapter number from that connection, 
    		
        ChapterRequested = in.readLine(); // Get requested chapter 
		System.out.println("Getting Chapter: " + ChapterRequested);
		out.println("Connected to the client seeking chapter"+ ChapterRequested ); // confirmation of connection
		
	    //wait 1 second 
		try{
    		Thread.currentThread();
			Thread.sleep(1000); 
	   }
		catch(InterruptedException ie){
		System.out.println("Thread interrupted");
		}

    	

    	
    	//read the chapter from the file system, 
		
    	//send the contents of that chapter through the connection.

    }catch (IOException e) {
        System.err.println("Could not listen to socket.");}
        //System.exit(1);
    	
    	
    	
   }
}
