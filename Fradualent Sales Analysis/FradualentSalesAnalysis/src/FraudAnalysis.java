import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import dataMiningAlgorithms.IterativeDichotomiser3;
import dataMiningAlgorithms.KMeansClustering;
import dataMiningAlgorithms.NaiveBayesClassifier;

public class FraudAnalysis {
	
	private HashMap<String, Integer> productUnitPrice;
	
	
	public FraudAnalysis ()
	{
		this.productUnitPrice = new HashMap<String, Integer>();
	}
	
	public HashMap<String, Integer> getProductUnitPrice()
	{
		return this.productUnitPrice;
	}

	public HashMap<String, Integer> computeUnitPrice(ArrayList<ArrayList<String>> fileData)
	{
		HashMap<String, ArrayList<Integer>> productUnitPriceTemp = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, Integer> productUnitPrice= new HashMap<String, Integer>();
		for (int i=0; i<fileData.size();i++)
		{
			if (!fileData.get(i).get(2).equalsIgnoreCase("NA")  && !fileData.get(i).get(3).equalsIgnoreCase("NA"))
			{	
				double value = Double.parseDouble(fileData.get(i).get(3));
				double quantity= Double.parseDouble(fileData.get(i).get(2));
				int unitPrice =(int)Math.ceil(value/quantity);
				// Adding another column of Unit Price
				try 
				{
					fileData.get(i).set(5,String.valueOf(unitPrice));
				}
				catch(IndexOutOfBoundsException e)
				{
					fileData.get(i).add(String.valueOf(unitPrice));
				}
			}
			else
				fileData.get(i).add(String.valueOf("-1"));
			
			String product=fileData.get(i).get(1);
			Integer unitPrice=Integer.parseInt(fileData.get(i).get(5));
		
			if (unitPrice != -1 && /*Ignoring unit price for transactions with NA*/
					!fileData.get(i).get(4).equalsIgnoreCase("Fraud") /*Ignoring unit price for Fraud transactions*/)
			{
				if (productUnitPriceTemp.containsKey(product))
				{
					productUnitPriceTemp.get(product).add(unitPrice);
				}
				else 
				{
					ArrayList<Integer> values = new ArrayList<Integer>();
					values.add(unitPrice);
					productUnitPriceTemp.put(product, values);		
				}
			}	
		}	
		
			Iterator<String> keys;
			keys = productUnitPriceTemp.keySet().iterator();
			while(keys.hasNext())
			{
				String key=keys.next();
				ArrayList<Integer> unitPriceList= productUnitPriceTemp.get(key);
				
				//Sorting the Unit price in increasing order
				Collections.sort(unitPriceList);
			
				Integer medianElem=unitPriceList.size()/2;
					
				productUnitPrice.put(key, unitPriceList.get(medianElem));
			}
		return productUnitPrice;
	}

	
	public void splitContunuosValues(ArrayList<ArrayList<String>>  trainData, String[] aType)
	{
		ArrayList<Double> contAttr = new ArrayList<Double>(); 
		for (int i=0; i<trainData.get(0).size();i++)
		{
			for (int j=0; j<trainData.size();j++)
			{
				if (!aType[i].equalsIgnoreCase("c"))
					break;
				else
				{
					contAttr.add(Double.parseDouble(trainData.get(j).get(i)));
				}
			}
		}
		
		Collections.sort(contAttr);
		double medianElem = contAttr.get(contAttr.size()/2);
		
		for (int i=0; i<trainData.get(0).size();i++)
		{
			for (int j=0; j<trainData.size();j++)
			{
				if (!aType[i].equalsIgnoreCase("c"))
					break;
				else
				{
					if (Double.parseDouble(trainData.get(j).get(i)) < medianElem)
						trainData.get(j).set(i, "T");
					else
						trainData.get(j).set(i, "F");
				}
			}
		}
	}

	
	public void cleanData(ArrayList<ArrayList<String>>fileData)
	{
		System.out.println("\n\n############################");
		System.out.println("Cleaning Data..........");
		System.out.println("############################");
		int qtyAndValMiss =0;
		boolean cleanFlag=true;
		int itr=1;
		while (cleanFlag)
		{
			cleanFlag=false;
			for (int i=0; i < fileData.size();i++)
			{
				// Records we remove
				if (
						(
								fileData.get(i).get(2).equalsIgnoreCase("NA") && fileData.get(i).get(3).equalsIgnoreCase("NA")
						) ||  /* Both Quantity and Val is NA*/
						
						/* These two products have quantity missing from all the transactions*/
						fileData.get(i).get(1).equalsIgnoreCase("p2442") ||  
						fileData.get(i).get(1).equalsIgnoreCase("p2443")
						
					)
				{
					cleanFlag=true;
					fileData.remove(fileData.get(i));
					qtyAndValMiss++;
				}
			}
			itr++;
		}
		
			System.out.println("Number of records post dropping records with missing values: " + fileData.size());
			System.out.println("Number of records with both qty and val missing: " + qtyAndValMiss);
			
			// Computing the unit price. So that we use it later to
			System.out.println("Computing Unit price....");
			productUnitPrice = computeUnitPrice(fileData);

			
			//Replace the remaining missing values with the now calculated Unit price
			int qtyRepCount=0, valRepCount=0;
			for (int i=0; i < fileData.size();i++)
			{
				// Quantity Missing
				if (fileData.get(i).get(2).equalsIgnoreCase("NA"))
				{
					double value = Integer.parseInt(fileData.get(i).get(3));
					double unitPrice = productUnitPrice.get(fileData.get(i).get(1));
					Integer qty = (int)Math.ceil(value/unitPrice);
					fileData.get(i).set(2,qty.toString());
					qtyRepCount++;
				}
				
				// Value Missing
				if (fileData.get(i).get(3).equalsIgnoreCase("NA"))
				{
					Integer value = Integer.parseInt(fileData.get(i).get(2))*productUnitPrice.get(fileData.get(i).get(1));
					fileData.get(i).set(3,value.toString());
					valRepCount++;
				}
			}
			
			System.out.println("Number of records for which Qty value replaced: " + qtyRepCount);
			System.out.println("Number of records for which val value replaced: " + valRepCount);			
			
			// Recomputing Unit Price
			System.out.println("\nRe-calculating unit price with newly filled Quantity and Value....");
			productUnitPrice = computeUnitPrice(fileData);
			System.out.println("Unit price updated....");
			
	}

