import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import de.nixosoft.jlr.JLRConverter;
import de.nixosoft.jlr.JLRGenerator;
import de.nixosoft.jlr.JLROpener;

public class debseal_MLfall2013 {

	public static void printLaTexOutput(ArrayList<ArrayList<Integer>> datatoPassInp, ArrayList<ArrayList<Double>> datatoPassOut)
	{
        File workDir = new File("src" + File.separator);
        File template = new File(workDir.getAbsolutePath() + File.separator + "javaOutputTemplate.tex");
        //String current = System.getProperty("user.dir");
        File tempDir = new File(workDir.getAbsolutePath() + File.separator + "temp");
        if (!tempDir.isDirectory()) {
            tempDir.mkdir();
        }
        
        File output1 = new File(tempDir.getAbsolutePath() + File.separator + "output1.tex");
        //File pdf1 = new File(tempDir.getAbsolutePath() + File.separator);
        File desktop = new File(System.getProperty("user.home")	+ File.separator + "Desktop");
        
        for(ArrayList<Double> obj : datatoPassOut)
        {
        	System.out.println("value" + obj.get(0)+ obj.get(1)+ obj.get(2)+ obj.get(3));
        }
        	
        
        try 
        {
            JLRConverter converter = new JLRConverter(workDir);
            JLRGenerator pdfGen = new JLRGenerator();
            converter.replace("Name", "Debpriya Seal");
            converter.replace("inpXY", datatoPassInp);
            converter.replace("outVal", datatoPassOut);
            converter.parse(template, output1);
            pdfGen.generate(output1, desktop, workDir);
            File pdf1 = pdfGen.getPDF();
            JLROpener.open(pdf1);
        } 
        catch (IOException ex) 
		{ 
        	System.err.println(ex.getMessage());
		}
            
	}
	public static void generateTrainingData(int inpX[][], int outY[],int obs)
	{	
		
		for (int i = 0;i<obs;i++)
		{
			inpX[i][0]=1;
			for (int j =1;j<3;j++)
			{
				inpX[i][j]= (int)(Math.random() * 99);
			}
		outY[i]= (int)(Math.random() * 2);
		}
	}
	
	public static double[] computeIntialTheta(int[][] X, int [] Y, int obs)
	{
		/* Making below Assumptions: 
		 * a) All the points below the line are 0 and above are 1.
		 * b) Let my hypothesis be  h(x)=ax1 + bx2 + c
		 */
		double radians, degree, m, predBase=0.0;
		int strtX1= (int)(Math.random() * 99),noOfDeg;
		int strtX2= (int)(Math.random() * 99),index=0;
		double[][] theta = new double[8][3];
		double[] perctRight = new double[8];
		for (degree=0.0,noOfDeg=0 ; degree<= 315.0; degree += 45.0, noOfDeg++)
		{
			radians = Math.toRadians(degree);
			m = Math.round(Math.tan(radians));
			theta[noOfDeg][0]= m*strtX2 - strtX1;
			theta[noOfDeg][1]= 1;
			theta[noOfDeg][2]= -m;
			//System.out.println(theta[noOfDeg][0] + "," + theta[noOfDeg][1]+","+theta[noOfDeg][2]);
			int correctPred=0,pointRelLine=0;
			for (int i = 0;i<obs;i++)
			{
				//System.out.println(+ X[i][0]+ "," + X[i][1] + "," + X[i][2]+":"+Y[i]);
				for (int k=0;k<3;k++)
				{
					pointRelLine += theta[noOfDeg][k] * X[i][k];
				}
				//System.out.println("pointRelLine" + pointRelLine); 
				if ((pointRelLine >= 0 && Y[i] == 1) || (pointRelLine < 0 && Y[i] == 0))
				{
					correctPred++;
				}
			
			}	
			perctRight[noOfDeg] = (double)correctPred/obs * 100;
			//System.out.println("Percent of right prediction:" + correctPred+ ","+ obs + ","+ perctRight[noOfDeg]);
			
			if (perctRight[noOfDeg] > predBase)
			{
				predBase = perctRight[noOfDeg]; 
				index = noOfDeg;
			}
		}

		return theta[index]; 
	}
	
	public static void printData(int inpX[][],int outY[],int k)
	{
		System.out.println("***********************************\n\t\tDelta\t\n***********************************" );
		System.out.println("smallDelta\t label(l)" );
		for (int i = 0;i<k;i++)
		{
			System.out.println(inpX[i][0] + "," + inpX[i][1]+ "," + inpX[i][2] + "\t\t|\t" + outY[i]);
		}
	}
	
	public static double sigmoid(double hx)
	{
	    return 1 / (1 + Math.exp(-hx));
	}
	
