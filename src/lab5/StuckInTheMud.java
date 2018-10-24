package lab5;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class StuckInTheMud extends JFrame
{
	private static final long serialVersionUID = 3794059922116115530L;

	private JTextField rollTextField = new JTextField();
	private JTextField mudTextField = new JTextField();
	private JTextField roundTextField = new JTextField();
	private JTextField roundTotalField = new JTextField();
	private JTextField bankTotalField = new JTextField();
	private int mudDice = 0;
	private int round = 1;
	private int roundTotal = 0;
	private int banked = 0;
	private List<Integer> rollList = new ArrayList<Integer>();
	private JCheckBox enableCheatItem;
	private int reset = 0;

	private JPanel getBottomPanel()
	{
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(0, 3));
		JButton rollButton = new JButton("Roll");
		rollButton.setMnemonic('R');
		rollButton.setToolTipText("Roll the dice that aren't stuck in the mud!");

		rollButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				{
					if (round < 4 && reset == 0)
					{
						rollList.clear();
						int die = 0;
						int downDice = mudDice;
						for (int i = 0; i < 6 - downDice; i++)
						{
							die = (int) (Math.random() * 6 + 1);
							rollList.add(die);
							if (die == 2 || die == 5)
							{
								mudDice++;
							}
						}
						if (mudDice == 6)
						{
							roundTotal = 0;
							round++;
							rollList.clear();
							mudDice = 0;
							reset = 0;
						} else if (!rollList.contains(5) && !rollList.contains(2))
						{
							for (int i : rollList)
							{
								roundTotal = roundTotal + i;

							}
							reset = 1;

						}

						updateTextFields();

					}
				}
			}
		});

		JButton bankButton = new JButton("Bank");
		bankButton.setMnemonic('B');
		bankButton.setToolTipText("Bank your score and start a new round!");
		bankButton.addActionListener(new ActionListener()
		{

			@SuppressWarnings("static-access")
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (roundTotal != 0)
				{
					banked = banked + roundTotal;
					roundTotal = 0;
					rollList.clear();
					mudDice = 0;
					reset = 0;
					round++;
					updateTextFields();
				}
				if (enableCheatItem.isSelected() && (e.getModifiers() & e.SHIFT_MASK) != 0)
				{
					round--;
					updateTextFields();
				}
			}
		});

		JButton pushLuckButton = new JButton("Push your luck");
		pushLuckButton.setMnemonic('P');
		pushLuckButton.setToolTipText("Keep rolling without banking!");
		pushLuckButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				rollList.clear();
				mudDice = 0;
				reset = 0;
				updateTextFields();
			}
		});

		buttonPanel.add(rollButton);
		buttonPanel.add(bankButton);
		buttonPanel.add(pushLuckButton);

		return buttonPanel;

	}

	private void updateTextFields()
	{
		rollTextField.setText("Current roll: " + rollList.toString());
		mudTextField.setText("Number of dice stuck in mud: " + mudDice);
		roundTextField.setText(gameOver());
		roundTotalField.setText("Round total: " + roundTotal);
		bankTotalField.setText("Total in bank: " + banked);

		validate();
	}
	
	private String gameOver()
	{
		String roundMessage = new String();
		if (round < 4)
		{
			roundMessage = ("Current Round: " + round + " of 3");
		}
		else roundMessage = ("Game over! Your final score is " + banked + ".");
		return roundMessage;
	}

	public StuckInTheMud()
	{
		super("Stuck in the Mud");

		getContentPane().setLayout(new GridLayout(0, 1));
		setLocationRelativeTo(null);
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(roundTextField);

		getContentPane().add(rollTextField);
		getContentPane().add(mudTextField);
		getContentPane().add(roundTotalField);
		getContentPane().add(bankTotalField);
		getContentPane().add(getBottomPanel(), BorderLayout.SOUTH);

		setJMenuBar(getMyMenuBar());
		updateTextFields();
		setVisible(true);
	}

	private void loadFromFile()
	{
		JFileChooser jfc = new JFileChooser();

		if (jfc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		if (jfc.getSelectedFile() == null)
			return;

		File chosenFile = jfc.getSelectedFile();

		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(chosenFile));
			String line = reader.readLine();

			if (line == null || reader.readLine() != null)
			{
				reader.close();
				throw new Exception("Unexpected file format");
			}

			StringTokenizer sToken = new StringTokenizer(line, ",");

			if (sToken.countTokens() != 2)
			{
				reader.close();
				throw new Exception("Something is wrong with this file.");
			}

			try
			{
				this.banked = Integer.parseInt(sToken.nextToken());
				this.round = Integer.parseInt(sToken.nextToken());
			} catch (Exception ex)
			{
				reader.close();
				throw new Exception("Unexpected file format");
			}
			reader.close();
			updateTextFields();

		} catch (Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not read file", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void saveToFile()
	{
		JFileChooser jfc = new JFileChooser();

		if (jfc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		if (jfc.getSelectedFile() == null)
			return;

		File chosenFile = jfc.getSelectedFile();

		if (jfc.getSelectedFile().exists())
		{
			String message = "File " + jfc.getSelectedFile().getName() + " exists.  Overwrite?";

			if (JOptionPane.showConfirmDialog(this, message) != JOptionPane.YES_OPTION)
				return;
		}

		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(chosenFile));
			writer.write(this.banked + "," + this.round);
			writer.flush();
			writer.close();
		} catch (Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Could not write file", JOptionPane.ERROR_MESSAGE);
		}
	}

	private JMenuBar getMyMenuBar()
	{
		JMenuBar jmenuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		jmenuBar.add(fileMenu);

		JMenu subMenu = new JMenu("Instructions");
		fileMenu.add(subMenu);
		subMenu.setMnemonic('I');
		JMenuItem gameInstructions = new JMenuItem("Game");
		gameInstructions.setMnemonic('G');
		subMenu.add(gameInstructions);
		gameInstructions.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFrame frame = new JFrame("Stuck in the mud instructions.");
				JOptionPane.showMessageDialog(frame, "<html><body><p style='width: 200px;'>"
						+ "Stuck in the Mud is a push-your-luck game played with 6 dice over 3 rounds. "
						+ "If you roll any 2's or 5's, your roll will be unscored, and those dice will be removed "
						+ "from play. Continue rolling until either all your dice are stuck in the mud, or a roll "
						+ "contains no 2's and 5's. The remaining dice are summed, and added to your round total. "
						+ "You may now choose to end the round and bank your points, or push your luck and reroll all "
						+ "6 dice to continue the round. But beware: if at any point all your dice are stuck in the mud, "
						+ "you will score no points for the round.</p></body></html>", "Game Instructions:", JOptionPane.PLAIN_MESSAGE);
			}
		});
		JMenuItem cheatInstructions = new JMenuItem("Cheat");
		subMenu.add(cheatInstructions);
		cheatInstructions.setMnemonic('H');
		cheatInstructions.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFrame frame = new JFrame("Stuck in the mud instructions.");
				JOptionPane.showMessageDialog(frame,
						"<html><body><p style='width: 200px;'>"
								+ "With cheat enabled, if you hold down the shift button "
								+ "while banking, your round number will not be increased.</p></body></html>");
			}
		});

		JMenuItem saveInstructions = new JMenuItem("Save/Load");
		subMenu.add(saveInstructions);
		saveInstructions.setMnemonic('L');
		saveInstructions.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFrame frame = new JFrame("Stuck in the mud instructions.");
				JOptionPane.showMessageDialog(frame, "<html><body><p style='width: 200px;'>"
						+ "You may save your round number and banked points at any time using the \"Save\" button. "
						+ "You may load a saved game using the \"Load\" button.");
			}
		});

		JMenuItem saveItem = new JMenuItem("Save");
		fileMenu.add(saveItem);
		saveItem.setMnemonic('S');

		saveItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				saveToFile();
			}
		});

		JMenuItem openItem = new JMenuItem("Load");
		openItem.setMnemonic('L');
		fileMenu.add(openItem);

		openItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				loadFromFile();
			}
		});

		JMenu cheatMenu = new JMenu("Cheat");
		cheatMenu.setMnemonic('C');
		jmenuBar.add(cheatMenu);

		this.enableCheatItem = new JCheckBox("Enable Cheat");
		enableCheatItem.setMnemonic('E');
		cheatMenu.add(enableCheatItem);
		return jmenuBar;
	}

	public static void main(String[] args)
	{
		new StuckInTheMud();
	}

}