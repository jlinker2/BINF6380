package lab7;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class PrimeNumGen2 extends JFrame
{

	private static final long serialVersionUID = 1L;
	private final JTextArea aTextField = new JTextArea();
	private final JButton primeButton = new JButton("Start");
	private final JButton cancelButton = new JButton("Cancel");
	private volatile boolean cancel = false;
	private final PrimeNumGen2 thisFrame;
	private List<Integer> list = Collections.synchronizedList(new ArrayList<Integer>());
	private final int NUM_WORKER_THREADS = 47;
	private volatile AtomicInteger count = new AtomicInteger(0);
	private volatile AtomicBoolean done = new AtomicBoolean(false);

	public static void main(String[] args)

	{
		PrimeNumGen2 png = new PrimeNumGen2("Primer Number Generator");

		// don't add the action listener from the constructor
		png.addActionListeners();
		png.setVisible(true);

	}

	private PrimeNumGen2(String title)
	{
		super(title);
		this.thisFrame = this;
		cancelButton.setEnabled(false);
		aTextField.setEditable(false);
		setSize(500, 200);
		setLocationRelativeTo(null);
		// kill java VM on exit
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(primeButton, BorderLayout.SOUTH);
		getContentPane().add(cancelButton, BorderLayout.EAST);
		getContentPane().add(new JScrollPane(aTextField), BorderLayout.CENTER);
	}

	private class CancelOption implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			cancel = true;

		}
	}

	private void addActionListeners()
	{
		cancelButton.addActionListener(new CancelOption());

		primeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				String num = JOptionPane.showInputDialog("Enter a large integer");
				Integer max = null;
				final long startTime;
				list.clear();
				cancel = false;
				done = new AtomicBoolean(false);
				primeButton.setEnabled(false);
				cancelButton.setEnabled(true);
				startTime = System.currentTimeMillis();

				try
				{
					max = Integer.parseInt(num);
				} catch (Exception ex)
				{
					JOptionPane.showMessageDialog(thisFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}

				if (max != null)
				{
					aTextField.setText("");
					count = new AtomicInteger(0);

					// start update thread here
					new Thread(new UpdateText(startTime, max)).start();

					// start prime threads
					new Thread(new LaunchWorkers(max)).start();

				}
			}
		});
	}

	private boolean isPrime(int i)
	{
		for (int x = 2; x < i - 1; x++)
			if (i % x == 0)
				return false;

		return true;
	}

	private class UpdateText implements Runnable
	{

		private final long startTime;
		long lastUpdate = System.currentTimeMillis();
		private final int max;

		private UpdateText(long startTime, int max)
		{
			this.startTime = startTime;
			this.max = max;
		}

		public void run()
		{

			while (!done.get())
			{
				if (System.currentTimeMillis() - lastUpdate > 500)
				{
					float time = (System.currentTimeMillis() - startTime) / 1000f;
					final String outString = "Found " + list.size() + " primes less than " + max + " in " + time
							+ " seconds ";
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							aTextField.setText(outString);
						}
					});

					lastUpdate = System.currentTimeMillis();
				}
			}

			final StringBuffer buff = new StringBuffer();
			Collections.sort(list);
			for (Integer i2 : list)
				buff.append(i2 + "\n");

			if (cancel)
				buff.append("cancelled\n");
			buff.append("Number of primes less than " + max + ": " + list.size() + "\n");
			float time = (System.currentTimeMillis() - startTime) / 1000f;
			buff.append("Time = " + time + " seconds ");

			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{

					cancel = false;
					primeButton.setEnabled(true);
					cancelButton.setEnabled(false);
					aTextField.setText((cancel ? "cancelled " : "") + buff.toString());

				}
			});
		}
	}// end UpdateText

	private class UserInput implements Runnable
	{
		private final int max;
		private final Semaphore sem;

		private UserInput(int num, Semaphore sem)
		{
			this.max = num;
			this.sem = sem;
		}

		public void run()
		{

			int threadID = count.getAndIncrement();

			for (int i = threadID; i < max && !cancel; i = i + NUM_WORKER_THREADS)
			{

				if (isPrime(i))
				{
					list.add(i);

				}
			}

			sem.release();

		}// end run

	} // end UserInput

	private class LaunchWorkers implements Runnable
	{
		final int max;

		private LaunchWorkers(int max)
		{
			this.max = max;
		}

		public void run()
		{

			Semaphore s = new Semaphore(NUM_WORKER_THREADS);

			// launch threads
			for (int x = 0; x < NUM_WORKER_THREADS; x++)
			{
				s.tryAcquire();
				new Thread(new UserInput(max, s)).start();
			}

			// gather up licenses - this part takes a while
			for (int i = 0; i < NUM_WORKER_THREADS; i++)
			{
				try
				{
					s.acquire();

				} catch (InterruptedException e1)
				{

					e1.printStackTrace();
					System.out.println("I tried to aquire and failed.");
				}

			}

			// tell the updater that we're done
			done = new AtomicBoolean(true);

		}// end run

	} // end LaunchWorkers
}