
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class URLConnectionReader {
	
	public double _lastVirtex;
	public double _highVirtex;
	public double _lowVirtex;
	public double _buyingVirtex;
	public double _sellingVirtex;

	public double _lastGox;
	public double _highGox;
	public double _lowGox;
	public double _buyingGox;
	public double _sellingGox;

	public double _avgGox15m;
	public double _avgGox1h;
	public double _avgGox4h;
	public double _avgGox12h;
	public double _avgGox1d;
	public double _avgGox2d;
	public double _avgGox7d;

	public double _avgVirtex15m;
	public double _avgVirtex1h;
	public double _avgVirtex4h;
	public double _avgVirtex12h;
	public double _avgVirtex1d;
	public double _avgVirtex2d;
	public double _avgVirtex7d;

	public double _1USD;
	public double _1CAD;
    
    
    public void getGoxData() throws Exception {
    	String tick = "";
    	
    	try
    	{
    		URL u = new URL("https://www.mtgox.com/code/data/ticker.php");
    		HttpsURLConnection http = (HttpsURLConnection)u.openConnection();
    		BufferedReader in = new BufferedReader(
                new InputStreamReader(
                http.getInputStream()));
    		String inputLine;

    		while ((inputLine = in.readLine()) != null) 
    			tick = tick.concat(inputLine).concat("\n");
    		in.close();
    	}
    	catch(MalformedURLException e)
    	{
    		System.out.println("Invalid URL");
    	}
    	catch(IOException e)
    	{
    		System.out.println("Error connecting: " + e.getMessage() );
    	}
    	//System.out.println(tick);
    	
    	String highGox = tick.substring((tick.indexOf("high") + 6), (tick.indexOf("low") - 2));
    	String lowGox = tick.substring((tick.indexOf("low") + 5), (tick.indexOf("avg") - 2));
    	//String avgGox = tick.substring((tick.indexOf("avg") + 5), (tick.indexOf("vol") - 2));
    	String lastGox = tick.substring((tick.indexOf("last") + 6), (tick.indexOf("buy") - 2));
    	String buyingGox = tick.substring((tick.indexOf("buy") + 5), (tick.indexOf("sell") - 2));
    	String sellingGox = tick.substring((tick.indexOf("sell") + 6), (tick.indexOf("}}")));
    	
    	_highGox = Double.valueOf(highGox);
    	_lowGox = Double.valueOf(lowGox);
    	_lastGox = Double.valueOf(lastGox);
    	_buyingGox = Double.valueOf(buyingGox);
    	_sellingGox = Double.valueOf(sellingGox);
    	/*
    	System.out.println("lastGox: " + lastGox);
    	System.out.println("highGox: " + highGox);
    	System.out.println("lowGox: " + lowGox);
    	//System.out.println("avgGox: " + avgGox);
    	System.out.println("buyingGox: " + buyingGox);
    	System.out.println("sellingGox: " + sellingGox);
    	System.out.println("\n");
    	*/
    }
    
    public void getVirtexData() throws Exception {
    	String html = "";
    	
    	try
    	{
    		URL u = new URL("https://www.cavirtex.com/orderbook");
    		HttpsURLConnection http = (HttpsURLConnection)u.openConnection();
    		BufferedReader in = new BufferedReader(
                new InputStreamReader(
                http.getInputStream()));
    		String inputLine;

    		while ((inputLine = in.readLine()) != null) 
    			html = html.concat(inputLine).concat("\n");
    		//System.out.println(html);
    		in.close();
    	}
    	catch(MalformedURLException e)
    	{
    		System.out.println("Invalid URL");
    	}
    	catch(IOException e)
    	{
    		System.out.println("Error connecting: " + e.getMessage() );
    	}
    	
    	int index1 = 0;
    	
    	String lastVirtex = html.substring((html.indexOf("Last: ") + 6), (html.indexOf("Last: ") + 14));
    	String highVirtex = html.substring((html.indexOf("High: ") + 6), (html.indexOf("High: ") + 14));
    	String lowVirtex = html.substring((html.indexOf("Low: ") + 5), (html.indexOf("Low: ") + 13));
    	
    	index1 = html.indexOf("orderbook_buy");
    	index1 = html.indexOf("</b></td>", index1);
    	index1 = html.indexOf("</b></td>", index1 + 1);
    	index1 = html.indexOf("<td><b>", index1);
    	String buyingVirtex = html.substring((index1+7), (index1+15));
    	
    	index1 = html.indexOf("orderbook_sell");
    	index1 = html.indexOf("</b></td>", index1);
    	index1 = html.indexOf("</b></td>", index1 + 1);
    	index1 = html.indexOf("<td><b>", index1);
    	String sellingVirtex = html.substring((index1+7), (index1+15));
    	
    	_highVirtex = Double.valueOf(highVirtex);
    	_lowVirtex = Double.valueOf(lowVirtex);
    	_lastVirtex = Double.valueOf(lastVirtex);
    	_buyingVirtex = Double.valueOf(buyingVirtex);
    	_sellingVirtex = Double.valueOf(sellingVirtex);
    	/*
    	System.out.println("lastVirtex: " + lastVirtex);
    	System.out.println("highVirtex: " + highVirtex);
    	System.out.println("lowVirtex: " + lowVirtex);
    	System.out.println("buyingVirtex: " + buyingVirtex);
    	System.out.println("sellingVirtex: " + sellingVirtex);
    	System.out.println("\n");
    	*/
    }
    
    public void getGoxAverages() throws Exception {
    	String html = "";
    	
    	try
    	{
    		URL u = new URL("http://bitcoincharts.com/markets/mtgoxUSD_trades.html");
    		HttpURLConnection http = (HttpURLConnection)u.openConnection();
    		BufferedReader in = new BufferedReader(
                new InputStreamReader(
                http.getInputStream()));
    		String inputLine;

    		while ((inputLine = in.readLine()) != null) 
    			html = html.concat(inputLine).concat("\n");
    		//System.out.println(html);
    		in.close();
    	}
    	catch(MalformedURLException e)
    	{
    		System.out.println("Invalid URL");
    	}
    	catch(IOException e)
    	{
    		System.out.println("Error connecting: " + e.getMessage() );
    	}
    	
    	int index1 = 0;
    	
    	index1 = html.indexOf(">15min</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgGox15m = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	if ( avgGox15m.equals("—")) {
    		avgGox15m = null;
    		_avgGox15m = -1;
    	}
    	else {
    		_avgGox15m = Double.valueOf(avgGox15m);
    	}
    	
    	index1 = html.indexOf("1h</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgGox1h = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	if ( avgGox1h.equals("—")) {
    		avgGox1h = null;
    		_avgGox1h = -1;
    	}
    	else {
    		_avgGox1h = Double.valueOf(avgGox1h);
    	}
    	
    	index1 = html.indexOf("4h</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgGox4h = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	if ( avgGox4h.equals("—")) {
    		avgGox4h = null;
    		_avgGox4h = -1;
    	}
    	else {
    		_avgGox4h = Double.valueOf(avgGox4h);
    	}
    	
    	index1 = html.indexOf("12h</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgGox12h = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	
    	index1 = html.indexOf("1d</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgGox1d = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	
    	index1 = html.indexOf("2d</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgGox2d = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	
    	index1 = html.indexOf("7d</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgGox7d = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	
    	_avgGox12h = Double.valueOf(avgGox12h);
    	_avgGox1d = Double.valueOf(avgGox1d);
    	_avgGox2d = Double.valueOf(avgGox2d);
    	_avgGox7d = Double.valueOf(avgGox7d);
    	
    	/*System.out.println("avgGox15m: " + avgGox15m);
    	System.out.println("avgGox1h: " + avgGox1h);
    	System.out.println("avgGox4h: " + avgGox4h);
    	System.out.println("avgGox12h: " + avgGox12h);
    	System.out.println("avgGox1d: " + avgGox1d);
    	System.out.println("avgGox2d: " + avgGox2d);
    	System.out.println("avgGox7d: " + avgGox7d);
    	System.out.println("\n");*/

    }
    
    public void getVirtexAverages() throws Exception {
    	String html = "";
    	
    	try
    	{
    		URL u = new URL("http://bitcoincharts.com/markets/virtexCAD_trades.html");
    		HttpURLConnection http = (HttpURLConnection)u.openConnection();
    		BufferedReader in = new BufferedReader(
                new InputStreamReader(
                http.getInputStream()));
    		String inputLine;

    		while ((inputLine = in.readLine()) != null) 
    			html = html.concat(inputLine).concat("\n");
    		//System.out.println(html);
    		in.close();
    	}
    	catch(MalformedURLException e)
    	{
    		System.out.println("Invalid URL");
    	}
    	catch(IOException e)
    	{
    		System.out.println("Error connecting: " + e.getMessage() );
    	}
    	
    	int index1 = 0;
    	
    	index1 = html.indexOf(">15min</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgVirtex15m = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	if ( avgVirtex15m.equals("—")) {
    		avgVirtex15m = null;
    		_avgVirtex15m = -1;
    	}
    	else {
    		_avgVirtex15m = Double.valueOf(avgVirtex15m);
    	}
    	
    	index1 = html.indexOf("1h</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgVirtex1h = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	if ( avgVirtex1h.equals("—")) {
    		avgVirtex1h = null;
    		_avgVirtex1h = -1;
    	}
    	else {
    		_avgVirtex1h = Double.valueOf(avgVirtex1h);
    	}
    	
    	index1 = html.indexOf("4h</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgVirtex4h = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	if ( avgVirtex4h.equals("—")) {
    		avgVirtex4h = null;
    		_avgVirtex4h = -1;
    	}
    	else {
    		_avgVirtex4h = Double.valueOf(avgVirtex4h);
    	}
    	
    	index1 = html.indexOf("12h</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgVirtex12h = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	
    	index1 = html.indexOf("1d</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgVirtex1d = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	
    	index1 = html.indexOf("2d</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgVirtex2d = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	
    	index1 = html.indexOf("7d</td>");
    	index1 = html.indexOf("</td>", index1 + 8);
    	index1 = html.indexOf("</td>", index1 + 1);
    	String avgVirtex7d = html.substring((html.indexOf("right", index1) + 7), html.indexOf("</td>", index1 + 1));
    	
    	_avgVirtex12h = Double.valueOf(avgVirtex12h);
    	_avgVirtex1d = Double.valueOf(avgVirtex1d);
    	_avgVirtex2d = Double.valueOf(avgVirtex2d);
    	_avgVirtex7d = Double.valueOf(avgVirtex7d);
    	
    	/*System.out.println("avgVirtex15m: " + avgVirtex15m);
    	System.out.println("avgVirtex1h: " + avgVirtex1h);
    	System.out.println("avgVirtex4h: " + avgVirtex4h);
    	System.out.println("avgVirtex12h: " + avgVirtex12h);
    	System.out.println("avgVirtex1d: " + avgVirtex1d);
    	System.out.println("avgVirtex2d: " + avgVirtex2d);
    	System.out.println("avgVirtex7d: " + avgVirtex7d);
    	System.out.println("\n");*/
    }
    
    public void getUSDtoCAD() throws Exception {
    	String html = "";
    	
    	try
    	{
    		URL u = new URL("http://www.google.com/finance/converter?a=1&from=USD&to=CAD");
    		HttpURLConnection http = (HttpURLConnection)u.openConnection();
    		BufferedReader in = new BufferedReader(
                new InputStreamReader(
                http.getInputStream()));
    		String inputLine;

    		while ((inputLine = in.readLine()) != null) 
    			html = html.concat(inputLine).concat("\n");
    		//System.out.println(html);
    		in.close();
    	}
    	catch(MalformedURLException e)
    	{
    		System.out.println("Invalid URL");
    	}
    	catch(IOException e)
    	{
    		System.out.println("Error connecting: " + e.getMessage() );
    	}
    	
    	String cad = html.substring((html.indexOf("class=bld>") + 10), (html.indexOf("</span>") - 4));
    	
    	_1USD = Double.valueOf(cad);
    	
    	//System.out.println("1 USD = " + cad + " CAD");
    	//System.out.println("\n");
    }
    
    public void getCADtoUSD() throws Exception {
    	String html = "";
    	
    	try
    	{
    		URL u = new URL("http://www.google.com/finance/converter?a=1&from=CAD&to=USD");
    		HttpURLConnection http = (HttpURLConnection)u.openConnection();
    		BufferedReader in = new BufferedReader(
                new InputStreamReader(
                http.getInputStream()));
    		String inputLine;

    		while ((inputLine = in.readLine()) != null) 
    			html = html.concat(inputLine).concat("\n");
    		//System.out.println(html);
    		in.close();
    	}
    	catch(MalformedURLException e)
    	{
    		System.out.println("Invalid URL");
    	}
    	catch(IOException e)
    	{
    		System.out.println("Error connecting: " + e.getMessage() );
    	}
    	
    	String usd = html.substring((html.indexOf("class=bld>") + 10), (html.indexOf("</span>") - 4));
    	
    	_1CAD = Double.valueOf(usd);
    	
    	//System.out.println("1 CAD = " + usd + " USD");
    	//System.out.println("\n");
    }

}
