import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class BitcoinTrader {
	
	private static URLConnectionReader ucr;
	private static BTgui frame;
	
	public static void main(String[] args) {
		ucr = new URLConnectionReader();
		updateGeneralData();
		updateAvgEx();
		frame = new BTgui(ucr);
		initTimers();
	}
	
	static void updateGeneralData() {
		  Runnable r=new Runnable()
		  {
		  public void run()
		  {
			  try {
				ucr.getVirtexData();
				ucr.getGoxData();
				frame.updateBasic();
			  } catch (Exception e) {
				// TODO Auto-generated catch block
				  e.printStackTrace();
			  }
		  }
		  };
		  Thread t = new Thread(r);
		  t.start();
	}
	
	static void updateAvgEx() {
		  Runnable r=new Runnable()
		  {
		  public void run()
		  {
			  try {
				ucr.getGoxAverages();
				ucr.getVirtexAverages();
				ucr.getCADtoUSD();
				ucr.getUSDtoCAD();
				frame.updateRest();
			  } catch (Exception e) {
				// TODO Auto-generated catch block
				  e.printStackTrace();
			  }
		  }
		  };
		  Thread t = new Thread(r);
		  t.start();
	}
	
	private static void initTimers() {
		//5 second Timer
		int delay = 5000; //milliseconds
    	ActionListener taskPerformer = new ActionListener() {
    	      public void actionPerformed(ActionEvent evt) {
					try {
						//Update data
						//System.out.println("Timer1 called");
						updateGeneralData();
					} catch (Exception e) {
						e.printStackTrace();
					}
    	      }
    	};
    	new Timer(delay, taskPerformer).start();
    	
    	//2 minute Timer
    	delay = 120000; //milliseconds
    	taskPerformer = new ActionListener() {
    	      public void actionPerformed(ActionEvent evt) {
					try {
						//Update Averages
						//System.out.println("Timer2 called");
						updateAvgEx();
					} catch (Exception e) {
						e.printStackTrace();
					}
    	      }
    	};
    	new Timer(delay, taskPerformer).start();
	}
	
	public static long getNumCombinations( int summands, int sum )
	{
		if ( summands <= 1 )
			return 1;
		long combos = 0;
		for ( int a = 0 ; a <= sum ; a++ )
			combos += getNumCombinations( summands-1, sum-a );
		return combos;
	}
}