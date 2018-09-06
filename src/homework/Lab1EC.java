package homework;

import java.util.Random;

public class Lab1EC
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
			float z = random.nextFloat();
			// I decided to try if/ else if for this block since
			// I used switch in the other assignment. Is this the way
			// it is supposed to look?
			if (z<=0.12)
			{
				threemer = threemer + "A";
			} else if (z<=0.5)
			{
				threemer = threemer + "C";
			} else if (z<=0.89)
			{
				threemer = threemer + "G";
			} else 
			{
				threemer = threemer + "T";
			}
			
			}
			System.out.println(threemer);
			if (threemer.equals("AAA"))
			{
			i++;	
			}
			
		}
		// We would expect to see the 3mer AAA by chance 
		// .12^3 percent of the time (0.1728%). For 1000 3mers, 
		// we would expect to see 1.728 AAAs. The number produced by this
		// code varies, but doesn't stray far from 2.
		System.out.println("Number of AAA:" + i);	
	}

}
