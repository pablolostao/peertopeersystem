import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;


public class Indexer {
    //Index. It is a hashmap whose key is the name of the file and de value is a hashset of the peers with that file
    public ConcurrentHashMap<String, HashSet<String>> fileToClientIds = new ConcurrentHashMap<String, HashSet<String>>();

    public Indexer(Integer port) throws NumberFormatException, IOException
    {
        ServerSocket serverSocket=null;
        Socket socket = null;
        try{
            //Create a server socket to listen
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening for requests on port "+port.toString()+"...\n");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        //Always listening
        while(true)
        {
            //For each new connection we create a thread Server, that manages request for one peer
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
        //General index
        private ConcurrentHashMap<String, HashSet<String>> fileToClientIds = null;
        //Socket for the peer
        private Socket socket= null;
        //ID of the peer (address:port)
        private String id= null;

        public Server(Socket socket,ConcurrentHashMap<String, HashSet<String>> fileToClientId){
            this.socket=socket;
            this.fileToClientIds = fileToClientId;
        }

        //Aux method to create de ID of the peer (address:port)
        private String getId(Integer port){
            return socket.getInetAddress() +":"+port.toString();
        }

        //Aux method to register a file in local index
        private void registerFile(String name){
            if (!fileToClientIds.containsKey(name)) {
                fileToClientIds.put(name,new HashSet<String>());
            }
            fileToClientIds.get(name).add(this.id);
        }

        //Thread itself
        public void run() {
            try {
                //Initialization and welcome messages
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                Integer port = (Integer) in.readObject();
                this.id = getId(port);
                System.out.println("New connection with client at " + this.id);
                out.writeObject("Connection established. Your ID is "+this.id);
                //We listed and can manage four types of requests (REGISTER_FOLDER,REGISTER,UNREGISTER,LOOKFOR)
                while (true) {
                    IndexerRequest request = (IndexerRequest) in.readObject();
                    File file = null;
                    File[] files = null;
                    switch (request.getRequestType()){
                        //Request to index files of the peer's folder at the beginning
                        case REGISTER_FOLDER:
                            files = (File[]) request.getRequestData();
                            for (File child : files) {
                                registerFile(child.getName());
                            }
                            out.writeObject("Folder registered successfully for client "+this.id);
                            break;
                        //Request to index a new file (if it is created in the peer's folder)
                        case REGISTER:
                            file = (File) request.getRequestData();
                            registerFile(file.getName());
                            out.writeObject("File "+file.getName()+" registered successfully for client "+this.id);
                            break;
                        //Request to unregister a file (if it is deleted in the peer's folder)
                        case UNREGISTER:
                            file = (File) request.getRequestData();
                            fileToClientIds.get(file.getName()).remove(this.id);
                            out.writeObject("File "+file.getName()+" unregistered successfully for client "+this.id);
                            break;
                        //Request to list all the peers that own a given file
                        case LOOKFOR:
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
