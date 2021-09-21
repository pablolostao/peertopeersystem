import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;


public class Indexer {

    public static ConcurrentHashMap<String, HashSet<String>> fileToClientIds = new ConcurrentHashMap<String, HashSet<String>>();

    public Indexer(Integer port) throws NumberFormatException, IOException
    {
        ServerSocket serverSocket=null;
        Socket socket = null;
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening for requests on port "+port.toString()+"...\n");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        while(true)
        {
            try{
                socket = serverSocket.accept();
            }
            catch(IOException e)
            {
                System.out.println("I/O error: " +e);
            }
            new Server(socket,fileToClientIds).start();
        }
    }


    private class Server extends Thread{

        private static ConcurrentHashMap<String, HashSet<String>> fileToClientIds = null;
        private Socket socket= null;
        private String id= null;

        public Server(Socket socket,ConcurrentHashMap<String, HashSet<String>> fileToClientId){
            this.socket=socket;
            this.fileToClientIds = fileToClientId;
            this.id = getId(this.socket);
            System.out.println("New connection with client at " + this.id);
        }

        private String getId(Socket socket){
            return socket.getInetAddress() +":"+socket.getPort();
        }

        public void run() {
            try {
                // Initializing output stream using the socket's output stream
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                // Initializing input stream using the socket's input stream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                out.writeObject("Connection established. Your ID is "+this.id);
                while (true) {
                    // Read the request object received from the Peer
                    IndexerRequest request = (IndexerRequest) in.readObject();
                    switch (request.getRequestType()){
                        case REGISTER:
                            File file = (File) request.getRequestData();
                            if (!fileToClientIds.contains(file.getName())) {
                                fileToClientIds.put(file.getName(),new HashSet<String>());
                            }
                            fileToClientIds.get(file.getName()).add(this.id);
                            out.writeObject("File "+file.getName()+" registered successfully for client "+this.id);
                    }

                }
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
            }
        }
    }
}
