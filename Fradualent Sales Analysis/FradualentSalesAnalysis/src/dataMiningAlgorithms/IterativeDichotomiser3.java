package dataMiningAlgorithms;
import dataMiningAlgorithms.*;
import java.security.AllPermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class IterativeDichotomiser3 {
	GenericTreeNode<String> gt;
	HashMap<String, ArrayList<String>> uniqValforAll;
	ArrayList<ArrayList<String>> trainData;
	ArrayList<ArrayList<String>> testData;
	ArrayList<String> availAttr;
	String[] attrType;
	String[] headerStr;
	ArrayList<String> distinctLabel;
	ArrayList<String> attrPicked;
	public IterativeDichotomiser3(ArrayList<ArrayList<String>> train, ArrayList<ArrayList<String>> test, String[] headStr)
	{
		this.gt = new GenericTreeNode<String>();
		this.uniqValforAll = new HashMap<String, ArrayList<String>>();
		this.trainData=train;
		this.testData=test;
		this.headerStr=headStr;
		this.availAttr= new ArrayList<String>();
		this.distinctLabel= new ArrayList<String>();
		this.attrPicked = new ArrayList<String>();
	}
	
	private int getAttrIndex(String headerName)
	{
		return Arrays.asList(headerStr).indexOf(headerName);
	}
	
	private String getAttrStr(int index)
	{
		return availAttr.get(index);
	}
	
	private double entropy(double[] probArr)
	{
		double retval=0.0;
		double sum=0;
		for (int i=0; i<probArr.length;i++)
		{
			sum +=probArr[i];
		}
		
		for (int i=0; i<probArr.length;i++)
		{
			if (probArr[i] != 0.0)
				retval += -((probArr[i]/sum)* Math.log(probArr[i]/sum)/Math.log(2));
		}
		return retval;
	}
	private String pickBestFeature(ArrayList<ArrayList<String>> trainDt, ArrayList<String> avlAttr)
	{
		Double[] entropyValues = new Double[avlAttr.size()];
		HashMap<String, Double> attrCount = new HashMap<String, Double>();
		HashMap<String, Double> labelCount = new HashMap<String, Double>();
		
		for (int i =0; i < avlAttr.size();i++)
		{
			int getAttrIndex = getAttrIndex(avlAttr.get(i));
			attrCount.clear();
			labelCount.clear();
			int totalRows = trainDt.size();
			for (int j =0; j < totalRows;j++)
			{
				String attrKey = trainDt.get(j).get(getAttrIndex);
				if (attrCount.containsKey(attrKey ))
				{
					attrCount.put(attrKey , attrCount.get(attrKey )+1);
				}
				else
					attrCount.put(attrKey ,1.0);
				
				int labelIndex = trainDt.get(i).size()-1;
				String labelKey = trainDt.get(j).get(labelIndex) + "|" + attrKey;
				if (labelCount.containsKey(labelKey))
				{
					labelCount.put(labelKey, labelCount.get(labelKey)+1.0);
				}
				else
					labelCount.put(labelKey,1.0);
			}
			
			Iterator<String> keysAttr;
			keysAttr= attrCount.keySet().iterator();
			double thisAttrEntr = 0.0;
			double[] probVar = new double[distinctLabel.size()];
			while(keysAttr.hasNext())
			{
				String key=keysAttr.next();
				for (int l=0; l<distinctLabel.size();l++)
				{
					String labelKey =distinctLabel.get(l) +"|"+ key;
					try 
					{
						probVar[l]  = labelCount.get(labelKey);
					}
					catch(NullPointerException e)
					{
						probVar[l] = 0;
					}
				}
				double entAttr = entropy(probVar);
				entAttr *=  attrCount.get(key)/totalRows;
				thisAttrEntr += entAttr;
			}
			entropyValues[i] = thisAttrEntr;
		}
		double min = Double.MAX_VALUE;
		int minIndex=0;
		for (int i=0;i<entropyValues.length;i++)
		{
			System.out.print(entropyValues[i] + "\t");
			if (min > entropyValues[i])
			{
				min = entropyValues[i];
				minIndex = i;
			}
		}
		System.out.println(minIndex);
		return getAttrStr(minIndex);
	}
	
	private double computeEntropyforLabel(ArrayList<ArrayList<String>> tData, String presentAttr, String[] nodeVal)
	{
		double entrop =0;
		int attrIndex = getAttrIndex(presentAttr);
		HashMap<String, Double> labelCount = new HashMap<String, Double>();
		for (int j =0; j < tData.size();j++)
		{
			String attrKey = tData.get(j).get(attrIndex);
			if (labelCount.containsKey(attrKey))
			{
				labelCount.put(attrKey , labelCount.get(attrKey )+1);
			}
			else
				labelCount.put(attrKey ,1.0);
		}	
		//System.out.println(labelCount);
		Iterator<String> keysAttr;
		keysAttr= labelCount.keySet().iterator();
		double[] probVar = new double[distinctLabel.size()];
		int i=0;
		while(keysAttr.hasNext())
		{
			String key=keysAttr.next();

			probVar[i] = labelCount.get(key);
			i++;
		}	
		entrop = entropy(probVar);
		
		if (entrop == 0.0)
			nodeVal[0] = (String) labelCount.keySet().toArray()[0];
		return entrop;
	}
	
	private String mostCommonLabel(ArrayList<ArrayList<String>> trainData)
	{
		HashMap<String, Double> labelCount = new HashMap<String, Double>();
		for (int i=0; i<trainData.size();i++)
		{			
			int labelIndex = trainData.get(i).size()-1;
			String attrKey = trainData.get(i).get(labelIndex);
			if (labelCount.containsKey(attrKey))
			{
				labelCount.put(attrKey , labelCount.get(attrKey )+1);
			}
			else
				labelCount.put(attrKey ,1.0);
		}	
		
		Iterator<String> keysAttr;
		keysAttr= labelCount.keySet().iterator();
		double max = Double.MIN_VALUE;
		String maxLabel=null;
		while(keysAttr.hasNext())
		{
			String key=keysAttr.next();
			double val = labelCount.get(key);
			if (max < val)
			{
				max = val;
				maxLabel=key;
			}
		}	
		return maxLabel;
	}
	
	private ArrayList<ArrayList<String>>  getRecordsfor(ArrayList<ArrayList<String>> eg, String Attr, String Value)
	{
		ArrayList<ArrayList<String>>  reqData = new ArrayList<ArrayList<String>>();
		int index = getAttrIndex(Attr);
		for (int i=0; i < eg.size();i++)
		{
			if (eg.get(i).get(index).equalsIgnoreCase(Value))
				reqData.add(eg.get(i));
		}
		
		return reqData;
	}
	
	private void computeDistinctVals(String[] aType)
	{
		this.attrType=aType; 
		// Getting parameters ready for Decision Tree to be created.
		for (int i =0; i <attrType.length;i++)
		{
			if (!attrType[i].equalsIgnoreCase("u")  && !attrType[i].equalsIgnoreCase("l"))
				availAttr.add(headerStr[i]);
		}
		
		for (int i=0 ; i<trainData.get(0).size();i++)
		{
			for (int j =0 ; j < trainData.size();j++)
			{
				String data = trainData.get(j).get(i);
				if (!attrType[i].equalsIgnoreCase("u"))
				{
					if (uniqValforAll.containsKey(headerStr[i]))
					{
						
						if(!uniqValforAll.get(headerStr[i]).contains(data))
							uniqValforAll.get(headerStr[i]).add(data);
					}
					else
					{
						uniqValforAll.put(headerStr[i], new ArrayList<String>());
						uniqValforAll.get(headerStr[i]).add(data);
					}
				}
				String label = trainData.get(j).get(trainData.get(i).size()-1);
				if (!distinctLabel.contains(label))
					distinctLabel.add(label);

			}

		}
		System.out.println(uniqValforAll);
	}
	private GenericTreeNode<String> build(ArrayList<ArrayList<String>> examples, ArrayList<String> avlAttr)
	{
		GenericTreeNode<String> root = new GenericTreeNode<String>();
		String[] NodeVal = new String[1]; 
		double entropyOfthis = computeEntropyforLabel(examples,headerStr[headerStr.length-1],NodeVal);
		if (entropyOfthis == 0.0)
		{
			root.setData(NodeVal[0]);
			//System.out.println("Entropy came to be zero");
			return root; 
		}
			
		else if (avlAttr.isEmpty())
		{
			root.setData(mostCommonLabel(examples));
			//System.out.println("Attributes exhausted");
			return root;
		}

		else
		{
			//System.out.println("Both entropy is non-zero and attr available");
			String attrName = pickBestFeature(examples,avlAttr);
			avlAttr.remove(attrName);
			attrPicked.add(attrName);
			root.setData(attrName);
			ArrayList<String> distinctVal = uniqValforAll.get(attrName);
			for (String attrVals : distinctVal)
			{				
				ArrayList<ArrayList<String>> examplesforThis = getRecordsfor(examples, attrName, attrVals);
				for(int p=0;p<examplesforThis.size();p++)
					//System.out.println(examplesforThis.get(p));
				if (examplesforThis.isEmpty())
				{
					root.addChild(new GenericTreeNode<String>(mostCommonLabel(examples),attrVals));
					continue;
				}
					
				else
				{
					GenericTreeNode<String> child = build(examplesforThis ,avlAttr);
					child.setTest(attrVals);
					root.addChild(child);
				}
					
			}
		}
		return root;
	}
	
	public void buildDecisionTree(String[] aType)
	{
		computeDistinctVals(aType);
		this.gt = build(trainData,availAttr);
		
		System.out.println("#################################################################");
		System.out.println("Your Decision Tree looks like below: Parent: [child1,child2,... ]");
		System.out.println("#################################################################");
		System.out.println("Attr Picked in Order:\t" +attrPicked.toString());
		traverse(gt);
		  
		  
		  
		
	}
	
	private void traverse(GenericTreeNode<String> node)
	{
	    if(node == null)
	        return;
	    System.out.println(node.toStringVerbose());
	    for(GenericTreeNode<String> child : node.getChildren()) 
	    {
	        traverse(child);
	    }
	
	}
	
	public String classifyAnInstance (ArrayList<String> inst)
	{
		GenericTreeNode<String> node=gt;
		while (true)
		{
			if (!node.hasChildren())
			{
				return (node.getData());
			}
			int index = getAttrIndex(node.getData());
			for(GenericTreeNode<String> child : node.getChildren())
			{
				if (child.getTest().equalsIgnoreCase(inst.get(index)))
				{
					node = child;
					break;
				}
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
}


