package main;

import data.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BeerFilter {
    private static ArrayList<Beer> beers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            System.out.println("#0");
            initDB();
            printList(beers);
            System.out.println("***\n");

            System.out.println("#1");
            ArrayList<Brand> brands = getBeersByBrand();
            printBrands(brands);
            System.out.println("***\n");

            System.out.println("#2");
            ArrayList<Brand> filteredByType = filterBrandsByType(new BeerType("Szűrt"));
            printBrands(filteredByType);
            System.out.println("***\n");

            System.out.println("#3");
            System.out.println(getCheapestBrand(brands));
            System.out.println("***\n");

            System.out.println("#4");
            System.out.println(filterBeersByIngredient(new Ingredient("búza")));
            System.out.println("***\n");

            System.out.println("#5");
            printList(sortByWaterRatio());
            System.out.println("***\n");
            System.out.println("#6");
            printMap(getBeerPrices());
            System.out.println("***\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * #0
     * Parses JSON file
     * @throws IOException
     * @throws ParseException
     */
    private static void initDB() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader("beers.json"));

        JSONArray ja = (JSONArray) obj;
        for (Object o : ja) {
            JSONObject jo = (JSONObject) o;

            BeerType type = new BeerType((String) jo.get("type"));
            String name = (String) jo.get("name");
            BeerBrand beerBrand = new BeerBrand((String) jo.get("brand"));
            int price = Integer.parseInt((String) jo.get("price"));
            double alcohol = Double.parseDouble((String) jo.get("alcohol"));
            JSONArray ingredients = (JSONArray) jo.get("ingredients");
            Iterator iterator = ingredients.iterator();
            Iterator<Map.Entry> iter2;
            ArrayList<Ingredient> tempIngredients = new ArrayList<>();
            while (iterator.hasNext()) {
                iter2 = ((Map) iterator.next()).entrySet().iterator();
                Ingredient temp = new Ingredient();
                while (iter2.hasNext()) {
                    Map.Entry pair = iter2.next();

                    if (((String) pair.getKey()).equals("ratio")) {
                        temp.setRatio(Double.parseDouble((String) pair.getValue()));
                    } else if (((String) pair.getKey()).equals("name")) {
                        temp.setName((String) pair.getValue());
                    }
                }
                tempIngredients.add(temp);
            }
            boolean isCan = (boolean) jo.get("isCan");

            beers.add(new Beer(type, name, beerBrand, price, alcohol, tempIngredients, isCan));
        }
    }

    /**
     * #1
     * Gets the beers from the collection filtered by Brand.
     * @return
     */
    private static ArrayList<Brand> getBeersByBrand() {
        ArrayList<Brand> resultList = new ArrayList<>();
        ArrayList<String> brands = new ArrayList<>();

        for (Beer b : beers) {
            if(!brands.contains(b.getBrand().getName())) {
               brands.add(b.getBrand().getName());
               resultList.add(new Brand(b.getBrand().getName()));
            }
        }

        for (Beer b: beers) {
            for(int i = 0; i < resultList.size(); i++) {
                if(b.getBrand().getName().equals(resultList.get(i).getName())){
                    resultList.get(i).addBeer(b);
                }
            }
        }
        return resultList;
    }

    /**
     * #2
     * Filters the Brands by the given BeerType.
     * @param type the BeerType to filter with.
     * @return an ArrayList of Brands filtered as mentioned before.
     */
    private static ArrayList<Brand> filterBrandsByType(BeerType type) {
        ArrayList<Brand> resultList = getBeersByBrand();
        ArrayList<Beer> toRemove = new ArrayList<>(); // Workaround for ConcurentModificationException
        for(Brand b : resultList) {
            for(Beer beer: b.getBeersOfBrand()) {
                if(!beer.getType().getName().equals(type.getName()))
                   toRemove.add(beer);
            }
            b.getBeersOfBrand().removeAll(toRemove);
        }


        return resultList;
    }

    /**
     * #3
     * Gets the cheapest brand.
     * @param brands the brands you want to filter
     * @return the cheapest brand.
     */
    private static String getCheapestBrand(ArrayList<Brand> brands) {
        Map<Double, String> brandsByPrice = new LinkedHashMap<>();
        for(Brand brand : brands) {
            double temp = 0;
            for (Beer beer: brand.getBeersOfBrand()) {
                temp += beer.getPrice();
            }
            temp /= brand.getBeersOfBrand().size();
            brandsByPrice.put(temp, brand.getName());
        }

        TreeMap<Double, String> sortedBrands = new TreeMap<Double, String>(
                new Comparator<Double>() {
                    @Override
                    public int compare(Double o1, Double o2) {
                        return o1.compareTo(o2);
                    }
                }
        );

        sortedBrands.putAll(brandsByPrice);
        return "The cheapest brand is: " + sortedBrands.firstEntry().getValue() + " with the average price of: " + sortedBrands.firstEntry().getKey();
    }

    /**
     * #4
     * Returns an array of beers which lack the given ingredient
     * @param ingredient the name of the ingredient you don't want in your beer
     * @return an <code>ArrayList</code> of <code>Beer</code> which lack the given ingredient.
     */
    private static ArrayList<Beer> filterBeersByIngredient(Ingredient ingredient) {
        ArrayList<Beer> resultList = new ArrayList<>();
        for(Beer b: beers)
            if (!b.hasIngredient(ingredient))
                resultList.add(b);
        return resultList;
    }

    /**
     * #5
     * Sorts the beers in decending order by water ratio
     * @return an <code>ArrayList</code> of Beer objects.
     */
    private static ArrayList<Beer> sortByWaterRatio() {
        ArrayList<Beer> resultList = new ArrayList<>();
        Map<Double, ArrayList<Beer>> beersByWaterRatio = new LinkedHashMap<>();

        for(Beer b: beers) {
            double waterRatio = 0;
            for(Ingredient ingredient : b.getIngredients()) {
                waterRatio += ingredient.getRatio();
            }
            waterRatio = 1 - waterRatio;

            if (!beersByWaterRatio.containsKey(waterRatio)) {
                beersByWaterRatio.put(waterRatio, new ArrayList<>());
            }
            beersByWaterRatio.get(waterRatio).add(b);
        }

        TreeMap<Double, ArrayList<Beer>> sortedByWaterRatio = new TreeMap<>(
                new Comparator<Double>() {
                    @Override
                    public int compare(Double o1, Double o2) {
                        return o2.compareTo(o1);
                    }
                }
        );

        sortedByWaterRatio.putAll(beersByWaterRatio);

        for(Map.Entry<Double, ArrayList<Beer>> entry: sortedByWaterRatio.entrySet()) {
            resultList.addAll(entry.getValue());
        }

        return resultList;
    }

    /**
     * #6
     * Creates <code>HashMap</code> with BeerPrices as keys, and BeerNames as values.
     * @return
     */
    private static Map<Integer, ArrayList<Beer>> getBeerPrices(){
        Map<Integer, ArrayList<Beer>> beersWithPrice = new LinkedHashMap<>();
        for (Beer b: beers) {
            int temp = Math.round((float)b.getPrice() / 100) * 100;
            if(!beersWithPrice.containsKey(temp))
                beersWithPrice.put(temp, new ArrayList<>());
            beersWithPrice.get(temp).add(b);
        }
        return beersWithPrice;
    }

    /**
     * Nothing interesting here...
     */
    private static void printBrands(ArrayList<Brand> list){
        for(Brand b : list) System.out.println(b);
    }

    private static void printList(ArrayList<Beer> list) {
        for(Beer b: list) System.out.println(b);
    }

    private static void printMap(Map<Integer, ArrayList<Beer>> map) {
        for(Map.Entry<Integer, ArrayList<Beer>> entry : map.entrySet()) {
            System.out.println("Nearest hundred: " + entry.getKey() + " { " + entry.getValue() + " }");
        }
    }

}
