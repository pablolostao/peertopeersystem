import java.io.*;
import java.net.Socket;
import java.util.HashSet;

public class Peer {
    Socket socket;
    public Peer(Integer peer_port,Integer server_port) throws NumberFormatException, IOException
    {
        new Peer._Client(peer_port,server_port).start();
        /*ServerSocket serverSocket=null;
        Socket socket = null;
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Client started. Listening for requests on port "+port.toString()+"...\n");
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
            new Client.Client(socket).start();
        }*/

    }
    private static class _Client extends Thread{
        Integer indexer_port;
        Integer peer_port;
        public _Client(Integer peer_port,Integer indexer_port){
            this.indexer_port =indexer_port;
            this.peer_port =peer_port;
        }
        public void run() {
            try{
                Socket socket = new Socket("localhost",this.indexer_port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                out.writeObject(this.peer_port);
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                String response = (String) in.readObject();
                System.out.println(response);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                int choice=0;
                while(true){
                    Boolean retry = true;
                    while (retry){
                        try{
                            System.out.println("Choose action");
                            System.out.println("[1] To register files");
                            System.out.println("[2] To unregister files");
                            System.out.println("[3] To look for file");
                            String line = reader.readLine().trim();
                            choice = Integer.parseInt(line);
                            retry = false;
                        }catch (NumberFormatException e){
                            System.out.println("Choice not valid");
                        }
                    }
                    String path = null;
                    File file = null;
                    IndexerRequest request = null;
                    switch (choice){
                        case 1:
                            System.out.println("\nEnter path of the file you want to register:");
                            path = reader.readLine().trim();
                            file = new File(path);
                            request = new IndexerRequest();
                            request.setRequestType(IndexerRequestType.REGISTER);
                            request.setRequestData(file);
                            out.writeObject(request);
                            response = (String) in.readObject();
                            System.out.println(response);
                            break;
                        case 2:
                            System.out.println("\nEnter path of the file you want to unregister:");
                            path = reader.readLine().trim();
                            file = new File(path);
                            request = new IndexerRequest();
                            request.setRequestType(IndexerRequestType.UNREGISTER);
                            request.setRequestData(file);
                            out.writeObject(request);
                            response = (String) in.readObject();
                            System.out.println(response);
                            break;
                        case 3:
                            System.out.println("\nEnter the name of the file you are looking for:");
                            String name = reader.readLine().trim();
                            request = new IndexerRequest();
                            request.setRequestType(IndexerRequestType.LOOKFOR);
                            request.setRequestData(name);
                            out.writeObject(request);
                            HashSet<String> set = (HashSet<String>) in.readObject();
                            if (set==null || set.isEmpty()){
                                System.out.println("File does not exist in the server");
                            }else{
                                System.out.println("You can find the file "+name+" in the following peers: "+set.toString());
                            }
                            break;
                    }

                }

            }catch (Exception e){
                System.err.println(e.getMessage());
            }

        }

    }
/*    private class Server extends Thread{
        private Socket socket= null;

        public Server(Socket socket){
            this.socket=socket;
            System.out.println("New connection with peer at " + socket.getInetAddress());
        }
        public void run() {
            try {
                // Initializing output stream using the socket's output stream
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                // Initializing input stream using the socket's input stream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                String clientIp = socket.getInetAddress().getHostAddress();
                out.writeObject("Your IP is: " + clientIp);
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
            }
        }
    }*/
}
