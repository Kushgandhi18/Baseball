import java.io.*;
import java.util.*;

public class Baseball {

	//used to store baseball card details
	 class Card {
	        String playerName;
	        int marketPrice;
	        int resalePrice;

	        public Card(String playerName, int marketPrice, int price) {
	            this.playerName = playerName;
	            this.marketPrice = marketPrice;
	            this.resalePrice = price;
	        }
	    }

	 //method for computing maximum profit
	    public Set<Card> computeMaxProfit(List<Card> items, int maxWeight) {
	        int maxProfit = 0;
	        Set<Card> maxSet = new HashSet<>();//to store best subset of cards
	        int nItems = items.size();//number of items

	        for (int i = 1; i <= nItems; i++) {
	            List<List<Card>> set = createSubsets(items, i);
	            for (List<Card> subset : set) {
	                int setResaleWeight = subset.stream().mapToInt(card -> card.resalePrice).sum();//resale value
	                int setProfit = subset.stream().mapToInt(card -> card.marketPrice - card.resalePrice).sum();//profit
	                if (setResaleWeight <= maxWeight && setProfit > maxProfit) {
	                    maxProfit = setProfit;
	                    maxSet = new HashSet<>(subset);
	                }
	            }
	        }
	        return maxSet;
	    }

	    //call generate set method
	    List<List<Card>> createSubsets(List<Card> items, int setSize) {
	        List<List<Card>> sets = new ArrayList<>();
	        generateSets(items, setSize, 0, new ArrayList<>(), sets);
	        return sets;
	    }

	    //method to generate subset
	    void generateSets(List<Card> items, int setSize, int currentPos, List<Card> currentSet, List<List<Card>> sets) {
	        if (setSize == 0) { 
	            sets.add(new ArrayList<>(currentSet));//add current set to list of sets
	            return;
	        }

	        if (currentPos == items.size()) {
	            return;
	        }

	        //reduces set size
	        currentSet.add(items.get(currentPos));
	        generateSets(items, setSize - 1, currentPos + 1, currentSet, sets);

	        currentSet.remove(currentSet.size() - 1);
	        generateSets(items, setSize, currentPos + 1, currentSet, sets);
	    }

	    //method for reading market price from market price file
	    Map<String, Integer> readMarketPrices(String filePath) {
	        Map<String, Integer> marketPrices = new HashMap<>();
	        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
	            int numMarketPrices = Integer.parseInt(reader.readLine().trim());
	            for (int i = 0; i < numMarketPrices; i++) {
	                String[] line = reader.readLine().trim().split(" ");
	                marketPrices.put(line[0], Integer.parseInt(line[1]));
	            }
	        } catch (Exception e) {
	            System.err.println("Error reading market prices: " + e.getMessage());
	        }
	        return marketPrices;
	    }

	    //method to read price list from price list and writing in output file
	    void managePriceList(String priceListPath, String outputPath, Map<String, Integer> marketPrices) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(priceListPath));
	             FileWriter fw = new FileWriter(outputPath)) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] firstLine = line.trim().split(" ");
	                int nj = Integer.parseInt(firstLine[0]);//number of cards
	                int Wj = Integer.parseInt(firstLine[1]);//max money to spend
	                List<Card> cards = new ArrayList<>();
	                
	                //add to card list if card name is found in market price map
	                for (int i = 0; i < nj; i++) {
	                    String[] cardInfo = reader.readLine().trim().split(" ");
	                    String cardName = cardInfo[0];
	                    int price = Integer.parseInt(cardInfo[1]);
	                    if (marketPrices.containsKey(cardName)) {
	                        cards.add(new Card(cardName, marketPrices.get(cardName), price));
	                    } else {
	                        System.err.println("Error: Card " + cardName + " not found in market prices.");
	                    }
	                }
	                
	                //calling computeMaxProfit method
	                long startTime = System.currentTimeMillis();
	                Set<Card> maxSet = computeMaxProfit(cards, Wj);
	                long endTime = System.currentTimeMillis();
	                int maxProfit = maxSet.stream().mapToInt(card -> card.marketPrice - card.resalePrice).sum();
	                
	                
	                //writes data in output file
	                fw.write(nj + " $" + maxProfit + " " + maxSet.size() + " " + (endTime - startTime) / 1000.0 + "\n");
	                for (Card card : maxSet) {
	                    fw.write(card.playerName + "\n");
	                }
	            }
	        } catch (Exception e) {
	            System.err.println("Error processing price list: " + e.getMessage());
	        }
	    }

	    public static void main(String[] args) {
	        if (args.length < 3) {
	            System.out.println("Usage: java Baseball <market-price-file> <price-list-file> <output-file>");
	            return;
	        }
	        
	        String marketPriceFile = args[0];
	        String priceListFile = args[1];
	        String outputFile = args[2];
	        
	        Baseball baseball = new Baseball();
	        Map<String, Integer> marketPrices = baseball.readMarketPrices(marketPriceFile);
	        baseball.managePriceList(priceListFile, outputFile, marketPrices);
	    }
}