	public ArrayList<ArrayList<String>> fileToMatrix(File fileName, boolean hasHeader)
	{
		ArrayList<ArrayList<String>> fileData = new ArrayList<ArrayList<String>>();
		System.out.println("############################");
		System.out.println("Reading File..........");
		System.out.println("############################");
		fileData = new ArrayList<ArrayList<String>>();  
		String[] colsVal=null;
		int rowIndex=0;
		 try 
		 {
		   Scanner scanner = new Scanner(fileName);
		   
		   while (scanner.hasNext())
		   {
			   int colIndex =0;
			   String line = scanner.next();
			   if (hasHeader)
			   {
				   colsVal = line.split(",");	
				   hasHeader=false;
				   continue;
			   }
			   colsVal = line.split(",");
			   fileData.add(new ArrayList<String>());
			   
			   while (colIndex < colsVal.length)
			   {
				   fileData.get(rowIndex).add(colsVal[colIndex]);
				   colIndex++;
			   }
			   
			   rowIndex++;
		   }
		   scanner.close();
		 } 
		 catch (FileNotFoundException e) 
		 {
			 e.printStackTrace();
		 }
		 System.out.println("Number of records read: " + rowIndex);
		 System.out.println("Number of columns found: " + colsVal.length);
		 
		 return fileData;
	}
	 
		 
	
