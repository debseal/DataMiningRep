package dataMiningAlgorithms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;
public class KMeansClustering {

	
	public ArrayList<ArrayList<BigDecimal>> stringToBigDecimal(ArrayList<ArrayList<String>> X)
	{
		ArrayList<ArrayList<BigDecimal>> cand = new ArrayList<ArrayList<BigDecimal>>();
		for (int i = 0; i < X.size();i++)
		{
			cand.add(new ArrayList<BigDecimal>());
			for (int j = 0; j < X.get(i).size();j++)
			{
				Double d = Double.valueOf(X.get(i).get(j));
				if (j==0)
				{
				BigDecimal bd= new BigDecimal(d/Math.pow(10, 18));
				bd= bd.setScale(1, BigDecimal.ROUND_HALF_UP);
				cand.get(i).add(bd);
				}
				else
				{
					BigDecimal bd= new BigDecimal(d);
					bd= bd.setScale(5, BigDecimal.ROUND_HALF_UP);
					cand.get(i).add(bd);
				}
			}	
		}
		
		return cand;
	}
	
	public int astroDistanceFunc (ArrayList<String> mTuple, ArrayList<ArrayList<Double>> candidate)
	{
		int index=0;
		double min = Double.MAX_VALUE;
		double[] distToCandidate = new double[candidate.size()];
		double zMain = Double.parseDouble(mTuple.get(2));
		double rMain = Double.parseDouble(mTuple.get(1));
		double uMinusrMain = (Double.parseDouble(mTuple.get(0)) - Double.parseDouble(mTuple.get(1)));
		
		for (int i = 0;i <candidate.size();i++)
		{
			distToCandidate[i] = Math.sqrt(Math.pow((zMain - candidate.get(i).get(2))/0.2596,2) +  
					Math.pow((rMain - candidate.get(i).get(1))/8.6,2) + 
					Math.pow((uMinusrMain - (candidate.get(i).get(0) - candidate.get(i).get(1)))/0.2596,2));
		}


		for (int i = 0 ; i < distToCandidate.length;i++)
		{
			/*
			 * In this program the way I am breaking the tie situation (a datapoint is equidistant 
			 * from 2 centroids) is by picking the centroid of lower index.
			 */
			if (distToCandidate[i] < min)
			{
				min = distToCandidate[i];
				index= i;
			}	
		}
		return index;
	}
	
	public int eucledianDistance(ArrayList<String> dataPoints, ArrayList<ArrayList<Double>> centroids)
	{
		double[] distanceArray = new double[centroids.size()];
		int clusterNumber=-1;

		for (int i = 0 ;i < centroids.size();i++)
		{
			double distance =0;
			for (int j = 0 ;j < dataPoints.size();j++)
			{
				distance +=  Math.pow(Double.parseDouble(dataPoints.get(j)) - centroids.get(i).get(j),2);	
			}
			distanceArray[i] = Math.sqrt(distance);
		}
		
		double min = Double.MAX_VALUE;
		for (int i = 0 ; i < distanceArray.length;i++)
		{
			
			/*
			 * In this program the way I am breaking the tie situation (a datapoint is equidistant 
			 * from 2 centroids) is by picking the centroid of lower index.
			 */
			if (distanceArray[i] < min)
			{
				min = distanceArray[i];
				clusterNumber= i;
			}	
		}
		return clusterNumber;
	}
	
	public void printData(ArrayList<ArrayList<String>> inpX , ArrayList<String> outY )
	{
		for (int i =0 ; i < inpX.size();i++)
		{	
			for (int j =0 ; j < inpX.get(i).size();j++)
			{
				System.out.print(inpX.get(i).get(j) + "\t");
			}	
			if (outY != null)
				System.out.print("\t\t" + outY.get(i));
			System.out.println();
		}
		
	}	
	
