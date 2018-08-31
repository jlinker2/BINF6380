package homework;

import java.util.Random;

public class Lab1
{
	

	public static void main(String[] args)
	{
		Random random = new Random();
		int i = 0;
		for (int x=0; x<1000; x++)
		{
			
			String threemer = "";
			for (int y=0; y<3; y++)
			{
			int	z = random.nextInt(4);
			switch (z)
				{
				case 0: threemer = threemer + "A";
				break;
				case 1: threemer = threemer + "T";
				break;
				case 2: threemer = threemer + "G";
				break;
				case 3: threemer = threemer + "C";
				break;
				default: System.out.println("You've messed something up.");
				}
			
			}
			System.out.println(threemer);
			if (threemer.equals("AAA"))
			{
			i++;	
			}
			
		}
		// We would expect to see the 3mer AAA by chance 
		// 1/4^3 percent of the time (1.5625%). For 1000 3mers, 
		// we would expect to see 15 or 16 AAAs. The number produced by this
		// code varies (hooray for randomness!), but doesn't stray far from 16. 
		System.out.println("Number of AAA:" + i);	
	}

}
