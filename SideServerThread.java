import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SideServerThread implements Runnable {
    private final Socket socket;
    private final AllUsers allUsers;

    SideServerThread(Socket socket, AllUsers allUsers) {
        this.socket = socket;
        this.allUsers = allUsers;
    }

    @Override
    public void run() {
        String loginPhoneNumber="";//phoneNumber of the user who is logged in
        DataInputStream in;
        DataOutputStream out;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
           outerLoop: while (true) {
                StringBuilder request = new StringBuilder();
                int c;
                while ((c = in.read()) != 0) {
                    if (c==-1){
                        break outerLoop;
                    }
                    request.append((char) c);
                }
                String mainRequest = request.toString();
                if (mainRequest.startsWith("login")) {
                    String[] loginRequest = mainRequest.split(":");
                    String phoneNumber = loginRequest[1];
                    String password = loginRequest[2];
                    if (allUsers.users.containsKey(phoneNumber)) {
                        if (allUsers.users.get(phoneNumber).password.equals(password)) {
                            out.writeBytes("login success");
                            System.out.println("login success");
                            loginPhoneNumber=phoneNumber;
                        } else {
                            out.writeBytes("login failed");
                            System.out.println("login failed");
                        }
                    } else {
                        out.writeBytes("login failed");
                        System.out.println("login failed");
                    }
                } else if (mainRequest.startsWith("register")) {
                    String[] registerRequest = mainRequest.split(":");
                    String username = registerRequest[1];
                    String phoneNumber = registerRequest[2];
                    String password = registerRequest[3];

                    String email = "";
                    if(registerRequest.length>4)
                        email = registerRequest[4];
                    String place="";
                    if (registerRequest.length>5)
                     place= registerRequest[5];
                    if (allUsers.users.containsKey(phoneNumber)) {
                        out.writeBytes("register failed");
                        System.out.println("register failed");
                    } else {
                        allUsers.users.put(phoneNumber, new UsersClass(phoneNumber, password, username, email, place));
                        out.writeBytes("register success");
                        System.out.println("register success");
                        loginPhoneNumber=phoneNumber;
                    }

                } else if (mainRequest.startsWith("edit")) {
                    String[] getRequest = mainRequest.split(":");

                    String username = "";
                    if (getRequest.length > 1)
                        username = getRequest[1];
                    String password = "";
                    if(getRequest.length>2)
                        password = getRequest[2];
                    String email="" ;
                    if (getRequest.length>3)
                        email=getRequest[3];
                    String store="";
                    if (getRequest.length>4)
                        store=getRequest[4];
                    if (allUsers.users.containsKey(loginPhoneNumber)) {
                        if (!username.equals(""))
                        allUsers.users.get(loginPhoneNumber).username = username;
                        if (!password.equals(""))
                        allUsers.users.get(loginPhoneNumber).password = password;
                        if (!email.equals(""))
                        allUsers.users.get(loginPhoneNumber).email = email;
                        if (!store.equals(""))
                        allUsers.users.get(loginPhoneNumber).storeAddress = store;
                        out.writeBytes("edit success");
                        System.out.println("edit success");
                    } else {
                        out.writeBytes("edit failed");
                        System.out.println("edit failed");
                    }
                } else if (mainRequest.startsWith("profile")) {
//                    out.writeBytes();
                } else if (mainRequest.startsWith("delete")) {
                    String[] deleteRequest = mainRequest.split(" ");
                    String username = deleteRequest[1];
                    String password = deleteRequest[2];
                    if (username.equals("admin") && password.equals("admin")) {
                        out.writeUTF("delete success");
                    } else {
                        out.writeUTF("delete failed");
                    }
                }
                System.out.println(allUsers.users);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("closing socket");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