	public static double hForX(int X[], double theta[])
	{
		
		double h = X[0]*theta[0] + X[1]*theta[1] + X[2]*theta[2];
		//System.out.println("value of h :" + h);
		return sigmoid(h);
	}
	
	public static double computeCost(double theta[], int X[][], int Y[],int k)
	{
		double Cost = 0;
		for (int i =0 ; i < k;i++)
		{
			//System.out.println(+ X[i][0]+ "," + X[i][1] + "," + X[i][2]+":"+Y[i]);
			double h = hForX(X[i],theta);
			//System.out.println( "hForX :" + h);
			Cost += (Y[i] * ((h==0)?-Double.MAX_VALUE:Math.log(h))) + ((1 - Y[i]) *( (h==1)?Double.MAX_VALUE:Math.log(1 - h)));
			//System.out.println( "Cost :" + Cost);
		}
		
		return -Cost/k;
		
	}
	public static double partDervCostFunc(int X[][], double theta[], int Y[], int featureNo)
	{
		double adjust=0;
		for (int i = 0 ; i < X.length; i++)
		{
			adjust += (hForX(X[i], theta)-Y[i]) * X[i][featureNo];
		}
		return adjust;
	}
	
	public static double[] updateTheta(double theta[], double lrate, int inpX[][], int outY[])
	{
		for (int i =0 ; i<theta.length ;i++)
		{
			theta[i] -= lrate * partDervCostFunc(inpX,theta,outY, i);
			
		}
		return theta;
	}
	
	public static void main (String[] args)
	{
		/*
		 * http://docs.oracle.com/javase/6/docs/api/java/util/Random.html
		 * It says that random function picks value with uniform probability
		 */
		int lowerBound = 5, higherBound=15, numberOfIter=10, loopCtr=0;
		
		double[] initialTheta= new double[3];
		int k = lowerBound + (int)(Math.random() * ((higherBound - lowerBound) + 1));
		k=12;
		
		int[][] inpX= new int[k][3];
		int[] outY= new int[k];
				
		generateTrainingData(inpX,outY,k);
		
		printData(inpX,outY,k);
		
		initialTheta=computeIntialTheta(inpX, outY,k);
		double initialCost = computeCost(initialTheta,inpX,outY,k);
		double costOfAlgo = initialCost ;
		double[] theta = initialTheta;
		int lrate=1;
		System.out.println("\n\n*********************************************************\nfCap\t\t\t\t\t\tCorrectness\t\n*********************************************************" );
		System.out.println(theta[0] + "," +theta[1] + "," +theta[2] + "\t|\t" + costOfAlgo);
	    ArrayList<ArrayList<Double>> datatoPassOutput = new ArrayList<ArrayList<Double>>();
	    
	    ArrayList<Double> computedVal= new ArrayList<Double>();
		computedVal.add(theta[0]);
		computedVal.add(theta[1]);
		computedVal.add(theta[2]);
		computedVal.add(costOfAlgo);
		datatoPassOutput.add(computedVal);
		
		while ((costOfAlgo > 2.0 || costOfAlgo < 0 ) && loopCtr < numberOfIter)
		{
			//System.out.println("++++++++++++++++++" + loopCtr);
			//System.out.println("costOfAlgo: " + costOfAlgo);
			theta = updateTheta(theta, lrate, inpX, outY);
			costOfAlgo = computeCost(theta,inpX,outY,k);
			ArrayList<Double> computedVal1= new ArrayList<Double>();
			computedVal1.add(theta[0]);
			computedVal1.add(theta[1]);
			computedVal1.add(theta[2]);
			computedVal1.add(costOfAlgo);
			datatoPassOutput.add(computedVal1);
			System.out.println(theta[0] + "," +theta[1] + "," +theta[2] + "\t|\t" + costOfAlgo);
			loopCtr++;
			
		}
		
        ArrayList<ArrayList<Integer>> datatoPassInput = new ArrayList<ArrayList<Integer>>();
        for (int i=0;i < outY.length;i++)
        {
	        	ArrayList<Integer> trainingData= new ArrayList<Integer>();
	        	trainingData.add(inpX[i][0]);
	        	trainingData.add(inpX[i][1]);
	        	trainingData.add(inpX[i][2]);
	        	trainingData.add(outY[i]);
	        	datatoPassInput.add(trainingData);
        }
        
        for(ArrayList<Double> obj : datatoPassOutput)
        {
        	System.out.println("value" + obj.get(0)+ obj.get(1)+ obj.get(2)+ obj.get(3));
        }
		printLaTexOutput(datatoPassInput,datatoPassOutput);
		
	}
}