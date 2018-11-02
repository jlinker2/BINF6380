package lab6;

import java.util.Arrays;
import java.util.Random;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class AminoQuiz extends JFrame
{
	private static final String[] SHORT_NAMES =
	{ "A", "R", "N", "D", "C", "Q", "E", "G", "H", "I", "L", "K", "M", "F", "P", "S", "T", "W", "Y", "V" };
	private static final String[] FULL_NAMES =
	{ "alanine", "arginine", "asparagine", "aspartic acid", "cysteine", "glutamine", "glutamic acid", "glycine",
			"histidine", "isoleucine", "leucine", "lysine", "methionine", "phenylalanine", "proline", "serine",
			"threonine", "tryptophan", "tyrosine", "valine" };
	private static int score = 0;
	private static final long serialVersionUID = 3794059922116115530L;

	private JTextField promptText = new JTextField();
	private JTextField answerText = new JTextField();
	private JTextField evaluationText = new JTextField();
	private JTextField scoreText = new JTextField();
	private JTextField timerText = new JTextField();

	private static int[] correct = new int[20];
	private static int[] incorrect = new int[20];
	private static String prompt;
	private static String evaluation;
	private static final int DEFAULT_TIMER = 10;
	private volatile static int timerSet = DEFAULT_TIMER;
	private volatile static int timeLeft;
	private volatile static boolean gameOver = false;
	private volatile static boolean stop = false;

	private JButton startButton = new JButton("Start!");
	private JButton timerButton = new JButton("Add 10 seconds");

	private JPanel getBottomPanel()
	{
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0, 3));
		// set up the START button
		startButton.setMnemonic('S');
		startButton.setToolTipText("Take the quiz!");
		startButton.addActionListener(new StartActionListener());
		// set up the TIMER button and add action listener
		timerButton.setMnemonic('A');
		timerButton.setToolTipText("Add 10 seconds to the timer.");
		timerButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				{
					timerSet = timerSet + 10;
					updateTextFields();
				}
			}
		});
		// set up the RESET button and add action listener
		JButton resetButton = new JButton("Reset");
		resetButton.setMnemonic('R');
		resetButton.setToolTipText("Reset the quiz.");
		resetButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				{
					startButton.setEnabled(true);
					timerButton.setEnabled(true);
					timerSet = DEFAULT_TIMER;
					score = 0;
					gameOver = false;
					stop = true;
					promptText.setText("");
					answerText.setText("");
					answerText.setFocusable(true);
					evaluation = "";
					evaluationText.setText("");
					scoreText.setText("Click start to play!");
					timerText.setText("Time: " + timeLeft + " of " + timerSet + " seconds");
				}
			}
		});

		buttonPanel.add(startButton);
		buttonPanel.add(timerButton);
		buttonPanel.add(resetButton);

		return buttonPanel;
	}

	// Action listener for the start button
	private class StartActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			startButton.setEnabled(false);
			timerButton.setEnabled(false);

			// start timer
			Thread worker = new Thread(new timerActionRunnable());
			worker.start();
			// deliver first prompt
			Random random = new Random();
			int x = random.nextInt(20);
			prompt = FULL_NAMES[x];
			// put the carat in the answer field
			answerText.requestFocus();
			updateTextFields();

		}
	}

	// Action listener for the answer text field
	private class AnswerActionListener implements ActionListener

	{
		public void actionPerformed(ActionEvent e)
		{
			{

				if (!gameOver)
				{
					// grab answer and make it uppercase
					String text = answerText.getText();
					text = text.toUpperCase();
					// check answer against prompt; set evaluation text
					checkAnswer(prompt, text);
					// new prompt
					Random random = new Random();
					int x = random.nextInt(20);
					prompt = FULL_NAMES[x];
					answerText.setText("");
					updateTextFields();
				}
			}
		}
	}

	// pop up results pane when game is over
	private void resultsPane()
	{
		StringBuffer results = new StringBuffer(
				"You finished the quiz with " + score + " correct answers in " + timerSet + " seconds. \n\n");
		for (int x = 0; x < FULL_NAMES.length; x++)
		{
			results.append(FULL_NAMES[x] + " (" + SHORT_NAMES[x] + "): " + correct[x] + " correct, " + incorrect[x]
					+ " incorrect. \n");
		}
		JFrame frame = new JFrame("");
		JOptionPane.showMessageDialog(frame, "<html><body><p style='width: 200px;'>" + results, "Results",
				JOptionPane.PLAIN_MESSAGE);
	}

	// timer to tick down seconds and report to timer text field
	private class timerActionRunnable implements Runnable
	{
		public void run()
		{

			try
			{
				stop = false;
				long now = System.currentTimeMillis();
				long end = now + timerSet * 1000;
				int holder = (int) ((end - System.currentTimeMillis()) / 1000);
				while (System.currentTimeMillis() <= end && !stop)
				{
					if ((int) ((end - System.currentTimeMillis()) / 1000) != holder)
					{
						holder = (int) ((end - System.currentTimeMillis()) / 1000);
						timeLeft = (int) ((end - System.currentTimeMillis()) / 1000);
						timerText.setText("Time: " + timeLeft + " of " + timerSet + " seconds");
						Thread.sleep(100);
					}
				}
				if (!stop)
				{
					SwingUtilities.invokeAndWait(new EndGameActionRunnable());
				}

			} catch (

			Exception ex)
			{
				ex.printStackTrace();
			}

		}

	}

	private class EndGameActionRunnable implements Runnable
	{
		public void run()
		{

			try
			{
				gameOver = true;
				answerText.setText("Game Over!");
				answerText.setFocusable(false);
				promptText.setText("");
				evaluationText.setText("");
				scoreText.setText("");
				resultsPane();

			} catch (

			Exception ex)
			{
				ex.printStackTrace();
			}

		}

	}

	// update all the text fields except the answer field, which does its own thing
	private void updateTextFields()
	{
		promptText.setText(prompt);
		evaluationText.setText(evaluation);
		scoreText.setText("Current score: " + score);
		timerText.setText("Time: " + timeLeft + " of " + timerSet + " seconds");

		validate();
	}

	public static void checkAnswer(String prompt, String answer)
	{
		if (Arrays.asList(SHORT_NAMES).indexOf(answer) == (Arrays.asList(FULL_NAMES).indexOf(prompt)))
		{
			score++;
			correct[Arrays.asList(FULL_NAMES).indexOf(prompt)] = correct[Arrays.asList(FULL_NAMES).indexOf(prompt)] + 1;
			evaluation = ("Correct!");
		} else
		{
			incorrect[Arrays.asList(FULL_NAMES).indexOf(prompt)] = incorrect[Arrays.asList(FULL_NAMES).indexOf(prompt)]
					+ 1;
			evaluation = ("Wrong! The correct abbreviation for " + prompt + " is "
					+ SHORT_NAMES[Arrays.asList(FULL_NAMES).indexOf(prompt)] + ".");
		}
	}

	public AminoQuiz()
	{
		super("Amino Acid Quiz");

		getContentPane().setLayout(new GridLayout(0, 1));
		setLocationRelativeTo(null);
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(promptText);
		promptText.setFocusable(false);
		getContentPane().add(answerText);
		answerText.addActionListener(new AnswerActionListener());
		getContentPane().add(evaluationText);
		evaluationText.setFocusable(false);
		getContentPane().add(scoreText);
		scoreText.setText("Click start to play!");
		scoreText.setFocusable(false);
		getContentPane().add(timerText);
		timerText.setFocusable(false);
		getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);

		promptText.setText(prompt);
		evaluationText.setText(evaluation);
		timerText.setText("Time: " + timeLeft + " of " + timerSet + " seconds");
		// not using updateTextFields because I want to leave "Click start to play!" in
		// the scoreText
		setVisible(true);
	}

	public static void main(String[] args)
	{
		new AminoQuiz();
	}

}
