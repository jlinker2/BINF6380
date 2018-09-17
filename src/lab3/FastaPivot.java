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
		sequenceArray = sequence.toArray(sequenceArray);
		countAcgt(sequenceArray);
		System.out.println(numA);	// for testing
		System.out.println(numC);	// for testing
		System.out.println(numG);	// for testing
		System.out.println(numT);	// for testing
	}

}