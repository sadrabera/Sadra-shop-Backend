import Enums.MainCategories;
import Enums.SideCategories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Good implements Serializable {
    public String title;
    public String price;
    public String description;
    public List<String> image;
    public String rate;
    public String owner;
    public String ownerNickname;
    public SideCategories sideCategory;
    public int numberOfExistingGoods;
    public int countOfLikes=0;
    public List<String> Colors = new ArrayList<>();
    public List<String> Sizes = new ArrayList<>();
    public HashMap<String, HashMap<String,String>> comments = new HashMap<>();
    Good(String title, String price, String description, List<String> image, String rate, String owner , SideCategories sideCategory, List<String> Colors, List<String> Sizes, HashMap<String, HashMap<String,String>> comments, int numberOfExistingGoods, String ownerNickname) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.image = image;
        this.rate = rate;
        this.owner = owner;
        this.sideCategory = sideCategory;
        this.Colors = Colors;
        this.Sizes = Sizes;
        this.comments = comments;
        this.numberOfExistingGoods=numberOfExistingGoods;
        this.ownerNickname=ownerNickname;
    }

}
class AllGoods implements Serializable{
public HashMap<String, Good> goods=new HashMap<>();


    public synchronized void removeGoods(String title){
        goods.remove(title);
    }
    public synchronized void updateGoods(String title, String price, String description, List<String> image, String rate, String owner, SideCategories sideCategory, ArrayList<String> Colors, ArrayList<String> Sizes, HashMap<String, HashMap<String,String>> comments, int numberOfExistingGoods) {
        goods.get(title).title = title;
        goods.get(title).price = price;
        goods.get(title).description = description;
        goods.get(title).image = image;
        goods.get(title).rate = rate;
        goods.get(title).owner = owner;
    }
    public  void printGoods(){
        for(String key:goods.keySet()){
            System.out.println(goods.get(key));
        }
    }
}
