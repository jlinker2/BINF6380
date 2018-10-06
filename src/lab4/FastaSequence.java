package lab4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FastaSequence
{
	private final String header;
	private final String sequence;
	
	public FastaSequence(String header, String sequence)
	{
		this.header = header;
		this.sequence = sequence;
	}
	
	// static factory for FastaSequence object
	public static FastaSequence makeSequence(String header, String sequence)
	{
		return new FastaSequence(header, sequence);
	}

	// returns the header of this sequence without the ">"
	public String getHeader()
	{
		return header;
	}

	// returns the DNA sequence of this object
	public String getSequence()
	{
		return sequence;
	}

	// returns the number of G's and C's divided by the length of this sequence
	public float getGCRatio()
	{
		int count = 0;
		for (int i = 0; i < this.sequence.length(); i++)
		{
			char a = this.sequence.charAt(i);
			if (a == 'G' || a == 'C')
			{
				count++;
			}
		}
		return (float) count / (float) this.sequence.length();
	}

	// takes a file 
	public static List<FastaSequence> readFastaFile(String filepath) throws Exception
	{
		List<FastaSequence> FastaList = new ArrayList<FastaSequence>();
		List<String> seqId = new ArrayList<String>();
		List<String> sequence = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(filepath));

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
		for (int i = 0; i < seqId.size(); i++)
		{
			FastaList.add(new FastaSequence(seqId.get(i), sequence.get(i)));

		}
		return FastaList;
	}

	public static void writeUnique(File inFile, File outFile) throws Exception
	{
		List<FastaSequence> fastaList = FastaSequence.readFastaFile(inFile.toString());

		HashMap<String, Integer> map = new LinkedHashMap<String, Integer>();

		for (int x = 0; x < fastaList.size(); x++)
		{
			String sequence2 = fastaList.get(x).getSequence();
			Integer count = map.get(sequence2);

			if (count == null)
			{
				count = 0;
			}
			count++;

			map.put(sequence2, count);
		}
		LinkedHashMap<String, Integer> sorted = sortByValue(map);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
		for (String seq : sorted.keySet())
		{
			String key = seq.toString();
			String value = sorted.get(seq).toString();
			writer.write(">" + value + "\n" + key);
			writer.newLine();
		}
		writer.close();
	}

	private static LinkedHashMap<String, Integer> sortByValue(Map<String, Integer> unsorted)
	{
		ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(unsorted.entrySet());

		Comparator<Map.Entry<String, Integer>> valueComparator = new Comparator<Map.Entry<String, Integer>>()
		{
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
			{
				return Integer.compare(o1.getValue(), o2.getValue());
			}

		};
		Collections.sort(list, valueComparator);

		LinkedHashMap<String, Integer> sorted = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list)
		{
			sorted.put(entry.getKey(), entry.getValue());
		}
		return sorted;

	}

	public static void main(String[] args) throws Exception
	{
		List<FastaSequence> fastaList = FastaSequence.readFastaFile("inFile");

		for (FastaSequence fs : fastaList)
		{
			System.out.println(fs.getHeader());
			System.out.println(fs.getSequence());
			System.out.println(fs.getGCRatio());
		}
		writeUnique(new File("inFile"), new File("outFile"));

	}

}
