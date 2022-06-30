
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class MainServer {

    public static void main(String[] args)  {

        ServerSocket serverSocket = null;
        Logger logger=Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        try {
             serverSocket=new ServerSocket(8080);
        } catch (IOException e) {
            logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }
        Gson gson=new Gson();
        AllUsers allUsers = null;
        AllGoods allGoods = null;
        try(FileReader reader=new FileReader("src/Data/users.json")){
                allUsers = gson.fromJson(reader, AllUsers.class);
            if (allUsers==null){
                allUsers=new AllUsers();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }catch (NullPointerException e){
            logger.log(Level.FINE, Arrays.toString(e.getStackTrace()));
            allUsers=new AllUsers();
        }
        try(FileReader reader=new FileReader("src/Data/goods.json")){
            allGoods = gson.fromJson(reader, AllGoods.class);
            if (allGoods==null){
                allGoods=new AllGoods();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
        }catch (NullPointerException e){
            logger.log(Level.FINE, Arrays.toString(e.getStackTrace()));
            allGoods = new AllGoods();
        }

        AllUsers finalAllUsers = allUsers;
        AllGoods finalAllGoods = allGoods;
        Runtime.getRuntime().addShutdownHook((new Thread(()->{
            try (FileWriter fileWriter= new FileWriter("src/Data/users.json");FileWriter fileWriter2= new FileWriter("src/Data/goods.json")) {
                gson.toJson(finalAllUsers,fileWriter);
                gson.toJson(finalAllGoods,fileWriter2);
            } catch (IOException e) {
                logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
            }
        })));
//        allUsers.users.get("09122007644").cartGoods.goods.put(allGoods.goods.get("Lenovo").title,allGoods.goods.get("Lenovo"));

        while(true){
            try {
                System.out.println("Waiting for connection...");
                assert serverSocket != null;
                Socket anotherClientSocket=serverSocket.accept();
                System.out.println("accepted");
                new Thread(new SideServerThread(anotherClientSocket,allUsers,allGoods)).start();  //Creating another thread for each client

            } catch (IOException e) {
                logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
            }
            finally {
                try(FileWriter writer=new FileWriter("src/Data/users.json")){
                    writer.write(gson.toJson(allUsers));
                    writer.flush();
                } catch (IOException e) {
                    logger.log(Level.WARNING, Arrays.toString(e.getStackTrace()));
                }
            }
        }


    }
}
