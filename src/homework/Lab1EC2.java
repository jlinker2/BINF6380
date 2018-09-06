package homework;

import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.*;

public class Lab1EC2
{
/* Hoping that by the end of the semester there will be many 
 * fewer "static"s in my code. For now I'm just glad it works.
 */
	static String[] nucleotides = { "A", "C", "G", "T" };
	static ArrayList<String> allThreemers = new ArrayList<String>();
	static String[] threemerList;
	static String three;
	static double dbn = 0;
	static double[] expected = new double[64];
	static long[] observed = new long[64];
	static ArrayList<Double> pValues = new ArrayList<Double>();

	/*
	 * Make an array of every possible trimer.
	 */
	public static void listThreemers()
	{

		for (int x = 0; x < 4; x++)
		{
			for (int y = 0; y < 4; y++)
			{
				for (int z = 0; z < 4; z++)
				{
					three = nucleotides[x] + nucleotides[y] + nucleotides[z];
					allThreemers.add(three);
				}
			}
		}
		threemerList = allThreemers.toArray(new String[0]);
	}
	/*
	 * Make an array with expected values for our each element
	 * of the array of trimers.
	 */
	public static void chiTable(String[] args)
	{
		for (int x = 0; x < args.length; x++)
		{
			String trimer = args[x];
			dbn = 10000;
			for (int y = 0; y < 3; y++)
			{
				char n = trimer.charAt(y);
				if (n == 'A')
				{
					dbn = .12 * dbn;
				} else if (n == 'C')
				{
					dbn = .38 * dbn;
				} else if (n == 'G')
				{
					dbn = .39 * dbn;
				} else
				{
					dbn = .11 * dbn;
				}

			}
			expected[x] = dbn;
		}
	}
	/*
	 * The following four methods (computeChiSquared, pochisq, poz, and ex)
	 * copied from 
	 * http://www.vvlasov.com/2013/06/how-to-calculate-pvalue-from-chisquare.html.
	 * 
	 */
	public static double computeChiSquared(double[] e, long[] o)
	{
		double[] value = new double[64];
		double d = 0;
		for (int i = 0; i < 64; i++)
		{
			value[i] = (o[i] - e[i]) * (o[i] - e[i]) / e[i];
		}
		for (int j = 0; j < 64; j++)
		{
			d = d + value[j];
		}
		return d;
	}

	private static final double LOG_SQRT_PI = Math.log(Math.sqrt(Math.PI));
	private static final double I_SQRT_PI = 1 / Math.sqrt(Math.PI);
	public static final int MAX_X = 20; // max value to represent exp(x)
	
	/* POCHISQ -- probability of chi-square value
    Adapted from:
    Hill, I. D. and Pike, M. C. Algorithm 299
    Collected Algorithms for the CACM 1967 p. 243
    Updated for rounding errors based on remark in
    ACM TOMS June 1985, page 185
	 */
	public static double pochisq(double x, int df)
	{
		double a, s;
		double e, c, z;

		if (x <= 0.0 || df < 1)
		{
			return 1.0;
		}
		a = 0.5 * x;
		boolean even = (df & 1) == 0;
		double y = 0;
		if (df > 1)
		{
			y = ex(-a);
		}
		s = (even ? y : (2.0 * poz(-Math.sqrt(x))));
		if (df > 2)
		{
			x = 0.5 * (df - 1.0);
			z = (even ? 1.0 : 0.5);
			if (a > MAX_X)
			{
				e = (even ? 0.0 : LOG_SQRT_PI);
				c = Math.log(a);
				while (z <= x)
				{
					e = Math.log(z) + e;
					s += ex(c * z - a - e);
					z += 1.0;
				}
				return s;
			} else
			{
				e = (even ? 1.0 : (I_SQRT_PI / Math.sqrt(a)));
				c = 0.0;
				while (z <= x)
				{
					e = e * (a / z);
					c = c + e;
					z += 1.0;
				}
				return c * y + s;
			}
		} else
		{
			return s;
		}
	}

	public static double poz(double z)
	{
		double y, x, w;
		double Z_MAX = 6.0; // Maximum meaningful z value
		if (z == 0.0)
		{
			x = 0.0;
		} else
		{
			y = 0.5 * Math.abs(z);
			if (y >= (Z_MAX * 0.5))
			{
				x = 1.0;
			} else if (y < 1.0)
			{
				w = y * y;
				x = ((((((((0.000124818987 * w - 0.001075204047) * w + 0.005198775019) * w - 0.019198292004) * w
						+ 0.059054035642) * w - 0.151968751364) * w + 0.319152932694) * w - 0.531923007300) * w
						+ 0.797884560593) * y * 2.0;
			} else
			{
				y -= 2.0;
				x = (((((((((((((-0.000045255659 * y + 0.000152529290) * y - 0.000019538132) * y - 0.000676904986) * y
						+ 0.001390604284) * y - 0.000794620820) * y - 0.002034254874) * y + 0.006549791214) * y
						- 0.010557625006) * y + 0.011630447319) * y - 0.009279453341) * y + 0.005353579108) * y
						- 0.002141268741) * y + 0.000535310849) * y + 0.999936657524;
			}
		}
		return z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5);
	}

	public static double ex(double x)
	{
		return (x < -MAX_X) ? 0.0 : Math.exp(x);
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		Random random = new Random();
		listThreemers();
		chiTable(threemerList);
		for (int j = 0; j < 10000; j++)
		{
			Arrays.fill(observed, 0); 
			for (int x = 0; x < 10000; x++)
			{

				String threemer = "";
				for (int y = 0; y < 3; y++)
				{
					float z = random.nextFloat();
					if (z <= 0.12)
					{
						threemer = threemer + "A";
					} else if (z <= 0.5)
					{
						threemer = threemer + "C";
					} else if (z <= 0.89)
					{
						threemer = threemer + "G";
					} else
					{
						threemer = threemer + "T";
					}

				}

				int count = Arrays.asList(threemerList).indexOf(threemer);
				observed[count] = observed[count] + 1;

			}

			double sum = computeChiSquared(expected, observed);

			double pValue = pochisq(sum, 63);

			pValues.add(pValue);

		}
		/*
		 * Write pValues to file to be wrangled into R.
		 */
		PrintStream o = new PrintStream(new File("Lab1pValues.txt"));
		PrintStream console = System.out;
		System.setOut(o);
		System.out.println(pValues);
		System.setOut(console);
	}

}
