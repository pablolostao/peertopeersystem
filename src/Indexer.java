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
        }

        private String getId(Integer port){
            return socket.getInetAddress() +":"+port.toString();
        }

        public void run() {
            try {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Integer port = (Integer) in.readObject();
                this.id = getId(port);
                System.out.println("New connection with client at " + this.id);
                out.writeObject("Connection established. Your ID is "+this.id);
                while (true) {
                    IndexerRequest request = (IndexerRequest) in.readObject();
                    File file = null;
                    switch (request.getRequestType()){
                        case REGISTER:
                            System.out.println("registering ");
                            file = (File) request.getRequestData();
                            if (!fileToClientIds.contains(file.getName())) {
                                fileToClientIds.put(file.getName(),new HashSet<String>());
                            }
                            fileToClientIds.get(file.getName()).add(this.id);
                            out.writeObject("File "+file.getName()+" registered successfully for client "+this.id);
                            break;
                        case UNREGISTER:
                            System.out.println("unregistering ");
                            file = (File) request.getRequestData();
                            fileToClientIds.get(file.getName()).remove(this.id);
                            out.writeObject("File "+file.getName()+" unregistered successfully for client "+this.id);
                            break;
                        case LOOKFOR:
                            System.out.println("looking for ");
                            String name = (String) request.getRequestData();
                            HashSet<String> set = fileToClientIds.get(name);
                            out.reset();
                            out.writeObject(set);
                            break;
                    }
                    System.out.println(fileToClientIds.toString());

                }
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
            }
        }
    }
}
