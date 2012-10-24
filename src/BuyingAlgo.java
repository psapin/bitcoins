public class BuyingAlgo {
	
	//These are the weights for the averages, THEY MUST ADD TO 100!, use checkWeight to check sum
	public static int _15mWeight = 5;
	public static int _1hWeight = 10;
	public static int _4hWeight = 15;
	public static int _12hWeight = 25;
	public static int _1dWeight = 20;
	public static int _2dWeight = 15;
	public static int _7dWeight = 10;
	
	
	//If sum of weights is 100 returns true, else returns false
	public boolean checkWeight() {
		if ((_15mWeight + _1hWeight + _4hWeight + _12hWeight + _1dWeight + _2dWeight + _7dWeight) == 100) {
			return true;
		}
		else {
			//make sure to handle this shit, big money will be lost if the algo runs in this state
			return false;
		}
	}
	
	//Calculates the average to use in buying calcs based on on the range of past averages and their assigned weights
	public double getCalcAverage(double a15m, double a1h, double a4h, double a12h, double a1d, double a2d, double a7d) {
		double avg = 0;
		
		avg += a7d * (_7dWeight / 100.);
		avg += a2d * (_2dWeight / 100.);
		avg += a1d * (_1dWeight / 100.);
		avg += a12h * (_12hWeight / 100.);
		
		//This part check on whether the lower averages were null(-1) and if so assigns their weight to the higher up averages
		if(a4h < 0) {
			avg += a12h * ((_4hWeight + _1hWeight + _15mWeight) / 100.);
		}
		else {
			avg += a4h * (_4hWeight / 100.);
			if(a1h < 0) {
				avg += a4h * ((_1hWeight + _15mWeight) / 100.);
			}
			else {
				avg += a1h * (_1hWeight / 100.);
				if(a15m < 0) {
					avg += a1h * ((_15mWeight) / 100.);
				}
				else {
					avg += a15m * (_15mWeight / 100.);
				}
			}
		}
		
		return avg;
	}
	
	//This one takes the 1 day day average and 7 day average, tell you if bitcoins are in rising or dropping trend(market going down returns -1, up returns 1)
	public int checkTrend(double a1d, double a7d) {
		if (a7d > a1d) {
			return -1;
		}
		else {
			return 1;
		}
	}
	
}