	public void printClusterGroup(ArrayList<ArrayList<ArrayList<String>>> clusterGroup  ,int k)
	{
		System.out.println();
		for (int l= 0;l<k;l++)
		{	
			System.out.println("Number of dataPoints in Cluster # " + l + "\t:" + clusterGroup.get(l).size());
		}
	}
	
	
	public boolean isStable(ArrayList<ArrayList<Double>> prevCent, ArrayList<ArrayList<Double>> Cent)
	{	
		for (int i =0 ; i < prevCent.size();i++)
		{
			for (int j =0 ; j < prevCent.get(i).size();j++)
			{
				if (!prevCent.get(i).get(j).equals(Cent.get(i).get(j)))
					return true;
			}
		}
		return false;
	}
	
	public void printCentroids(ArrayList<ArrayList<Double>> cent)
	{
		System.out.println("Coordinates of Centroids now are:");
		for (int i = 0 ; i <cent.size();i++)
		{
			for (int j = 0 ; j <cent.get(i).size();j++)
			{
				System.out.print(cent.get(i).get(j) + "\t");
				
			}
			System.out.println();
		}
	}
	public ArrayList<ArrayList<ArrayList<String>>>  kMeanClusterAlgo(ArrayList<ArrayList<String>> X, int k, String distType, int iterCount, ArrayList<ArrayList<String>> cent, ArrayList<ArrayList<Double>> finalCentroids)
	{
		ArrayList<ArrayList<ArrayList<String>>> clusterGroup = new ArrayList<ArrayList<ArrayList<String>>>();
		int noOfattr = X.get(0).size();
		System.out.println("\n===============================================================");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("Implementing K means Clustering Algorithm with:");
		System.out.println("Number of Cluster:\t" + k);
		System.out.println("Distance Metric:\t" + (distType=="E"?"Eucledian":"Others"));
		System.out.println("Number of Attributes:\t" + noOfattr);
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		int count=0;  
		boolean stable = true;

		ArrayList<ArrayList<Double>> centroids = new ArrayList<ArrayList<Double>>();
		centroids.clear();
		for (int i = 0 ; i <k;i++)
		{
			centroids.add(new ArrayList<Double>());
			for (int j = 0 ; j <noOfattr;j++)
			{
				if (cent == null)
					centroids.get(i).add(Math.random() * 10);
				else
					centroids.get(i).add(Double.parseDouble(cent.get(i).get(j)));
			}
		}

		while (count < iterCount && stable)
		{
			ArrayList<ArrayList<Double>> prevCentroids = new ArrayList<ArrayList<Double>>();
			clusterGroup.clear();
			
			for (int i = 0 ; i < k; i++)
			{  
				prevCentroids.add(new ArrayList<Double>());
				prevCentroids.get(i).addAll(centroids.get(i));
				clusterGroup.add(new ArrayList<ArrayList<String>>());
			}	
		
			int[] arrayCounter= new int[k];
			for (int i= 0;i<X.size();i++)
			{
				if (distType == "E")
				{
					int index = eucledianDistance(X.get(i), centroids);
					clusterGroup.get(index).add(new ArrayList<String>());
					clusterGroup.get(index).get(arrayCounter[index]).addAll(X.get(i));
					arrayCounter[index]++;
				}
				else 
				{
					int index = astroDistanceFunc(X.get(i), centroids);
					clusterGroup.get(index).add(new ArrayList<String>());
					clusterGroup.get(index).get(arrayCounter[index]).addAll(X.get(i));
					arrayCounter[index]++;
				}
			}
			
			
			//Finding new centroids
			for (int i =0; i < k; i++)
			{	
				if (clusterGroup.get(i).size() == 0)
					continue;	
				for (int l = 0 ; l < noOfattr;l++)
				{
					int numberOfDataPoints=0;
					double sum=0;
					for (int j =0; j< clusterGroup.get(i).size(); j++)
					{
						sum += Double.parseDouble(clusterGroup.get(i).get(j).get(l));
						numberOfDataPoints++;
					}	
					centroids.get(i).set(l,sum/numberOfDataPoints);
				}
			}
			
			count++;
			stable =isStable(prevCentroids,centroids);
		}
		
		
		if (count >= iterCount)
			System.out.println("KMeans Algorithm ended because the iteration count exhausted");
		else
		{
			System.out.println("KMeans Algorithm ended because stable state was achieved after " + (count-1) + " iterations");
		}	
		finalCentroids.clear();
		for (int i = 0 ; i < k; i++)
		{  
			finalCentroids.add(centroids.get(i));
		}

		return clusterGroup;
	}
	
	
	
