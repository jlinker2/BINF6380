package lab7;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
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
	private final int NUMBER_OF_PROCESSORS = 4;
	private volatile AtomicInteger count = new AtomicInteger(0);
	
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
		setSize(400, 200);
		setLocationRelativeTo(null);
		//kill java VM on exit
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(primeButton,  BorderLayout.SOUTH);
		getContentPane().add(cancelButton,  BorderLayout.EAST);
		getContentPane().add( new JScrollPane(aTextField),  BorderLayout.CENTER);
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
	
		primeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					
					String num = JOptionPane.showInputDialog("Enter a large integer");
					Integer max =null;
					
					try
					{
						max = Integer.parseInt(num);
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(
								thisFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
					
					if( max != null)
					{
						aTextField.setText("");
						primeButton.setEnabled(false);
						cancelButton.setEnabled(true);
						cancel = false;
						Semaphore s = new Semaphore(NUMBER_OF_PROCESSORS);
						for (int x = 0; x < NUMBER_OF_PROCESSORS; x++)
						{
							s.tryAcquire();
							new Thread(new UserInput(max, s)).start();
//							System.out.println("thread " + x + " " + (System.currentTimeMillis() - startTime) / 1000f);
						}

					}
				}});
		}
	
	private boolean isPrime( int i)
	{
		for( int x=2; x < i -1; x++)
			if( i % x == 0  )
				return false;
		
		return true;
	}
	
	private class UserInput implements Runnable
	{
		private final int max;
		private final long startTime;
		private final Semaphore sem;
		
		private UserInput(int num, Semaphore sem)
		{
			this.max = num;
			this.startTime = System.currentTimeMillis();
			this.sem = sem;
		}
		
		public void run()
		{
			long lastUpdate = System.currentTimeMillis();
			int threadID = count.getAndIncrement();
			
			for (int i = threadID; i < max && ! cancel; i = i + NUMBER_OF_PROCESSORS) 
			{
				if( isPrime(i))
				{
					list.add(i);
						
					if( System.currentTimeMillis() - lastUpdate > 500)
					{
						float time = (System.currentTimeMillis() -startTime )/1000f;
						final String outString= "Found " + list.size() + " in " + i + " of " + max + " " 
									+ time + " seconds ";
						
						SwingUtilities.invokeLater( new Runnable()
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
			}
			
			final StringBuffer buff = new StringBuffer();
			
			for( Integer i2 : list)
				buff.append(i2 + "\n");
			
			if( cancel)
				buff.append("cancelled\n");
			
			float time = (System.currentTimeMillis() - startTime )/1000f;
			buff.append("Time = " + time + " seconds " );
			
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					
					cancel = false;
					primeButton.setEnabled(true);
					cancelButton.setEnabled(false);
					aTextField.setText( (cancel ? "cancelled " : "") +  buff.toString());
					
				}
			});
			sem.release();
			
			
			
		}// end run
		
	}  // end UserInput
}