package lab3;

import java.io.*;
import java.util.*;




public class FastaPivot
{

	private static List<Integer> numA = new ArrayList<Integer>();
	private static List<Integer> numC = new ArrayList<Integer>();
	private static List<Integer> numG = new ArrayList<Integer>();
	private static List<Integer> numT = new ArrayList<Integer>();
	
	public static void countAcgt(String[] sequence) 
	{
		for (String s : sequence)
		{
			numA.add(s.length() - s.replace("A", "").length());  //this makes me deeply happy
			numC.add(s.length() - s.replace("C", "").length());
			numG.add(s.length() - s.replace("G", "").length());
			numT.add(s.length() - s.replace("T", "").length());
		}
	}
	
	public static void resultsOut(String[] seqName, String[] sequence, List<Integer> a, List<Integer> c, List<Integer> g, List<Integer> t) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("PivotOutput.txt")));
		writer.write("sequenceID\tnumA\tnumC\tnumG\tnumT\tsequence");
		writer.newLine();
		
		for (int i = 0; i < seqName.length; i++)
		{
			writer.write(seqName[i]+"\t"+a.get(i)+"\t"+c.get(i)+"\t"+g.get(i)+"\t"+t.get(i)+"\t"+sequence[i]);
			writer.newLine();
		}
		writer.close();
	}
	
	public static void main(String[] args) throws Exception
	{
		
		File inFile = null;
		if (args.length > 0 )
		{
			inFile = new File(args[0]);
		}
		else
		{
			System.out.println("You must enter a file to parse.");
			System.exit(0);
		}
		BufferedReader reader = new BufferedReader(new FileReader (inFile));
		
		List<String> seqId = new ArrayList<String>();
		List<String> sequence = new ArrayList<String>();
		int numSeq = -1;
		for(String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine())
		{
			if (nextLine.startsWith(">"))
			{
				numSeq++;
				nextLine = nextLine.replace("> ", "");
				seqId.add(nextLine);
			}
			else
			{
				if (sequence.size()<numSeq+1)
				{
					sequence.add(nextLine);
				}
				else
				{
					sequence.set(numSeq, sequence.get(numSeq) + nextLine);	
				}
				
			}
			
		}
		reader.close();
		System.out.println(seqId);  // for testing
		System.out.println(sequence);   // for testing
		String[] sequenceArray = new String[sequence.size()];
		String[] seqIdArray = new String[seqId.size()];
//		Integer[] numAArray = new Integer[numA.size()];
//		String[] numCArray = new String[numC.size()];
//		String[] numGArray = new String[numG.size()];
//		String[] numTArray = new String[numT.size()];
		sequenceArray = sequence.toArray(sequenceArray);
		seqIdArray = seqId.toArray(seqIdArray);
//		numAArray = numA.toArray(new Integer[numAArray.length]);
//		numCArray = numC.toArray(numCArray);
//		numGArray = numG.toArray(numGArray);
//		numTArray = numT.toArray(numTArray);


		
		countAcgt(sequenceArray);
		System.out.println(numA);	// for testing
		System.out.println(numC);	// for testing
		System.out.println(numG);	// for testing
		System.out.println(numT);	// for testing  
		resultsOut(seqIdArray, sequenceArray, numA, numC, numG, numT);
	}

}