	public void prepareTrainTestDataset(ArrayList<ArrayList<String>> fileData, ArrayList<ArrayList<String>> trainData, ArrayList<ArrayList<String>> testData)
	{
		int trainRecCount=0,testRecCount=0; 
		for (int i=0; i<fileData.size();i++)
		{
			int numOfCol=fileData.get(i).size();
			if (!fileData.get(i).get(numOfCol-2).equalsIgnoreCase("unkn"))
			{
				trainData.add(new ArrayList<String>());
				for (int j=0; j<numOfCol;j++)
				{
					if (j != 2 && j!= 3 && j!= 4)
						trainData.get(trainRecCount).add(fileData.get(i).get(j));
				}
				trainData.get(trainRecCount).add(fileData.get(i).get(4));
				trainRecCount++;
				
			}
			else
			{
				testData.add(new ArrayList<String>());
				for (int j=0; j<numOfCol;j++)
				{
					if (j != 2 && j!= 3 && j!= 4)
						testData.get(testRecCount).add(fileData.get(i).get(j));
				}
				testData.get(testRecCount).add(fileData.get(i).get(4));
				testRecCount++;
			}		
		}
		System.out.println("Creating Training and Test Data set... \nExample...");
		System.out.println("Train:: Col: "+ trainData.get(0).size() + " Records: " + trainData.size() + ": " + trainData.get(0));
		System.out.println("Test:: Col: " + testData.get(0).size() + " Recrods: " + testData.size()+ ": " + testData.get(0));
	}
	public static void main (String[] args)
	{ 
 
		FraudAnalysis fa = new FraudAnalysis();
		
		ArrayList<ArrayList<String>> fileData = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> trainData = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testData = new ArrayList<ArrayList<String>>();
		
		/*
		 * Getting data in Java native data structure
		 */
		fileData = fa.fileToMatrix(new File("src//sales.csv"),true);
		
		fa.cleanData(fileData);

		fa.prepareTrainTestDataset(fileData, trainData, testData);

		/*******************************************
		 * Implementing Naive Bayes Classifier
		********************************************/ 		
		System.out.println("Implemenetating Naive Bayes .....");
		
		NaiveBayesClassifier nb = new NaiveBayesClassifier();
		
		nb.train(trainData, new String[]{"d","d","c","l"}, new String[]{"ID","Prod","UnitPrice","Insp"});
		
		System.out.println("#################################################################");
		System.out.println("Classifying Data...");
		System.out.println("#################################################################");
		ArrayList<ArrayList<String>> pred= nb.classify(testData);
		
		System.out.println("Naive Bayes Implemenetation complete.....");
		
		/*******************************************
		 * Implementing K Means Clustering
		********************************************/ 
		
		KMeansClustering kmc = new KMeansClustering();
		
		ArrayList<ArrayList<ArrayList<String>>> clusterGroup = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<Double>> finalCentroids = new ArrayList<ArrayList<Double>>();
		
		ArrayList<ArrayList<String>> inpX = new ArrayList<ArrayList<String>>();
		HashMap<String, Integer> productUnitPrice= fa.getProductUnitPrice();
		
		Iterator<String> keysAttr;
		keysAttr= productUnitPrice.keySet().iterator();
		int fraudCount=0, okCount=0;
		System.out.println(fileData.get(0));
		while(keysAttr.hasNext())
		{
			inpX.clear();
			String prodID=keysAttr.next();
			System.out.println("For Product ID:\t "+ prodID);
			for (int i=0; i < fileData.size();i++)
			{
				if (fileData.get(i).get(1).equalsIgnoreCase(prodID))
				{
					inpX.add(fileData.get(i));
				}
						
			}
			clusterGroup =kmc.kMeanClusterAlgo(kmc.pickAttributes(inpX, inpX.get(0).size()-1, inpX.get(0).size()-1),2,"E", 50,null,finalCentroids);
			kmc.printCentroids(finalCentroids);	
			// My assumption is smaller cluster would be the fraud one.
			
			if (clusterGroup.get(0).size() <= clusterGroup.get(1).size())
			{
				fraudCount += clusterGroup.get(0).size();
				okCount += clusterGroup.get(1).size();
			}
			else
			{
				fraudCount += clusterGroup.get(1).size();
				okCount += clusterGroup.get(0).size();
			}
		}	

		System.out.println("Number of records classified as Fraud:\t" + fraudCount);	
		System.out.println("Number of records classified as OK:\t" + okCount);
		
		/*******************************************
		 * Implementing ID3
		********************************************/
		
		System.out.println("Implemenetating IterativeDichotomiser3 (ID3) .....");
		fa.splitContunuosValues(trainData, new String[]{"d","d","c","l"});
		for (int i=0; i <10;i++)
			System.out.println(trainData.get(i));	
		
		//IterativeDichotomiser3 id3 = new IterativeDichotomiser3(trainData, trainData, new String[]{"Example","A1","A2","A3","OutputY"});
		IterativeDichotomiser3 id3 = new IterativeDichotomiser3(trainData, testData, new String[]{"ID","Prod","UPrice","Insp"});
		

		id3.buildDecisionTree(new String[]{"d","d","d","l"});
		System.out.println("#################################################################");
		System.out.println("Classifying Data...");
		System.out.println("#################################################################");
		id3.classify(testData);
	}
}
