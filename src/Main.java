import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    private static final int DEFAULT_INDEXER_PORT = 30301;
    private static final int DEFAULT_PEER_PORT = 30302;

    public static void main(String args[]) throws Exception{
        System.out.println("Welcome. To create an indexer server type 'indexer' or type 'peer' to create a peer (client and server).");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String type = reader.readLine().trim().toLowerCase();
        if(type.equals("indexer")){
            Integer port = DEFAULT_INDEXER_PORT;
            Boolean retry = true;
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
            Indexer s = new Indexer(port);
        }
        else if(type.equals("peer")){
            Integer peer_port = DEFAULT_PEER_PORT;
            Integer indexer_port = DEFAULT_INDEXER_PORT;
            Boolean retry = true;
            while (retry){
                try{
                    System.out.println("Choose client port: (Default 30302)");
                    String port_s = reader.readLine().trim();
                    if (!port_s.equals("")){
                        peer_port = Integer.parseInt(port_s);
                    }
                    System.out.println("Choose server port: (Default 30301)");
                    port_s = reader.readLine().trim();
                    if (!port_s.equals("")){
                        indexer_port = Integer.parseInt(port_s);
                    }
                    retry = false;
                }catch (NumberFormatException e){
                    System.out.println("Port number not valid");
                }
            }
            Peer c = new Peer(peer_port,indexer_port);
        }
        else{
            System.out.println("You can only choose 'indexer' or 'peer'");
        }
    }
}
