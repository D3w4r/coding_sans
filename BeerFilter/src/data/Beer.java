package data;

import java.util.ArrayList;

public class Beer {
    private BeerType type;
    private String name;
    private BeerBrand beerBrand;
    private int price;
    private double alcohol;
    private ArrayList<Ingredient> ingredients;
    private boolean isCan;

    public Beer(BeerType type, String name, BeerBrand beerBrand, int price, double alcohol, ArrayList<Ingredient> ingredients, boolean isCan) {
        this.type = type;
        this.name = name;
        this.beerBrand = beerBrand;
        this.price = price;
        this.alcohol = alcohol;
        this.ingredients = ingredients;
        this.isCan = isCan;
    }

    public BeerType getType() {
        return type;
    }

    public void setType(BeerType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BeerBrand getBrand() {
        return beerBrand;
    }

    public void setBrand(BeerBrand beerBrand) {
        this.beerBrand = beerBrand;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(double alcohol) {
        this.alcohol = alcohol;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean isCan() {
        return isCan;
    }

    public void setCan(boolean can) {
        isCan = can;
    }

    public boolean hasIngredient(Ingredient i) {
        for(Ingredient ingredient : ingredients) {
            if(ingredient.getName().equals(i.getName()))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "beer{" +
                "name='" + name + '\'' +
                '}' + '\n';
    }
}
