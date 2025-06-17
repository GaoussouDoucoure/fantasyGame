
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/***
 * Game where we can have to Entities battle blow-by-blow and use their magic items.
 * NOUNS (Person, Place, Things, or Idea): Game, Entity, Item
 * VERBS (Action words like Run or Jump):
 * ADJECTIVES (Color, Height):
 */
class MagicItem{
    String name;
    int price = 0;
    boolean allowMultiple = false;
    String attr;
    int amount;
    MagicItem(String name, int price, String attr, int amount){
        this.name = name;
        this.price = price;
        this.attr = attr;
        this.amount = amount;
    }
}

class Entity{
    String name;
    ArrayList<MagicItem> inventory;
    HashMap<String, Integer> attrs;
    Entity(String name){
        this.name = name;
        this.inventory = new ArrayList<MagicItem>();
        this.attrs = new HashMap<String, Integer>();
    }
}
class Store{
    ArrayList<MagicItem> inventory;
    Store(){
        this.inventory = new ArrayList<MagicItem>();
        FileInputStream f = null;
        try {
            f = new FileInputStream("store.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Scanner s = new Scanner(f);
        while(s.hasNext()){
            String[] parts = s.nextLine().trim().split("\\|");
            int price = Integer.parseInt(parts[1]);
            int amount = Integer.parseInt(parts[3]);
            this.inventory.add( new MagicItem(
                    parts[0],
                    price,
                    parts[2],
                    amount
            ) );
        }
        try {
            f.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        this.inventory.add( new MagicItem("Fire Sword", 100, "STRENGTH", 10 ) );
//        this.inventory.add( new MagicItem("Ice Shield", 100, "DEXTERITY", 10 ) );
    }
    MagicItem buy(Entity buyer, int itemIndex){
        MagicItem item = this.inventory.get(itemIndex);
        if(buyer.attrs.get("GOLD") >= item.price){
            this.inventory.remove(item);
            return item;
        }
        return null;
    }
}
class Game{
    ArrayList<Entity> entities;
    Game(ArrayList<Entity> entities){
        this.entities = entities;
    }
    void battle(Entity e1, Entity e2){ //fight
        //BCS Base Chance of success +- dex armor value of your items
        //Damage str +- attack value of item;
        System.out.println("Battling!!!!");
        Entity attacker = e1;
        Entity defender = e2;
        while(e1.attrs.get("HEALTH") > 0 && e2.attrs.get("HEALTH") > 0) {
            int dexModifier = 0;
            int strModifier = 0;
            for(MagicItem mi : attacker.inventory){
                if (mi.attr == "DEXTERITY"){
                    dexModifier += mi.amount;
                }else if(mi.attr == "STRENGTH"){
                    strModifier += mi.amount;
                }
            }
            int dexModifierDefender = 0;
            for(MagicItem mi : defender.inventory){
                if (mi.attr == "DEXTERITY"){
                    dexModifierDefender += mi.amount;
                }
            }
            int baseChance = 50 + e1.attrs.get("DEXTERITY") - e2.attrs.get("DEXTERITY") + dexModifier - dexModifierDefender;
            double roll = (Math.random() + Math.random() + Math.random()) / 3; //bell curve
            if(roll > (baseChance / 100.0)){
                //HIT
                int damage = 10 + attacker.attrs.get("STRENGTH") + strModifier;
                defender.attrs.put("HEALTH", defender.attrs.get("HEALTH") - damage);
                System.out.println(attacker.name + " hits " + defender.name + " for " + damage + " damage.");
            }else{
                System.out.println(attacker.name + " swings and misses " + defender.name + ".");
            }
            attacker = (attacker == e1) ? e2 : e1;
            defender = (defender == e1) ? e2 : e1;
            System.out.printf("\n%s has %d health, ", e1.name, e1.attrs.get("HEALTH"));
            System.out.printf("%s has %d health.\n", e2.name, e2.attrs.get("HEALTH"));
        }
        if(e1.attrs.get("HEALTH") <= 0){
            System.out.println(e1.name + " has left this mortal coil.");
        }
        if(e2.attrs.get("HEALTH") <= 0){
            System.out.println(e2.name + " has met their doom.");
        }
        System.out.println("BATTLE CONCLUDED");
    }
}
public class Main {
    public static void main(String[] args) {
        Store store = new Store();
        ArrayList<Entity> players = new ArrayList<Entity>();

        System.out.print("What is your hero name? ");
        Scanner in = new Scanner(System.in);
        String name = in.nextLine().trim();

        System.out.print("What is your enemy name? ");
        String enemyName = in.nextLine().trim();

        Entity hero = new Entity(name);
        hero.attrs.put("STRENGTH", 18);
        hero.attrs.put("DEXTERITY", 18);
        hero.attrs.put("HEALTH", 100);
        hero.attrs.put("GOLD", 100);
        store.buy(hero, 1); //1 is sheild
        players.add(hero);

        Entity zombie = new Entity(enemyName);
        zombie.attrs.put("STRENGTH", 12);
        zombie.attrs.put("DEXTERITY", 12);
        zombie.attrs.put("HEALTH", 100);
        zombie.attrs.put("GOLD", 100);
        store.buy(zombie, 0); //sword
        players.add(zombie);

        Game g = new Game(players);
        g.battle(hero, zombie);
    }
}
