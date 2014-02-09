package dataMiningAlgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class NaiveBayesClassifier {

	//Training Data FileName
	private File fileName;
	private ArrayList<ArrayList<String>> fileData;
	private String[] headerStr;
	private boolean hasHeader;
	private ArrayList<String> strAttr;
	private ArrayList<String> strLabel;
	private double[][] contigencyMatrix;
	private String[] columnType;
	private int[] labelGroupCount;
	
	public NaiveBayesClassifier(String file, boolean HeaderFlag) {
		this.fileName=  new File (file);
		this.hasHeader = HeaderFlag;
		this.strAttr= new ArrayList<String>();
		this.strLabel= new ArrayList<String>();
		this.labelGroupCount = new int[strLabel.size()];
	}
	
	public void fileToMatrix()
	{
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
				   headerStr=colsVal.clone();	
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
		 System.out.println("Number of records read:\t" + rowIndex);
		 System.out.println("Number of columns found:\t" + colsVal.length);
	}
	
	public void printMatrix()
	{
		for (int i=0; i < fileData.size();i++)
				System.out.println(fileData.get(i));
	}
	
	public void cleanData( String cleanFlag)
	{
		System.out.println("\n\n############################");
		System.out.println("Cleaning Data..........");
		System.out.println("############################");
		int[]  ColNumMissCount = new int[fileData.get(0).size()];
		for (int i=0; i < fileData.get(i).size();i++)
		{
			ColNumMissCount[i]=0;
			for (int j=0; j < fileData.size();j++)
			{
				if (fileData.get(j).get(i).equalsIgnoreCase("NA"))
				{
					if (cleanFlag.equalsIgnoreCase("ignore"))
						fileData.remove(fileData.get(i));
						
					ColNumMissCount[i]++;
				}
					
			}
		}
		
		for (int i=0;i<ColNumMissCount.length;i++)
		{
			System.out.println("For column " + (i+1) + " there are " + ColNumMissCount[i] + " missing values");
		}
		
		if (cleanFlag.equalsIgnoreCase("ignore"))
		{
			System.out.println("\nMethod used to clean data:\t" + cleanFlag);
			System.out.println("Number of records post dropping records with missing values:\t" + fileData.size());
		}
			
				
	}

	private int getStrIndex(ArrayList<String> str, String toSearch)
	{
		for( int i=0; i < str.size();i++)
		{
			if (str.get(i).equalsIgnoreCase(toSearch))
				return i;
		}
		return -1;
	}
	
	private String getStrFromIndex(ArrayList<String> str, int index)
	{
		return str.get(index);
	}

	public void computeContigencyMatrix(ArrayList<String> strAttr, ArrayList<String> strLabel, String[] colType)
	{
		this.contigencyMatrix = new double[strAttr.size()][strLabel.size()];
		int colTypeIndex=0;
		int labelIndex= fileData.get(0).size()-1;
		labelGroupCount = new int[strLabel.size()]; 
		for (int i=0; i < fileData.get(i).size();i++)
		{
			for (int j=0; j < fileData.size();j++)
			{
				int colIndex= getStrIndex(strLabel, fileData.get(j).get(labelIndex));
				if (colType[colTypeIndex].equalsIgnoreCase("u"))
					break;
				
				else if (colType[colTypeIndex].equalsIgnoreCase("d"))
				{
					String val = headerStr[colTypeIndex] + "_" + fileData.get(j).get(i);
					
					int rowIndex= getStrIndex(strAttr, val);
					contigencyMatrix[rowIndex][colIndex]++;
				}
				
				else if  (colType[colTypeIndex].equalsIgnoreCase("c"))
				{
					String val = headerStr[colTypeIndex] + "_" + "Mean";
					
					int rowIndex= getStrIndex(strAttr, val);
					double valNumber = Double.parseDouble(fileData.get(j).get(i));
					contigencyMatrix[rowIndex][colIndex] += valNumber;
				}
				
				if (i == 4)
				labelGroupCount[colIndex]++;
			}
			colTypeIndex++; 

		}
		
		//Computing Variance
		colTypeIndex=0;
		int colIndex=0,rowIndex=0;
		for (int i=0; i < fileData.get(i).size();i++)
		{
			for (int j=0; j < fileData.size();j++)
			{
				if (colType[colTypeIndex].equalsIgnoreCase("c"))
				{
					String val = headerStr[colTypeIndex] + "_Variance";
					colIndex= getStrIndex(strLabel, fileData.get(j).get(labelIndex));
					rowIndex= getStrIndex(strAttr, val);				
					int meanRowIndex = getStrIndex(strAttr, new String(headerStr[colTypeIndex] + "_Mean"));
					double valNumber = Double.parseDouble(fileData.get(j).get(i));
					contigencyMatrix[rowIndex][colIndex] += Math.pow((valNumber - contigencyMatrix[meanRowIndex][colIndex]/labelGroupCount[colIndex]),2);
				}
				else 
					break;
			}
			colTypeIndex++;
		}
		
		//Dividing by the label group count
		for (int i=0; i < contigencyMatrix[i].length;i++)
		{
			for (int j=0; j < contigencyMatrix.length;j++)
			{
				if (getStrFromIndex(strAttr,j).endsWith("Variance"))
					contigencyMatrix[j][i] /= (labelGroupCount[i]-1);
				else
					contigencyMatrix[j][i] /= labelGroupCount[i];
			}
		}	
		
		System.out.print("\t\t\t\t");
		for (int i =0; i <strLabel.size();i++)
			System.out.format("%10s",strLabel.get(i));
			//System.out.print(strLabel.get(i) + "\t");
		System.out.println();
		System.out.println("---------------------------------------------");
		for (int i =0; i <contigencyMatrix.length;i++)
		{
			System.out.format("%10s",strAttr.get(i));
			for (int j =0; j <contigencyMatrix[i].length;j++)
			{
				System.out.format("%30s",contigencyMatrix[i][j]);
			}
			System.out.println();
		}
	}
	
	public void train(String[] colType)
	{
		this.columnType=colType;
		fileToMatrix();
		int colTypeIndex=0;
		for (int i=0; i < fileData.get(i).size();i++)
		{
			for (int j=0; j < fileData.size();j++)
			{
				if (colType[colTypeIndex].equalsIgnoreCase("u"))
					break;
				
				else if (colType[colTypeIndex].equalsIgnoreCase("d"))
				{
					String val = headerStr[colTypeIndex] + "_" + fileData.get(j).get(i);
					if (!strAttr.contains(val))
					{
						strAttr.add(val);
					}
				}	
				else if (colType[colTypeIndex].equalsIgnoreCase("c"))
					{
						strAttr.add(headerStr[colTypeIndex] + "_Mean");	
						strAttr.add(headerStr[colTypeIndex] + "_Variance");
						break;
					}
				else if (colType[colTypeIndex].equalsIgnoreCase("l"))
				{
					String val = fileData.get(j).get(i);
					if (!strLabel.contains(val))
					{
						strLabel.add(val);
					}
				}	
			}
			colTypeIndex++;
		}
		
		System.out.println(strAttr);
		System.out.println(strLabel);
		
		computeContigencyMatrix(strAttr,strLabel,colType);
	}
	
	private double getProbability(String AttrVal, int Label, String columnName)
	{
		String rowHeader = columnName + "_" + AttrVal;
		int index= getStrIndex(strAttr, rowHeader);
		if (index != -1)
			return contigencyMatrix[index][Label];
		else
		{
			int meanIndex= getStrIndex(strAttr, new String(columnName + "_Mean"));
			int varIndex= getStrIndex(strAttr, new String(columnName + "_Variance"));
			return Math.exp(-Math.pow((Double.parseDouble(AttrVal)-contigencyMatrix[meanIndex][Label]),2)/(2*contigencyMatrix[varIndex][Label]))
					/Math.sqrt(2*Math.PI*contigencyMatrix[varIndex][Label]);
		}
			
		
	}
	public String classify(String[] singleTestRecord)
	{
		double probClass[]= new double[strLabel.size()];
		double totalLabelCount=0;
		for (int i=0;i<probClass.length;i++)
		{
			probClass[i]=1.0;
			totalLabelCount += labelGroupCount[i];
		}
			
			
		for (int i=0;i<contigencyMatrix[i].length;i++)
		{
			for (int j=0;j<singleTestRecord.length;j++)
			{
				if (columnType[j].equalsIgnoreCase("u"))
					continue;
				probClass[i] *= getProbability(singleTestRecord[j], i, headerStr[j]);
			}
			probClass[i] *= (labelGroupCount[i]/totalLabelCount);
		}
		System.out.println(probClass[0] + ":"+probClass[1]);
		int maxIndex=Integer.MIN_VALUE;
		double max = Double.MIN_VALUE;
		for (int i=0;i<probClass.length;i++)
		{
			if (max < probClass[i])
			{
				maxIndex=i;
				max=probClass[i];
			}		
		}
		return getStrFromIndex(strLabel, maxIndex);
	}
	public static void main (String[] args)
	{
		NaiveBayesClassifier nb = new NaiveBayesClassifier("src//LoanDefault.csv",true);
		//nb.cleanData("Ignore");
		nb.train(new String[]{"u","d","d","c","l"});
		String cls= nb.classify(new String[]{"0","No","Married","120"});
		System.out.println(cls);
		
	}
}

