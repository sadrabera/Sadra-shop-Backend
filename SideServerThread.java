import Enums.SideCategories;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.gson.Gson;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SideServerThread implements Runnable {
    private final Socket socket;
    private final AllUsers allUsers;
    private final AllGoods allGoods;

    SideServerThread(Socket socket, AllUsers allUsers,AllGoods allGoods) {
        this.socket = socket;
        this.allUsers = allUsers;
        this.allGoods = allGoods;
    }

    @Override
    public void run() {
        Gson gson=new Gson();
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
               System.out.println(mainRequest);
                if (mainRequest.startsWith("login")) {
                    String[] loginRequest = mainRequest.split(":");
                    String phoneNumber = loginRequest[1];
                    String password = loginRequest[2];
                    if (allUsers.users.containsKey(phoneNumber)) {
                        if (allUsers.users.get(phoneNumber).password.equals(password)) {
                            System.out.println("login success");
                            out.write("login success".getBytes(StandardCharsets.UTF_8));

                            loginPhoneNumber=phoneNumber;
                            System.out.println(loginPhoneNumber);
                        } else {
                            out.write("login failed".getBytes(StandardCharsets.UTF_8));
                            System.out.println("login failed");
                            System.out.println(allUsers.users.get(phoneNumber).password);
                        }
                    } else {
                        out.write("login failed".getBytes(StandardCharsets.UTF_8));
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
                        out.write("register failed".getBytes(StandardCharsets.UTF_8));

                        System.out.println("register failed");
                    } else {
                        allUsers.users.put(phoneNumber, new UsersClass(phoneNumber, username, password, email, place));
                        out.write("register success".getBytes(StandardCharsets.UTF_8));

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
                        out.write("edit success".getBytes(StandardCharsets.UTF_8));
                        System.out.println("edit success");
                    } else {
                        out.write("edit failed".getBytes(StandardCharsets.UTF_8));
                        System.out.println("edit failed");
                    }
                }
                else if(mainRequest.equals("Am I logged in?")){
                    if(loginPhoneNumber.equals("")){
                        out.write("no".getBytes(StandardCharsets.UTF_8));
                    }
                    else{
                        out.write("yes".getBytes(StandardCharsets.UTF_8));
                    }
                }
                else if (mainRequest.equals("profile")) {
                    out.write(gson.toJson(allUsers.users.get(loginPhoneNumber)).getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    System.out.println(gson.toJson(allUsers.users.get(loginPhoneNumber)));
                } else if (mainRequest.startsWith("get goods")) {
                    String[] getRequest=mainRequest.split(":");
                    String category=getRequest[1];
                    HashMap<String,Good> sendGoods=new HashMap<>();
                    for(String good:allGoods.goods.keySet()){
                        if(allGoods.goods.get(good).sideCategory.toString().equals(category)){
                            sendGoods.put(good,allGoods.goods.get(good));
                        }
                    }
                    out.write((gson.toJson(sendGoods)).getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
                else if(mainRequest.startsWith("add to cart")){
                    String title=mainRequest.split(":")[1];
                    if(allGoods.goods.get(title).numberOfExistingGoods>0&&!loginPhoneNumber.equals("")&&!allGoods.goods.get(title).owner.equals(loginPhoneNumber)){
                        allGoods.goods.get(title).numberOfExistingGoods--;
                        allUsers.users.get(loginPhoneNumber).cartGoods.goods.put(title,allGoods.goods.get(title));
                        out.write("added".getBytes(StandardCharsets.UTF_8));
                    }
                    else{
                        out.write("failed".getBytes(StandardCharsets.UTF_8));
                    }

                }else if(mainRequest.startsWith("get cart")) {
                    out.write(gson.toJson(allUsers.users.get(loginPhoneNumber)).getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }else if(mainRequest.startsWith("remove from cart")) {
                    String title = mainRequest.split(":")[1];
                    if(title.contains("cart")){
                        allUsers.users.get(loginPhoneNumber).cartGoods.goods.remove(title.substring(0,title.indexOf("get")));
                        allGoods.goods.get(title.substring(0,title.indexOf("get"))).numberOfExistingGoods++;
                    }
                    else{
                        allUsers.users.get(loginPhoneNumber).cartGoods.goods.remove(title);
                        allGoods.goods.get(title.substring(0,title.indexOf("get"))).numberOfExistingGoods++;
                    }
                }else if(mainRequest.startsWith("add address")) {
                    String address = mainRequest.split(":")[1];
                    allUsers.users.get(loginPhoneNumber).address.add(address);
                    System.out.println("add address");
                }else if(mainRequest.startsWith("order")) {
                    allUsers.users.get(loginPhoneNumber).completedOrders.goods.putAll(allUsers.users.get(loginPhoneNumber).cartGoods.goods);
                    allUsers.users.get(loginPhoneNumber).cartGoods.goods.clear();
                }else if(mainRequest.startsWith("add to liked")){
                    String title=mainRequest.split(":")[1];
                    if(!loginPhoneNumber.equals("")){
                        allUsers.users.get(loginPhoneNumber).likedGoods.goods.put(title,allGoods.goods.get(title));
                        out.write("added".getBytes(StandardCharsets.UTF_8));
                    }
                    else{
                        out.write("failed".getBytes(StandardCharsets.UTF_8));
                    }
                }else if(mainRequest.startsWith("add to comments")) {
                    String title = mainRequest.split(":")[1];
                    String comment = mainRequest.split(":")[2];
                    String Stars = mainRequest.split(":")[3];
                    if(!loginPhoneNumber.equals("")){
                        HashMap<String,String> temp=new HashMap<>();
                        temp.put("view",comment);
                        temp.put("score",Stars);
                        allGoods.goods.get(title).comments.put(allUsers.users.get(loginPhoneNumber).username, temp);
                        int tempCounter=allGoods.goods.get(title).countOfLikes;
                        allGoods.goods.get(title).countOfLikes++;
                        allGoods.goods.get(title).rate=String.valueOf((Double.parseDouble(allGoods.goods.get(title).rate)*tempCounter+Double.parseDouble(Stars))/(tempCounter+1));
                        out.write("added".getBytes(StandardCharsets.UTF_8));
                    }
                    else{
                        out.write("failed".getBytes(StandardCharsets.UTF_8));
                    }
                }else if(mainRequest.startsWith("addItem")) {
                        String sideCategory = mainRequest.split("::::")[1];
                        String title = mainRequest.split("::::")[2];
                        String image = mainRequest.split("::::")[3];
                        String price = mainRequest.split("::::")[4];
                        String size = mainRequest.split("::::")[5];
                        String color = mainRequest.split("::::")[6];
                        String numberOfExistingGoods = mainRequest.split("::::")[7];
                        String description = mainRequest.split("::::")[8];
                        String ownerNickName = mainRequest.split("::::")[9];
                    List<String> images = Arrays.asList(image.split(" "));
                        List<String> colors = Arrays.asList(color.split(" "));
                        List<String> Sizes = Arrays.asList(size.split(" "));
                        Good good = new Good(title, price, description, images, "0", loginPhoneNumber, SideCategories.valueOf(sideCategory), colors, Sizes, new HashMap<>(), Integer.parseInt(numberOfExistingGoods), ownerNickName);
                        allGoods.goods.put(title, good);
                        allUsers.users.get(loginPhoneNumber).ownedGoods.goods.put(title, good);
                }else if(mainRequest.startsWith("removeItem")) {

                    String title = mainRequest.split(":")[1];
                    allGoods.goods.remove(title);
                    allUsers.users.get(loginPhoneNumber).ownedGoods.goods.remove(title);
                }

            }

        } catch (Exception e) {
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
