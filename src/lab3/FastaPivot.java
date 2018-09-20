package lab3;

import java.io.*;
import java.util.*;

public class FastaPivot
{

	public static File inFile = null;

	private static List<String> seqId = new ArrayList<String>();
	private static List<String> sequence = new ArrayList<String>();

	private static List<Integer> numA = new ArrayList<Integer>();
	private static List<Integer> numC = new ArrayList<Integer>();
	private static List<Integer> numG = new ArrayList<Integer>();
	private static List<Integer> numT = new ArrayList<Integer>();

	public static void fileIn(String[] arguments) throws Exception
	{
		if (arguments.length > 0)
		{
			inFile = new File(arguments[0]);
		} else
		{
			System.out.println("You must enter a file to parse.");
			System.exit(0);
		}
		BufferedReader reader = new BufferedReader(new FileReader(inFile));

		int numSeq = -1;
		for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine())
		{
			if (nextLine.startsWith(">"))
			{
				numSeq++;
				nextLine = nextLine.substring(1);
				if (nextLine.startsWith(" "))
				{
					nextLine = nextLine.substring(1);
				}
				seqId.add(nextLine);
			} else
			{
				if (sequence.size() < numSeq + 1)
				{
					sequence.add(nextLine);
				} else
				{
					sequence.set(numSeq, sequence.get(numSeq) + nextLine);
				}
			}
		}
		reader.close();
	}

	public static void countAcgt(List<String> sequence)
	{
		for (String s : sequence)
		{
			numA.add(s.length() - s.replace("A", "").length()); // this makes me deeply happy. I wish I'd thought of it
			numC.add(s.length() - s.replace("C", "").length()); // myself.
			numG.add(s.length() - s.replace("G", "").length());
			numT.add(s.length() - s.replace("T", "").length());
		}
	}

	public static void resultsOut(List<String> seqName, List<String> sequence, List<Integer> a, List<Integer> c,
			List<Integer> g, List<Integer> t) throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("PivotOutput.txt")));
		writer.write("sequenceID\tnumA\tnumC\tnumG\tnumT\tsequence");
		writer.newLine();

		for (int i = 0; i < seqName.size(); i++)
		{
			writer.write(seqName.get(i) + "\t" + a.get(i) + "\t" + c.get(i) + "\t" + g.get(i) + "\t" + t.get(i) + "\t"
					+ sequence.get(i));
			writer.newLine();
		}
		writer.close();
	}

	public static void main(String[] args) throws Exception
	{
		fileIn(args);
		countAcgt(sequence);
		resultsOut(seqId, sequence, numA, numC, numG, numT);
	}

}
