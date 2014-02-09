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
public class ClusteringAlgo {

	
	public static ArrayList<ArrayList<BigDecimal>> stringToBigDecimal(ArrayList<ArrayList<String>> X)
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
	public static void compareControl(ArrayList<ArrayList<String>> myControl, ArrayList<ArrayList<String>> givenControl)
	{
		boolean found=false;
		int presentInMine=0;
		int presentInBoth=0;
		int presentInGiven=0;
		ArrayList<ArrayList<BigDecimal>> myC = new ArrayList<ArrayList<BigDecimal>>();
		ArrayList<ArrayList<BigDecimal>> givenC = new ArrayList<ArrayList<BigDecimal>>();
		
		
		myC= stringToBigDecimal(myControl);
		
		givenC= stringToBigDecimal(givenControl);
		for (int i=0; i< myC.size(); i++)
		{
			found=false;
			for (int j=0; j< givenC.size(); j++)
			{
				if (myC.get(i).containsAll(givenC.get(j)))
				{
					found=true;
					presentInBoth++;
					break;
				}	
			}
			if(!found)
				presentInMine++;
		}

		for (int i=0; i< givenC.size(); i++)
		{
			found=false;
			for (int j=0; j< myC.size(); j++)
			{
				if (givenC.get(i).containsAll(myC.get(j)))
					found=true;
			}	
			if(!found)
				presentInGiven++;
		}
		
		System.out.println("Number of datapoints present in both\t:" + presentInBoth);
		System.out.println("Number of datapoints present only in our computed control\t:" + presentInMine);
		System.out.println("Number of datapoints present only in our given control\t:" + presentInGiven);
	}
	public static void printInLatex()
	{
		
		String workDir= System.getProperty("user.dir");
		System.out.println(workDir);
		File template = new File(workDir + File.separator + "javaOutputTemplate.tex");
		File output1 = new File(template.getAbsolutePath() + File.separator + "output.tex");
		File desktop = new File(workDir	+ File.separator + "Desktop");
		
	}
	public static void  dataInVFold(ArrayList<ArrayList<String>> X, ArrayList<ArrayList<String>> train, ArrayList<ArrayList<String>> test,int vFold, int foldNumber)
	{
		test.clear();
		train.clear();
		int fold = (int)Math.round(X.size()/(double)vFold);
		int end = (fold * foldNumber);
		int start = end - fold;
		for (int i = 0; i < X.size();i++)
		{
			if (i>=start && i<end) 
				test.add(X.get(i));
			else 
				train.add(X.get(i));
		}
	}

	
	public static ArrayList<ArrayList<String>>  astroPickGalaxyType(ArrayList<ArrayList<String>> X, int galaxyType)
	{
		ArrayList<ArrayList<String>> updInpX = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < X.size();i++)
		{
			int galType = Integer.parseInt(X.get(i).get(1));
			if (galType == galaxyType)
			{
				updInpX.add(X.get(i));
			}
			
		}
		return updInpX;
	}
	
	public static int astroDistanceFunc (ArrayList<String> mTuple, ArrayList<ArrayList<Double>> candidate)
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
	
	
	public static ArrayList<ArrayList<String>> astroControlAlgo (ArrayList<ArrayList<String>> main, ArrayList<ArrayList<String>> candidate)
	{
		ArrayList<ArrayList<Double>> cand = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<String>> control = new ArrayList<ArrayList<String>> ();
		int index=0;
		int rowCounter = 0 ;
		
		/*
		 * Converting string to double Arraylist
		 */
		for (int i = 0; i < candidate.size();i++)
		{
			cand.add(new ArrayList<Double>());
			for (int j = 0; j < candidate.get(i).size();j++)
				cand.get(i).add(Double.parseDouble(candidate.get(i).get(j)));
		}
	
		/*
		 * Computing the distance
		 */
		for (int i =0 ; i < main.size();i++)
		{
			index = astroDistanceFunc(main.get(i), cand);
			control.add(new ArrayList<String>());
			control.get(rowCounter).addAll(candidate.get(index));
			rowCounter++;
		}
		
		return control;
	}
	public static int eucledianDistance(ArrayList<String> dataPoints, ArrayList<ArrayList<Double>> centroids)
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
	
	public static void printData(ArrayList<ArrayList<String>> inpX , ArrayList<String> outY )
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
	
	public static void printClusterGroup(ArrayList<ArrayList<ArrayList<String>>> clusterGroup  ,int k)
	{
		System.out.println();
		for (int l= 0;l<k;l++)
		{	
			System.out.println("Number of dataPoints in Cluster # " + l + "\t:" + clusterGroup.get(l).size());
		}
	}
	
	
	public static boolean isStable(ArrayList<ArrayList<Double>> prevCent, ArrayList<ArrayList<Double>> Cent)
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
	
	public static double computeWeightedPPV(ArrayList<ArrayList<String>> data, ArrayList<ArrayList<String>> X, ArrayList<String> Y,ArrayList<ArrayList<ArrayList<String>>> clusterGroup, ArrayList<ArrayList<Double>> centroids)
	{
		int j, truePositive=0, falsePositive=0; double sum;
		double ppv;
		String[] clusterClass = new String[clusterGroup.size()];
		for (int i = 0 ; i <clusterGroup.size();i++)
		{
			sum = 0;
			for (j = 0 ; j <clusterGroup.get(i).size();j++)
			{
				for (int k = 0 ; k <X.size();k++)
				{
					if (X.get(k).equals(clusterGroup.get(i).get(j)))
					{
						sum += Double.parseDouble(Y.get(k));
						// Because there are many tuples with same n attributes.
						break;
					}
				}
			}
			if (Math.abs(sum/j - 4) <  Math.abs(sum/j - 2)) 
				clusterClass[i]= "4";
			else 
				clusterClass[i]= "2";
		}
		
		for (int k = 0 ; k <data.size();k++)
		{
			int index = eucledianDistance(data.get(k), centroids);
			for (int i = 0 ; i <X.size();i++)
			{
				if (data.get(k).equals(X.get(i)))
				{
					if (Y.get(i).equals(clusterClass[index])) 
						truePositive++;
					else 
						falsePositive++;
					break;
				}
			}
		}	
		ppv = ((double)truePositive / (truePositive + falsePositive));
		System.out.println("truePostive \t:" + truePositive);
		System.out.println("falsePositive\t: " + falsePositive);
		System.out.println("PPV \t: " + ppv);
		return ppv;
	}
	
	
	
	public static void computePPV(ArrayList<ArrayList<String>> X, ArrayList<String> Y,ArrayList<ArrayList<ArrayList<String>>> clusterGroup)
	{
		int j, truePositive=0, falsePositive=0; double sum;
		double ppv;
		String[] clusterClass = new String[clusterGroup.size()]; 
		for (int i = 0 ; i <clusterGroup.size();i++)
		{
			sum = 0;
			for (j = 0 ; j <clusterGroup.get(i).size();j++)
			{
				for (int k = 0 ; k <X.size();k++)
				{
					if (X.get(k).equals(clusterGroup.get(i).get(j)))
					{
						sum += Double.parseDouble(Y.get(k));
						// Because there are many tuples with same n attributes.
						break;
					}
				}
			}
			if (Math.abs(sum/j - 4) <  Math.abs(sum/j - 2)) clusterClass[i]= "4";
			else clusterClass[i]= "2";
		}
		
		for (int i = 0 ; i <clusterGroup.size();i++)
		{
			for (j = 0 ; j <clusterGroup.get(i).size();j++)
			{
				for (int k = 0 ; k <X.size();k++)
				{
					if (X.get(k).equals(clusterGroup.get(i).get(j)))
					{
						if (Y.get(k).equals(clusterClass[i])) 
						{
							truePositive++;
						}
						else falsePositive++;
						break;
					}
				}	
			}
		}
		ppv = ((double)truePositive / (truePositive + falsePositive));
		System.out.println("truePostive \t:" + truePositive);
		System.out.println("falsePositive\t: " + falsePositive);
		System.out.println("PPV \t: " + ppv);
	}
	public static void printCentroids(ArrayList<ArrayList<Double>> cent)
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
	public static ArrayList<ArrayList<ArrayList<String>>>  kMeanClusterAlgo(ArrayList<ArrayList<String>> X, int k, String distType, int iterCount, ArrayList<ArrayList<String>> cent, ArrayList<ArrayList<Double>> finalCentroids)
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
	
	
	public static void getData(String fileName, ArrayList<ArrayList<String>> inpX , ArrayList<String> outY, boolean header )
	{ 
		boolean hdrChk = header;
		String dirName = System.getProperty("user.dir");
		System.out.println(dirName);
		String csvFile = dirName + "/src/" + fileName;
		BufferedReader br = null;
		String line = "";
		String delimiter = ",";
		int rowCount=0, lengthofAttr=0;
		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) 
			{
				// Skipping the header name.
				if (header)
				{
					header = false;
					continue;
				}
				
				String[] attr= line.split(delimiter);
				if (hdrChk)
					lengthofAttr=attr.length;
				else 
					lengthofAttr=attr.length-1;
				int i;
				inpX.add(new ArrayList<String>());
				for (i =0 ; i < lengthofAttr; i++ )
				{
					inpX.get(rowCount).add(attr[i]);
				}
				
				if (outY != null) outY.add(attr[i]);
				//else outY.add("NA");
				rowCount++;
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Data Pulled Successfully!!!");
	}
	
	public static ArrayList<ArrayList<String>>  pickAttributes(ArrayList<ArrayList<String>> X, int start, int end)
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
	
	public static void dataCleansing(ArrayList<ArrayList<String>> X, String method)
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
	
	
	public static void main (String[] args)
	{
		final long startTime = System.currentTimeMillis();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//get current date time with Date()
		Date dateStart = new Date();
		ArrayList<ArrayList<ArrayList<String>>> clusterGroup = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<String>> inpX = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> inpXTemp = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> trainDataSet = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> testDataSet = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<Double>> finalCentroids = new ArrayList<ArrayList<Double>>();
		ArrayList<String> outY = new ArrayList<String>();
		double ppvTotal=0;
		getData("WolBergsBreastCancerData.csv", inpX, outY, false);
		
		
		
		/*
		 * Problem Two  
		 * Question 5
		*/ 
		
		for (int i = 0 ; i < inpX.size(); i++)
		{  
			inpXTemp.add(new ArrayList<String>());
			inpXTemp.get(i).addAll(inpX.get(i));
		}	
		
		
		dataCleansing(inpX,"mean");
		for (int i = 1; i <=10;i++)
		{
			clusterGroup =kMeanClusterAlgo(pickAttributes(inpX, 1, 9),i,"E", 50,null,finalCentroids );
			printCentroids(finalCentroids);
			computePPV(pickAttributes(inpX, 1, 9), outY,clusterGroup);
		}	
		
		inpX.clear();
		for (int i = 0 ; i < inpXTemp.size(); i++)
		{  
			inpX.add(new ArrayList<String>());
			inpX.get(i).addAll(inpXTemp.get(i));
		}
		
		
		String[] cleanMethod = {"mean", "median","mode","removal"};
		for (int j=0;j<cleanMethod.length;j++)
		{
			ppvTotal =0;
			dataCleansing(inpX,cleanMethod[j]);
			for (int i = 9; i >=2;i-=2)
			{
				clusterGroup =kMeanClusterAlgo(pickAttributes(inpX, 1, i),2,"E", 50,null,finalCentroids );
				printCentroids(finalCentroids);
				computePPV(pickAttributes(inpX, 1, i), outY,clusterGroup);
			}
			clusterGroup =kMeanClusterAlgo(pickAttributes(inpX, 1, 2),2,"E", 50,null,finalCentroids );
			printCentroids(finalCentroids);
			computePPV(pickAttributes(inpX, 1, 2), outY,clusterGroup);
			System.out.println("===============================================================");
		
			/*
			 * Problem Two  
			 * Question 6
			*/
			for (int i = 1; i<=10 ; i++)
			{
				System.out.println("\n\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("Training .....");
				System.out.println("VFold\t:10");
				System.out.println("Fold training on \t:" + i);
				dataInVFold(inpX,trainDataSet, testDataSet,10, i);
				System.out.println("Training data of size \t:" + trainDataSet.size());
				System.out.println("Test data of size \t:" + testDataSet.size());
				finalCentroids.clear();
				clusterGroup =kMeanClusterAlgo(pickAttributes(trainDataSet, 1, 9),2,"E", 20,null,finalCentroids);
				printCentroids(finalCentroids);
				ArrayList<ArrayList<String>> centroids = new ArrayList<ArrayList<String>>();
				centroids.clear();
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("Now Testing .....");
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				ppvTotal += computeWeightedPPV(pickAttributes(testDataSet, 1, 9), pickAttributes(inpX, 1, 9), outY, clusterGroup, finalCentroids);
				System.out.println("\n===============================================================");
			}
			System.out.println("\nWeighted PPV is \t:" +  ppvTotal/10);
			System.out.println("\n===============================================================");
			inpX.clear();
			for (int i = 0 ; i < inpXTemp.size(); i++)
			{  
				inpX.add(new ArrayList<String>());
				inpX.get(i).addAll(inpXTemp.get(i));
			}
		}
		/*
		 * Problem Three: Astronomy  
		 * Question 2
		 */
		System.out.println("\nAstronomy Data being pulled......");
		ArrayList<ArrayList<String>> myMain = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> myCandidate= new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> myControl= new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> computedControl= new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<ArrayList<String>>> myclusterGroup = new ArrayList<ArrayList<ArrayList<String>>>();
		getData("main.csv", myMain, null,true);
		getData("candidates.csv", myCandidate, null,true);
		getData("control_2.csv", myControl, null,true);
		
		
		computedControl = astroControlAlgo(myMain, myCandidate);
		System.out.println("\n===============================================================");
		System.out.println("Computing control Algortihm......");
		compareControl(computedControl, myControl);
		System.out.println("\n===============================================================");
		/*
		 * Problem Three: Astronomy  
		 * Question 3
		 */

		for (int i =0; i <=2;i++)
		{
			ArrayList<ArrayList<String>> datapointsByGroupCand=astroPickGalaxyType(myCandidate, i);
			ArrayList<ArrayList<String>> datapointsbyAttributesCand=pickAttributes(datapointsByGroupCand, 4, 6);
			ArrayList<ArrayList<String>> datapointsByGroupMain=astroPickGalaxyType(myMain, i);
			ArrayList<ArrayList<String>> datapointsbyAttributesMain=pickAttributes(datapointsByGroupMain, 4, 6);
			System.out.println("===============================================================");
			System.out.println("\nFor the GalaxyType\t:" + i);
			System.out.println("Number of records in main with GalaxyType\t:" + i + "\t:" + datapointsByGroupMain.size());
			System.out.println("Number of records in candidate with GalaxyType\t:" + i + "\t:" + datapointsByGroupCand.size());
			myclusterGroup =kMeanClusterAlgo(datapointsbyAttributesCand,datapointsbyAttributesMain.size(),"R", 20,datapointsbyAttributesMain,finalCentroids);
			printClusterGroup(myclusterGroup, datapointsbyAttributesMain.size());
		}
		final long endTime = System.currentTimeMillis();
		Date dateEnd = new Date();
		System.out.println("\n===============================================================");
		System.out.println("Started at\t:" + dateFormat.format(dateStart));
		System.out.println("Started at\t:" + dateFormat.format(dateEnd));
		System.out.println("Total execution time (in minutes): " + TimeUnit.MILLISECONDS.toMinutes(endTime - startTime));
		System.out.println("===============================================================");
	}	
}
