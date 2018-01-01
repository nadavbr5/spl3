package bgu.spl181.net;

public class Movie {
    private int id;
    private String name;
    private double price;
    private String[] bannedCountries;
    private int availableAmount;
    private int totalAmount;

    //TODO:: check if the name already exists in the system and if the price or the amount are <=0
    public Movie(int id, String name, double price, String[] bannedCountries, int availableAmount, int totalAmount){
        this.id=id;
        this.name=name;
        this.price=price;
        this.bannedCountries=bannedCountries;
        this.availableAmount=availableAmount;
        this.totalAmount=totalAmount;
    }

    public void setPrice(double newPrice){
        this.price=newPrice;
    }

    public boolean rent(){
        if(availableAmount == 0)
            return false;
        this.availableAmount--;
        return true;
    }

    public void returnMovie(){
        this.availableAmount++;
    }

    //TODO:: change the function
    public String toString(){
        return "\"name\" "+ availableAmount + " " + totalAmount + " " + bannedCountries.toString();

    }
}
