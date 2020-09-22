package data;

import java.util.ArrayList;

public class Brand {
    private String name;
    private ArrayList<Beer> beersOfBrand = new ArrayList<>();

    public Brand() {
    }

    public Brand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Beer> getBeersOfBrand() {
        return beersOfBrand;
    }

    public void setBeersOfBrand(ArrayList<Beer> beersOfBrand) {
        this.beersOfBrand = beersOfBrand;
    }

    public void addBeer(Beer b) {
        beersOfBrand.add(b);
    }

    @Override
    public String toString() {
        return "[{brand: " + name + ", beers: [" + beersOfBrand + "]}]";
    }
}
