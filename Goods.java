import java.util.HashMap;

public class Goods {
    public String title;
    public String price;
    public String description;
    public String image;
    public String rate;
    public String owner;
    Goods (String title, String price, String description, String image, String rate, String owner) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.image = image;
        this.rate = rate;
        this.owner = owner;
    }

}
class AllGoods{
public HashMap<String,Goods> goods=new HashMap<>();

    public void addGoods(String title, String price, String description, String image, String rate, String owner){
        goods.put(title,new Goods(title,price,description,image,rate,owner));
    }
    public void removeGoods(String title){
        goods.remove(title);
    }
    public void updateGoods(String title, String price, String description, String image, String rate, String owner){
        goods.get(title).title = title;
        goods.get(title).price = price;
        goods.get(title).description = description;
        goods.get(title).image = image;
        goods.get(title).rate = rate;
        goods.get(title).owner = owner;
    }
    public void printGoods(){
        for(String key:goods.keySet()){
            System.out.println(goods.get(key));
        }
    }
}
