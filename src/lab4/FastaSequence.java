package lab4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FastaSequence
{
	private final String header;
	private final String sequence;

	public FastaSequence(String header, String sequence)
	{
		this.header = header;
		this.sequence = sequence;
	}

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

	public static void main(String[] args) throws Exception
	{
		List<FastaSequence> fastaList = FastaSequence.readFastaFile("C:\\Users\\reeli\\git\\BINF6380\\sample2.fa");

		for (FastaSequence fs : fastaList)
		{
			System.out.println(fs.getHeader());
			System.out.println(fs.getSequence());
			System.out.println(fs.getGCRatio());
		}

	}

}
