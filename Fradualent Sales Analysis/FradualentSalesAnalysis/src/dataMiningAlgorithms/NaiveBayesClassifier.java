package dataMiningAlgorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class NaiveBayesClassifier {

	//Training Data FileName
	private ArrayList<ArrayList<String>> fileData;
	private String[] headerStr;
	private ArrayList<String> strAttr;
	private ArrayList<String> strLabel;
	private double[][] contigencyMatrix;
	private String[] columnType;
	private int[] labelGroupCount;

	public NaiveBayesClassifier() {

		this.strAttr= new ArrayList<String>();
		this.strLabel= new ArrayList<String>();
		this.labelGroupCount = new int[strLabel.size()];
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

	private void computeContigencyMatrix(ArrayList<String> strAttr, ArrayList<String> strLabel, String[] colType, HashMap<String,Integer> attrDisntCount) 
	{
		this.contigencyMatrix = new double[strAttr.size()][strLabel.size()];
		int colTypeIndex=0;
		int labelIndex= fileData.get(0).size()-1;
		labelGroupCount = new int[strLabel.size()];
		
		
		colTypeIndex=0;
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
				
				if (i == fileData.get(i).size()-1)
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
		
		/*
		 * Dividing by the label group count to normalize n_c/n
		 * Also, implementing m-estimate to take care of zero probabilities
		 * 
		 * A nonzero prior estimate p for Pr(A j B), and
		 * I A number m that says how confident we are of our prior 
		 * estimate p, as measured in number of samples
		 */
		
		for (int i=0; i < contigencyMatrix[i].length;i++)
		{
			for (int j=0; j < contigencyMatrix.length;j++)
			{
				if (getStrFromIndex(strAttr,j).endsWith("Variance"))
					contigencyMatrix[j][i] /= (labelGroupCount[i]-1);
				else if (getStrFromIndex(strAttr,j).endsWith("Mean"))
					contigencyMatrix[j][i] /= (labelGroupCount[i]);
				else	
				{
					String attr = getStrFromIndex(strAttr,j);
					contigencyMatrix[j][i] =  (contigencyMatrix[j][i] + attrDisntCount.get(attr) * 1/attrDisntCount.get(attr))/(labelGroupCount[i] + attrDisntCount.get(attr));
				}
			}
		}	
		
		
		
		System.out.print("\t\t\t\t");
		for (int i =0; i <strLabel.size();i++)
			System.out.format("%10s",strLabel.get(i));
			//System.out.print(strLabel.get(i) + "\t");
		System.out.println();
		System.out.println("---------------------------------------------");
		for (int i =0; i <10;i++)
		{
			System.out.format("%10s",strAttr.get(i));
			for (int j =0; j <contigencyMatrix[i].length;j++)
			{
				System.out.format("%30s",contigencyMatrix[i][j]);
			}
			System.out.println();
		}
	}
	
	public void train(ArrayList<ArrayList<String>> fileData, String[] colType, String[] headerName)
	{
		System.out.println("Training the Model....");
		this.fileData = fileData;
		this.headerStr=headerName;
		this.columnType=colType;
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
		
		HashMap<String, Integer> AttrDistinctVal = new HashMap<String, Integer> (); 
		for (int i =0; i <colType.length;i++)
		{
			if (colType[i].equalsIgnoreCase("d"))
			{
				for (int j=0;j<fileData.size();j++)
					{
						String AttrVal=headerStr[i] + "_" + fileData.get(j).get(i);
						if (AttrDistinctVal.containsKey(AttrVal))
						{
							AttrDistinctVal.put(AttrVal, AttrDistinctVal.get(AttrVal) + 1);
						}
						else
						{
							AttrDistinctVal.put(AttrVal, 1);
						}
					}
			}
			
		}
		computeContigencyMatrix(strAttr,strLabel,colType,AttrDistinctVal);
	}
	
	public ArrayList<ArrayList<String>> getFileData()
	{
		return this.fileData;
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
			
			try 
			{
				return Math.exp(-Math.pow((Double.parseDouble(AttrVal)-contigencyMatrix[meanIndex][Label]),2)/(2*contigencyMatrix[varIndex][Label]))
						/Math.sqrt(2*Math.PI*contigencyMatrix[varIndex][Label]);
			}
			catch (NumberFormatException e)
			{
				return -1;
			}
			
		}
			
		
	}
	
	public ArrayList<ArrayList<String >>  classify(ArrayList<ArrayList<String >> testData)
	{
		System.out.println("Classify Test Data....");
		ArrayList<ArrayList<String >> classifiedData = new ArrayList<ArrayList<String >>(); 
		int fraudDetect=0, okDetect=0;
		for (int i=0; i <testData.size();i++)
		{
			classifiedData.add(new ArrayList<String>());
			classifiedData.get(i).addAll(testData.get(i));
		}
			
		for (int i=0; i <testData.size();i++)
		{
			if (i % 8 == 0)
				System.out.println();
			System.out.print(i + ":" + testData.size() + "\t");
			int labelIndex=classifiedData.get(i).size()-1;
			String pred = classifyAnInstance(testData.get(i));
			if (pred.equalsIgnoreCase("ok"))
				okDetect++;
			else	
				fraudDetect++;
			classifiedData.get(i).set(labelIndex, pred);
		}
		
		System.out.println("\n\nNumber of transaction classified as Fraud:\t" + fraudDetect);
		System.out.println("Number of transaction classified as OK:\t" + okDetect);
		return classifiedData;
	}
	
	public String classifyAnInstance(ArrayList<String> singleTestRecord)
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
			for (int j=0;j<singleTestRecord.size();j++)
			{
				if (columnType[j].equalsIgnoreCase("u"))
					continue;
				double prob = getProbability(singleTestRecord.get(j), i, headerStr[j]);
				if (prob != -1)
					probClass[i] *= getProbability(singleTestRecord.get(j), i, headerStr[j]);
			}
			probClass[i] *= (labelGroupCount[i]/totalLabelCount);
		}

		int maxIndex=Integer.MIN_VALUE;
		double max = Double.MIN_VALUE;
		for (int i=0;i<probClass.length;i++)
		{
			if (max <= probClass[i])
			{
				maxIndex=i;
				max=probClass[i];
			}		
		}
		String PredLabel = getStrFromIndex(strLabel, maxIndex);
		return PredLabel;
	}
}