	public ArrayList<ArrayList<String>>  pickAttributes(ArrayList<ArrayList<String>> X, int start, int end)
	{
		ArrayList<ArrayList<String>> updInpX = new ArrayList<ArrayList<String>>();
		
		for (int i = 0; i < X.size();i++)
		{
			updInpX.add(new ArrayList<String>());
			for (int j = start; j <= end;j++)
			{
				updInpX.get(i).add(X.get(i).get(j));
			}
		}
		return updInpX;
	}
	
	public void dataCleansing(ArrayList<ArrayList<String>> X, String method)
	{
		ArrayList<Integer> medianArr = new ArrayList<Integer>();
		ArrayList<Integer> modeArr = new ArrayList<Integer>();
		System.out.println("\nCleansing data ....");
		System.out.println("Using " + method + " to replace missing values" +"....");
		System.out.println("##################################");
		int noOfAttr = X.get(0).size();
		int[] attrMean = new int[noOfAttr];
		
		if (method == "removal")
		{
			int count=0;
			for (int i= 0; i<X.size();i++)
			{
				if (X.get(i).contains("?"))
				{
					count++;
					X.remove(i);
					i=0;
					continue;
				}
			}
			System.out.println(count +" records with missing values removed!!!");
			System.out.println("New size of dataset is \t:" + X.size());
			return;
		}
		
		
		
		for (int j= 0; j<noOfAttr;j++)
		{
			double sum=0;
			double countObs=0;
			medianArr.clear();
			modeArr.clear();
			for (int i= 0; i<X.size();i++)
			{
				if (!X.get(i).get(j).equals("?"))
				{
					if (method == "mean")
					{
						sum += Double.parseDouble(X.get(i).get(j));
						countObs++;
					}
					else if (method == "median")
						medianArr.add(Integer.parseInt(X.get(i).get(j)));
					else if (method == "mode")
					{
						modeArr.add(Integer.parseInt(X.get(i).get(j)));
					}
				}
			}
			
			if (method == "mean")
				attrMean [j] =(int)(sum/countObs);
			else if (method == "median")
			{
				Collections.sort(medianArr);
				 int middle = medianArr.size()/2;
				    if (medianArr.size()%2 == 1) {
				    	attrMean [j] = medianArr.get(middle).intValue();
				    } else {
				    	attrMean[j] = (medianArr.get(middle-1).intValue() + medianArr.get(middle).intValue())/2;
				    }	
			}	
			else if (method == "mode")
			{ 
				int max = Integer.MIN_VALUE;
				for (int i= 1; i <= 10;i++)
				{
					int freq = Collections.frequency(modeArr, i);
					if (freq > max)
					{
						max=freq;
						attrMean [j]=i;	
					}
				}
			}
		}	
		
		
		
		
		for (int i= 0; i<X.size();i++)
		{
			for (int j= 0; j<noOfAttr;j++)
			{
				if (X.get(i).get(j).equals("?"))
				{
					if (method != "removal")
					{
						String replaceVal = String.valueOf(attrMean[j]);
						X.get(i).set(j,replaceVal);
						System.out.println("For SCN " + X.get(i).get(0) + " attribute " + j +" for record " + (i+1) + " cleaned with " + replaceVal);
					}

				}
				
			}
		}
	}
}	

