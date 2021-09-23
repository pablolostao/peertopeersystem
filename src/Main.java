import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    //Default ports for indexer and peer
    private static final int DEFAULT_INDEXER_PORT = 30301;
    private static final int DEFAULT_PEER_PORT = 30302;

    public static void main(String args[]) throws Exception{
        //We show welcome message and ask the user if he wants to create an indexer server or a peer
        System.out.println("Welcome. To create an indexer server type 'indexer' or type 'peer' to create a peer (client and server).");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String type = reader.readLine().trim().toLowerCase();
        //Case of indexer server
        if(type.equals("indexer")){
            Integer port = DEFAULT_INDEXER_PORT;
            Boolean retry = true;
            //Until the port entered is correct, retry. If it is empty we use default port.
            while (retry){
                try{
                    System.out.println("Choose port: (Default 30301)");
                    String port_s = reader.readLine().trim();
                    if (port_s.equals("")){
                        retry=false;
                    }else{
                        port = Integer.parseInt(port_s);
                        retry = false;
                    }

                }catch (NumberFormatException e){
                    System.out.println("Port number not valid");
                }
            }
            //Create an indexer server with selected port
            Indexer s = new Indexer(port);
        }
        //Case of peer
        else if(type.equals("peer")){
            Integer peer_port = DEFAULT_PEER_PORT;
            Integer indexer_port = DEFAULT_INDEXER_PORT;
            Boolean retry = true;
            //Until the port entered is correct, retry. If it is empty we use default port.
            while (retry){
                try{
                    //Specify where the peer is going to listen (file requests)
                    System.out.println("Choose peer port: (Default 30302)");
                    String port_s = reader.readLine().trim();
                    if (!port_s.equals("")){
                        peer_port = Integer.parseInt(port_s);
                    }
                    //Specify where the indexer is
                    System.out.println("Choose indexer port: (Default 30301)");
                    port_s = reader.readLine().trim();
                    if (!port_s.equals("")){
                        indexer_port = Integer.parseInt(port_s);
                    }
                    retry = false;
                }catch (NumberFormatException e){
                    System.out.println("Port number not valid");
                }
            }
            //Create a peer with selected ports
            Peer c = new Peer(peer_port,indexer_port);
        }
        //Type not valid
        else{
            System.out.println("You can only choose 'indexer' or 'peer'");
        }
    }
}
