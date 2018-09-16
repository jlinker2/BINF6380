package Lab2;

import java.util.Random;
import java.util.Arrays;

// Due 9/17/18
public class Lab2Quiz
{
	private static String[] SHORT_NAMES =
		{ "A","R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F",
				"P", "S", "T", "W", "Y", "V" };
	private static String[] FULL_NAMES =
		{"alanine","arginine", "asparagine", "aspartic acid", "cysteine",
		"glutamine", "glutamic acid", "glycine" ,"histidine","isoleucine",
		"leucine", "lysine", "methionine", "phenylalanine", "proline",
		"serine","threonine","tryptophan", "tyrosine", "valine"};
	private static int score = 0;
		
	public static String chooseNextFullName()
	{
		Random random = new Random();
		int x = random.nextInt(20);
		return FULL_NAMES[x];
	}
	
	public static void checkAnswer(String prompt, String answer)
	{
		if(Arrays.asList(SHORT_NAMES).indexOf(answer)==(Arrays.asList(FULL_NAMES).indexOf(prompt)))
		{
			score++;
			System.out.println("Correct! " + answer + " is the abbreviation of " + prompt + ".");
		}
		else
		{
			System.out.println("Wrong! The correct abbreviation for " + prompt + " is " 
					+ SHORT_NAMES[Arrays.asList(FULL_NAMES).indexOf(prompt)] + ".");
		}
	}
	
	public static void main(String[] args)
	{
		long start = System.currentTimeMillis();
		long end = (start+30000);
		while (System.currentTimeMillis()<end)
		{
			String prompt = chooseNextFullName();
			System.out.println(prompt);
			String answer = System.console().readLine().toUpperCase();
			if (answer.length()==0)
			{
				System.out.println("You must enter an answer!");
			}
			else if (answer.equals("QUIT")){
				System.out.println("Goodbye!");
				break;
			}
			else 
			{
			String answerCharacter = "" + answer.charAt(0);
			checkAnswer(prompt,answerCharacter);
			System.out.println("Your current score is: "+ score + ".");
			long elapse = (System.currentTimeMillis() - start)/1000; 
			System.out.println("Time: " + elapse + " out of 30 seconds.");
			}
		}
		
	}

}
