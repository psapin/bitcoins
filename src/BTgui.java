import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/******************************************************
 *  TODO [list is in order of my thoughts, not priority or relevance]
 *  make gui refresh with f5 memonic keypress or whatever
 *  the part that shows mt gox and virtex account data and funds, and a way to edit them
 *  One-click trading through apis
 *  make sure (quad check) calcs are correct.
 *  make averages update currency when currency is swapped
 *  clean up code, at 600 lines atm
 * ****************************************************/
 
public class BTgui extends javax.swing.JFrame implements ActionListener {

	private boolean mtgoxUSD;
    private boolean virtexCAD;
    private NumberFormat nf;
    private final double MTGOXFEE = .0065;
    private final double VIRTEXFEE = .0059;
    private URLConnectionReader ucr;
    private BuyingAlgo ba;
	
	/** Creates new form BTgui */
    public BTgui(URLConnectionReader reader) {
    	ucr = reader;
        initComponents();
        virtexCAD = true;
        mtgoxUSD = true;
        nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(6);
        setVisible(true);
        ba = new BuyingAlgo();
    }
    
    public void updateAll(){
    	updateBasic();
    	updateRest();
    }
    
    public void updateBasic(){
    	updateGox();
    	updateVirtex();
    	updateGoxToVirtexProfitPercent();
    	updateVirtexToGoxProfitPercent();
    }
    
    public void updateRest(){
    	updateConversion();
    	updateAverageGox12h();
    	updateAverageVirtex12h();
    	calcAverages();
    }
    
    public double goxToVirtexProfit(double buy, double sell, double cad, double amount){
    	return (sell * ( (amount * (1 - MTGOXFEE)) / (1 + VIRTEXFEE) ) * cad) - (amount * buy);
    }
    public double virtexToGoxProfit(double buy, double sell, double cad, double amount){
    	return ((( amount / (1 + VIRTEXFEE)) * sell) * (1 - MTGOXFEE)) - (buy * amount * cad);
    }
    public void updateGoxToVirtexProfitPercent()
    {
    	double profit = goxToVirtexProfit(ucr._sellingGox, ucr._buyingVirtex, ucr._1CAD, 1.0);
    	double percent = profit/(ucr._sellingGox*1.0) * 100;
    	if(percent > 0){
    		if(percent < 2)
    			mtgoxToVirtexProfitLabel.setForeground(Color.YELLOW);
    		else
    			mtgoxToVirtexProfitLabel.setForeground(Color.GREEN);
    	}	
    	else if(percent == -100){
    		mtgoxToVirtexProfitLabel.setText("loading 5s lol");return;}
    	else
    		mtgoxToVirtexProfitLabel.setForeground(Color.RED);
    	mtgoxToVirtexProfitLabel.setText(nf.format(percent)+" %");

    }
    public void updateVirtexToGoxProfitPercent()
    {
    	double profit = virtexToGoxProfit(ucr._sellingVirtex, ucr._buyingGox, ucr._1CAD, 1.0);
    	//double percent = profit/(buy*amount*(1+VIRTEXFEE)*cad) * 100;
    	double percent = profit / (ucr._sellingVirtex*1.0*ucr._1CAD) * 100;
    	if(percent > 0){
    		if(percent < 2)
        		virtexToMtgoxProfit.setForeground(Color.YELLOW);
    		else
    			virtexToMtgoxProfit.setForeground(Color.GREEN);
    	}
    		
    	else if(percent == -100){
    		virtexToMtgoxProfit.setText("loading 5s lol");return;}
    	else
    		virtexToMtgoxProfit.setForeground(Color.RED);
    	virtexToMtgoxProfit.setText(nf.format(percent)+" %");

    }
    public void updateGox(){
    	if(mtgoxUSD){
    		mtgoxLatestPriceLabel.setText("Latest Price: "+ucr._lastGox);
        	mtgoxHighLabel.setText("High: "+ucr._highGox);
        	mtgoxLowLabel.setText("Low: "+ucr._lowGox);
        	mtgoxBuyLabel.setText("Buy: "+ucr._buyingGox);
        	mtgoxSellLabel.setText("Sell: "+ucr._sellingGox);
    	}
    	else{
    		mtgoxLatestPriceLabel.setText("Latest Price: "+nf.format(ucr._lastGox*ucr._1USD));
        	mtgoxHighLabel.setText("High: "+nf.format(ucr._highGox*ucr._1USD));
        	mtgoxLowLabel.setText("Low: "+nf.format(ucr._lowGox*ucr._1USD));
        	mtgoxBuyLabel.setText("Buy: "+nf.format(ucr._buyingGox*ucr._1USD));
        	mtgoxSellLabel.setText("Sell: "+nf.format(ucr._sellingGox*ucr._1USD));
    	}
    }
    public void updateVirtex(){
    	if(virtexCAD){
    		virtexLatestPriceLabel.setText("Latest Price: "+ucr._lastVirtex);
        	virtexHighLabel.setText("High: "+ucr._highVirtex);
        	virtexLowLabel.setText("Low: "+ucr._lowVirtex);
        	virtexBuyLabel.setText("Buy: "+ucr._buyingVirtex);
        	virtexSellLabel.setText("Sell: "+ucr._sellingVirtex);
    	}
    	else{
    		virtexLatestPriceLabel.setText("Latest Price: "+nf.format(ucr._lastVirtex*ucr._1CAD));
        	virtexHighLabel.setText("High: "+nf.format(ucr._highVirtex*ucr._1CAD));
        	virtexLowLabel.setText("Low: "+nf.format(ucr._lowVirtex*ucr._1CAD));
        	virtexBuyLabel.setText("Buy: "+nf.format(ucr._buyingVirtex*ucr._1CAD));
        	virtexSellLabel.setText("Sell: "+nf.format(ucr._sellingVirtex*ucr._1CAD));
    	}
    	
    }
    
    public void calcAverages(){
    	double goxCalcAvg = ba.getCalcAverage(ucr._avgGox15m, ucr._avgGox1h, ucr._avgGox4h, ucr._avgGox12h, ucr._avgGox1d, ucr._avgGox2d, ucr._avgGox7d);
    	double virtexCalcAvg = ba.getCalcAverage(ucr._avgVirtex15m, ucr._avgVirtex1h, ucr._avgVirtex4h, ucr._avgVirtex12h, ucr._avgVirtex1d, ucr._avgVirtex2d, ucr._avgVirtex7d);
    	
    	double goxCalcPercent = (1-(ucr._lastGox/goxCalcAvg)) * 100.0;
    	double virtexCalcPercent = (1-(ucr._lastVirtex/virtexCalcAvg)) * 100.0;
    	
    	if(mtgoxUSD) {
    		lblCalculatedAvgGox.setText("Calculated Avg: " + nf.format(goxCalcAvg) + " USD");
    	}
    	else {
    		lblCalculatedAvgGox.setText("Calculated Avg: " + nf.format(goxCalcAvg*ucr._1USD) + " CAD");
    	}
    	if(virtexCAD) {
    		lblCalculatedAvgVirtex.setText("Calculated Avg: " + nf.format(virtexCalcAvg) + " CAD");
    	}
    	else {
    		lblCalculatedAvgVirtex.setText("Calculated Avg: " + nf.format(virtexCalcAvg*ucr._1CAD) + " USD");
    	}
    	
    	if(goxCalcPercent > 0){
    		if(goxCalcPercent < 2)
    			calcAvgGoxPercent.setForeground(Color.YELLOW);
    		else
    			calcAvgGoxPercent.setForeground(Color.GREEN);
    	}	
    	else {
    		calcAvgGoxPercent.setForeground(Color.RED);
    	}
    	calcAvgGoxPercent.setText(nf.format(goxCalcPercent) + "%");
    	
    	if(virtexCalcPercent > 0){
    		if(virtexCalcPercent < 2)
    			calcAvgVirtexPercent.setForeground(Color.YELLOW);
    		else
    			calcAvgVirtexPercent.setForeground(Color.GREEN);
    	}	
    	else {
    		calcAvgVirtexPercent.setForeground(Color.RED);
    	}
    	calcAvgVirtexPercent.setText(nf.format(virtexCalcPercent) + "%");
    	
    	
    	

    }
    public void updateConversion(){
    	currencyConversionLabel.setText("$1 USD = $"+ucr._1USD+" CAD     ||     $1 CAD = $"+ucr._1CAD+" USD");
    }
    public void updateAverageGox12h(){
    	if(mtgoxUSD)
    		mtgoxAverage.setText("12h Average: "+ucr._avgGox12h+" USD");
    	else
    		mtgoxAverage.setText("12h Average: "+nf.format(ucr._avgGox12h*ucr._1USD)+" CAD");
    }
    public void updateAverageVirtex12h(){
    	if(virtexCAD)
    		virtexAverage.setText("12h Average: "+ucr._avgVirtex12h+" CAD");
    	else
    		virtexAverage.setText("12h Average: "+nf.format(ucr._avgVirtex12h*ucr._1CAD)+" USD");
    }
    
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mtgoxUSDButton){
			mtgoxUSD = true;
			updateAll();
		}
		else if (e.getSource() == mtgoxCADButton){
			mtgoxUSD = false;
			updateAll();
		}
		else if (e.getSource() == virtexUSDButton){
			virtexCAD = false;
			updateAll();
		}
		else if (e.getSource() == virtexCADButton){
			virtexCAD = true;
			updateAll();
		}
		else if (e.getSource() == virexToGoxGetPotentialProfitBtn) {
			virtexToGoxCalcProfitLbl.setText(" GET PAID");//do things
			virtexToGoxCalcProfitLbl.setText(nf.format((virtexToGoxProfit(ucr._sellingVirtex, ucr._buyingGox, ucr._1CAD, Double.valueOf(virtexToGoxInput.getText())))) + " $ USD");
		}
		else if (e.getSource() == goxToVirtexGetPotentialProfitBtn) {
			goxToVirtexCalcProfitLbl.setText(" MAKE BANK");//do things
			goxToVirtexCalcProfitLbl.setText(nf.format((goxToVirtexProfit(ucr._sellingGox, ucr._buyingVirtex, ucr._1CAD, Double.valueOf(goxToVirtexInput.getText())))) + " $ USD");
		}
		else if (e.getSource() == refreshMenuItem) {
			BitcoinTrader.updateGeneralData();
			BitcoinTrader.updateAvgEx();
		}
	}
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        mtgoxButtonGroup = new javax.swing.ButtonGroup();
        virtexButtonGroup = new javax.swing.ButtonGroup();
        menuBar = new javax.swing.JMenuBar();
        menuBar.setBorderPainted(false);
        optionMenu = new javax.swing.JMenu();
        mtgoxAccountMenuItem = new javax.swing.JMenuItem();
        virtexAccountMenuItem = new javax.swing.JMenuItem();
        refreshMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Buttcoin Trader 9001 v0.2.63");
        setBackground(new java.awt.Color(153, 153, 153));
        setForeground(java.awt.Color.darkGray);
        setMinimumSize(new java.awt.Dimension(1024, 768));
        setResizable(false);;

        menuBar.setBackground(new java.awt.Color(153, 153, 153));

        optionMenu.setText("Options");

        mtgoxAccountMenuItem.setText("Select MtGox Account");
        optionMenu.add(mtgoxAccountMenuItem);

        virtexAccountMenuItem.setText("Select Virtex Account");
        optionMenu.add(virtexAccountMenuItem);

        refreshMenuItem.setText("Manual Refresh");
        refreshMenuItem.addActionListener(this);
        optionMenu.add(refreshMenuItem);

        menuBar.add(optionMenu);

        setJMenuBar(menuBar);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane);
        mainPanel = new javax.swing.JPanel();
        tabbedPane.addTab("Main", null, mainPanel, null);
        topPanel = new javax.swing.JPanel();
        currencyConversionLabel = new javax.swing.JLabel();
        virtexPanel = new javax.swing.JPanel();
        virtexLatestPriceLabel = new javax.swing.JLabel();
        virtexBuyFeeLabel = new javax.swing.JLabel();
        virtexSellFeeLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        virtexHighLabel = new javax.swing.JLabel();
        virtexLowLabel = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        virtexAverage = new javax.swing.JLabel();
        virtexBuyLabel = new javax.swing.JLabel();
        virtexSellLabel = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        mtgoxPanel = new javax.swing.JPanel();
        mtgoxBuyFeeLabel = new javax.swing.JLabel();
        mtgoxSellFeeLabel = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        mtgoxLatestPriceLabel = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        mtgoxHighLabel = new javax.swing.JLabel();
        mtgoxLowLabel = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        mtgoxAverage = new javax.swing.JLabel();
        mtgoxBuyLabel = new javax.swing.JLabel();
        mtgoxSellLabel = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        centerPanel = new javax.swing.JPanel();
        mtgoxUSDButton = new javax.swing.JRadioButton();
        mtgoxCADButton = new javax.swing.JRadioButton();
        virtexUSDButton = new javax.swing.JRadioButton();
        virtexCADButton = new javax.swing.JRadioButton();
        lblCalculatedAvgGox = new javax.swing.JLabel();
        lblCalculatedAvgVirtex = new javax.swing.JLabel();
        calcAvgVirtexPercent = new javax.swing.JLabel();
        calcAvgGoxPercent = new javax.swing.JLabel();
        
                mainPanel.setLayout(new java.awt.BorderLayout());
                
                        topPanel.setBackground(new java.awt.Color(102, 102, 102));
                        
                                currencyConversionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                                currencyConversionLabel.setText("$ USD = $ CAD");
                                currencyConversionLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Exchange Rate", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
                                currencyConversionLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                                currencyConversionLabel.setMaximumSize(new Dimension(150, 44));
                                topPanel.add(currencyConversionLabel);
                                
                                        mainPanel.add(topPanel, java.awt.BorderLayout.PAGE_START);
                                        
                                                virtexPanel.setBackground(new java.awt.Color(102, 102, 102));
                                                virtexPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Virtex", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
                                                
                                                        virtexLatestPriceLabel.setText("Latest Price: ");
                                                        
                                                                virtexBuyFeeLabel.setText("Buying Fee: "+VIRTEXFEE*100+"% CAD");
                                                                
                                                                        virtexSellFeeLabel.setText("Selling Fee: "+VIRTEXFEE*100+"% BTC");
                                                                        
                                                                                virtexHighLabel.setText("High:");
                                                                                
                                                                                        virtexLowLabel.setText("Low:");
                                                                                        
                                                                                                virtexAverage.setText("Average:");
                                                                                                
                                                                                                        virtexBuyLabel.setText("Buy: ");
                                                                                                        
                                                                                                                virtexSellLabel.setText("Sell: ");
                                                                                                                        
                                                                                                                        lblCalculatedAvgVirtex.setText("Calculated AVG:");
                                                                                                                        
                                                                                                                        calcAvgVirtexPercent.setText("%");
                                                                                                                        calcAvgVirtexPercent.setFont(new Font("Arial", Font.BOLD, 30));
                                                                                                                
                                                                                                                        javax.swing.GroupLayout gl_virtexPanel = new javax.swing.GroupLayout(virtexPanel);
                                                                                                                        gl_virtexPanel.setHorizontalGroup(
                                                                                                                        	gl_virtexPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                        		.addGroup(gl_virtexPanel.createSequentialGroup()
                                                                                                                        			.addContainerGap()
                                                                                                                        			.addGroup(gl_virtexPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                        				.addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                                                                                                                        				.addComponent(virtexBuyFeeLabel)
                                                                                                                        				.addComponent(virtexSellFeeLabel)
                                                                                                                        				.addComponent(virtexLatestPriceLabel)
                                                                                                                        				.addComponent(jSeparator2, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                                                                        				.addComponent(virtexHighLabel)
                                                                                                                        				.addComponent(virtexLowLabel)
                                                                                                                        				.addComponent(jSeparator5, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                                                                        				.addComponent(virtexBuyLabel)
                                                                                                                        				.addComponent(virtexSellLabel)
                                                                                                                        				.addComponent(jSeparator7, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                                                                        				.addComponent(virtexAverage)
                                                                                                                        				.addComponent(lblCalculatedAvgVirtex)
                                                                                                                        				.addComponent(calcAvgVirtexPercent))
                                                                                                                        			.addContainerGap())
                                                                                                                        );
                                                                                                                        gl_virtexPanel.setVerticalGroup(
                                                                                                                        	gl_virtexPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                        		.addGroup(gl_virtexPanel.createSequentialGroup()
                                                                                                                        			.addComponent(virtexBuyFeeLabel)
                                                                                                                        			.addPreferredGap(ComponentPlacement.RELATED)
                                                                                                                        			.addComponent(virtexSellFeeLabel)
                                                                                                                        			.addGap(14)
                                                                                                                        			.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                                                                        			.addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                                                        			.addComponent(virtexLatestPriceLabel)
                                                                                                                        			.addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                                                        			.addComponent(jSeparator2, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                                                                                                        			.addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                                                        			.addComponent(virtexHighLabel)
                                                                                                                        			.addGap(18)
                                                                                                                        			.addComponent(virtexLowLabel)
                                                                                                                        			.addGap(18)
                                                                                                                        			.addComponent(jSeparator5, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                                                                                                        			.addPreferredGap(ComponentPlacement.RELATED)
                                                                                                                        			.addComponent(virtexBuyLabel)
                                                                                                                        			.addGap(18)
                                                                                                                        			.addComponent(virtexSellLabel)
                                                                                                                        			.addGap(18)
                                                                                                                        			.addComponent(jSeparator7, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                                                                                                        			.addPreferredGap(ComponentPlacement.RELATED)
                                                                                                                        			.addComponent(virtexAverage)
                                                                                                                        			.addGap(92)
                                                                                                                        			.addComponent(lblCalculatedAvgVirtex)
                                                                                                                        			.addPreferredGap(ComponentPlacement.RELATED)
                                                                                                                        			.addComponent(calcAvgVirtexPercent)
                                                                                                                        			.addContainerGap(227, Short.MAX_VALUE))
                                                                                                                        );
                                                                                                                        virtexPanel.setLayout(gl_virtexPanel);
                                                                                                                        
                                                                                                                                mainPanel.add(virtexPanel, java.awt.BorderLayout.LINE_END);
                                                                                                                                
                                                                                                                                        mtgoxPanel.setBackground(new java.awt.Color(102, 102, 102));
                                                                                                                                        mtgoxPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mt Gox", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
                                                                                                                                        
                                                                                                                                                mtgoxBuyFeeLabel.setText("Buying Fee: "+MTGOXFEE*100+"% BTC");
                                                                                                                                                
                                                                                                                                                        mtgoxSellFeeLabel.setText("Selling Fee: "+MTGOXFEE*100+"% USD");
                                                                                                                                                        
                                                                                                                                                                mtgoxLatestPriceLabel.setText("Latest Price: ");
                                                                                                                                                                
                                                                                                                                                                        mtgoxHighLabel.setText("High:");
                                                                                                                                                                        
                                                                                                                                                                                mtgoxLowLabel.setText("Low:");
                                                                                                                                                                                
                                                                                                                                                                                        mtgoxAverage.setText("Average:");
                                                                                                                                                                                        
                                                                                                                                                                                                mtgoxBuyLabel.setText("Buy: ");
                                                                                                                                                                                                
                                                                                                                                                                                                        mtgoxSellLabel.setText("Sell: ");
                                                                                                                                                                                                                
                                                                                                                                                                                                                lblCalculatedAvgGox.setText("Calculated AVG:");
                                                                                                                                                                                                                
                                                                                                                                                                                                                calcAvgGoxPercent.setText("%");
                                                                                                                                                                                                                calcAvgGoxPercent.setFont(new Font("Arial", Font.BOLD, 30));
                                                                                                                                                                                                        
                                                                                                                                                                                                                javax.swing.GroupLayout gl_mtgoxPanel = new javax.swing.GroupLayout(mtgoxPanel);
                                                                                                                                                                                                                gl_mtgoxPanel.setHorizontalGroup(
                                                                                                                                                                                                                	gl_mtgoxPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                                                                                                                		.addGroup(gl_mtgoxPanel.createSequentialGroup()
                                                                                                                                                                                                                			.addContainerGap()
                                                                                                                                                                                                                			.addGroup(gl_mtgoxPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                                                                                                                				.addComponent(jSeparator3, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                                                                                                                                                                                                                				.addComponent(mtgoxBuyFeeLabel)
                                                                                                                                                                                                                				.addComponent(mtgoxLatestPriceLabel)
                                                                                                                                                                                                                				.addComponent(jSeparator4, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                				.addComponent(mtgoxHighLabel)
                                                                                                                                                                                                                				.addComponent(mtgoxLowLabel)
                                                                                                                                                                                                                				.addComponent(jSeparator6, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                				.addComponent(mtgoxSellFeeLabel)
                                                                                                                                                                                                                				.addComponent(mtgoxBuyLabel)
                                                                                                                                                                                                                				.addComponent(mtgoxSellLabel)
                                                                                                                                                                                                                				.addComponent(mtgoxAverage)
                                                                                                                                                                                                                				.addComponent(jSeparator8, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                				.addComponent(lblCalculatedAvgGox)
                                                                                                                                                                                                                				.addComponent(calcAvgGoxPercent))
                                                                                                                                                                                                                			.addContainerGap())
                                                                                                                                                                                                                );
                                                                                                                                                                                                                gl_mtgoxPanel.setVerticalGroup(
                                                                                                                                                                                                                	gl_mtgoxPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                                                                                                                		.addGroup(gl_mtgoxPanel.createSequentialGroup()
                                                                                                                                                                                                                			.addComponent(mtgoxBuyFeeLabel)
                                                                                                                                                                                                                			.addGap(3)
                                                                                                                                                                                                                			.addComponent(mtgoxSellFeeLabel)
                                                                                                                                                                                                                			.addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                                                                                                                                                			.addComponent(jSeparator3, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                			.addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                                                                                                                                                			.addComponent(mtgoxLatestPriceLabel)
                                                                                                                                                                                                                			.addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                                                                                                                                                			.addComponent(jSeparator4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                			.addGap(18)
                                                                                                                                                                                                                			.addComponent(mtgoxHighLabel)
                                                                                                                                                                                                                			.addGap(18)
                                                                                                                                                                                                                			.addComponent(mtgoxLowLabel)
                                                                                                                                                                                                                			.addGap(18)
                                                                                                                                                                                                                			.addComponent(jSeparator6, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                			.addPreferredGap(ComponentPlacement.RELATED)
                                                                                                                                                                                                                			.addComponent(mtgoxBuyLabel)
                                                                                                                                                                                                                			.addGap(18)
                                                                                                                                                                                                                			.addComponent(mtgoxSellLabel)
                                                                                                                                                                                                                			.addGap(18)
                                                                                                                                                                                                                			.addComponent(jSeparator8, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                			.addPreferredGap(ComponentPlacement.RELATED)
                                                                                                                                                                                                                			.addComponent(mtgoxAverage)
                                                                                                                                                                                                                			.addGap(96)
                                                                                                                                                                                                                			.addComponent(lblCalculatedAvgGox)
                                                                                                                                                                                                                			.addPreferredGap(ComponentPlacement.RELATED)
                                                                                                                                                                                                                			.addComponent(calcAvgGoxPercent)
                                                                                                                                                                                                                			.addContainerGap(212, Short.MAX_VALUE))
                                                                                                                                                                                                                );
                                                                                                                                                                                                                mtgoxPanel.setLayout(gl_mtgoxPanel);
                                                                                                                                                                                                                
                                                                                                                                                                                                                        mainPanel.add(mtgoxPanel, java.awt.BorderLayout.LINE_START);
                                                                                                                                                                                                                        
                                                                                                                                                                                                                                centerPanel.setBackground(new java.awt.Color(102, 102, 102));
                                                                                                                                                                                                                                
                                                                                                                                                                                                                                        mtgoxUSDButton.setBackground(new java.awt.Color(102, 102, 102));
                                                                                                                                                                                                                                        mtgoxButtonGroup.add(mtgoxUSDButton);
                                                                                                                                                                                                                                        mtgoxUSDButton.setText("USD");
                                                                                                                                                                                                                                        mtgoxUSDButton.setSelected(true);
                                                                                                                                                                                                                                        mtgoxUSDButton.addActionListener(this);
                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                mtgoxCADButton.setBackground(new java.awt.Color(102, 102, 102));
                                                                                                                                                                                                                                                mtgoxButtonGroup.add(mtgoxCADButton);
                                                                                                                                                                                                                                                mtgoxCADButton.setText("CAD");
                                                                                                                                                                                                                                                mtgoxCADButton.addActionListener(this);
                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                        virtexUSDButton.setBackground(new java.awt.Color(102, 102, 102));
                                                                                                                                                                                                                                                        virtexButtonGroup.add(virtexUSDButton);
                                                                                                                                                                                                                                                        virtexUSDButton.setText("USD");
                                                                                                                                                                                                                                                        virtexUSDButton.addActionListener(this);
                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                virtexCADButton.setBackground(new java.awt.Color(102, 102, 102));
                                                                                                                                                                                                                                                                virtexButtonGroup.add(virtexCADButton);
                                                                                                                                                                                                                                                                virtexCADButton.setText("CAD");
                                                                                                                                                                                                                                                                virtexCADButton.setSelected(true);
                                                                                                                                                                                                                                                                virtexCADButton.addActionListener(this);
                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                goxToVirtexPanel = new JPanel();
                                                                                                                                                                                                                                                                goxToVirtexPanel.setBackground(new Color(102, 102, 102));
                                                                                                                                                                                                                                                                goxToVirtexPanel.setBorder(new TitledBorder(null, "Buy at Mt Gox, sell at Virtex", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                virtexToGoxPanel = new JPanel();
                                                                                                                                                                                                                                                                virtexToGoxPanel.setBackground(new Color(102, 102, 102));
                                                                                                                                                                                                                                                                virtexToGoxPanel.setBorder(new TitledBorder(null, "Buy at Virtex, sell at Mt Gox", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                        javax.swing.GroupLayout gl_centerPanel = new javax.swing.GroupLayout(centerPanel);
                                                                                                                                                                                                                                                                        gl_centerPanel.setHorizontalGroup(
                                                                                                                                                                                                                                                                        	gl_centerPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                                                                                                                                                                        		.addGroup(gl_centerPanel.createSequentialGroup()
                                                                                                                                                                                                                                                                        			.addContainerGap()
                                                                                                                                                                                                                                                                        			.addGroup(gl_centerPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                                                                                                                                                                        				.addComponent(goxToVirtexPanel, GroupLayout.PREFERRED_SIZE, 558, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                                                                        				.addComponent(virtexToGoxPanel, GroupLayout.PREFERRED_SIZE, 559, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                                                                        				.addGroup(gl_centerPanel.createSequentialGroup()
                                                                                                                                                                                                                                                                        					.addGroup(gl_centerPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                                                                                                                                                                        						.addComponent(mtgoxCADButton)
                                                                                                                                                                                                                                                                        						.addComponent(mtgoxUSDButton))
                                                                                                                                                                                                                                                                        					.addPreferredGap(ComponentPlacement.RELATED, 470, Short.MAX_VALUE)
                                                                                                                                                                                                                                                                        					.addGroup(gl_centerPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                                                                                                                                                                        						.addComponent(virtexCADButton)
                                                                                                                                                                                                                                                                        						.addComponent(virtexUSDButton))))
                                                                                                                                                                                                                                                                        			.addContainerGap())
                                                                                                                                                                                                                                                                        );
                                                                                                                                                                                                                                                                        gl_centerPanel.setVerticalGroup(
                                                                                                                                                                                                                                                                        	gl_centerPanel.createParallelGroup(Alignment.LEADING)
                                                                                                                                                                                                                                                                        		.addGroup(gl_centerPanel.createSequentialGroup()
                                                                                                                                                                                                                                                                        			.addContainerGap()
                                                                                                                                                                                                                                                                        			.addGroup(gl_centerPanel.createParallelGroup(Alignment.BASELINE)
                                                                                                                                                                                                                                                                        				.addComponent(mtgoxUSDButton)
                                                                                                                                                                                                                                                                        				.addComponent(virtexUSDButton))
                                                                                                                                                                                                                                                                        			.addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                                                                                                                                                                                                        			.addGroup(gl_centerPanel.createParallelGroup(Alignment.BASELINE)
                                                                                                                                                                                                                                                                        				.addComponent(mtgoxCADButton)
                                                                                                                                                                                                                                                                        				.addComponent(virtexCADButton))
                                                                                                                                                                                                                                                                        			.addPreferredGap(ComponentPlacement.UNRELATED)
                                                                                                                                                                                                                                                                        			.addComponent(goxToVirtexPanel, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                                                                        			.addPreferredGap(ComponentPlacement.RELATED)
                                                                                                                                                                                                                                                                        			.addComponent(virtexToGoxPanel, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                                                                                        			.addContainerGap(334, Short.MAX_VALUE))
                                                                                                                                                                                                                                                                        );
                                                                                                                                                                                                                                                                        virtexToGoxPanel.setLayout(null);
                                                                                                                                                                                                                                                                        virtexToMtgoxProfit = new javax.swing.JLabel();
                                                                                                                                                                                                                                                                        virtexToMtgoxProfit.setBounds(223, 23, 190, 33);
                                                                                                                                                                                                                                                                        virtexToGoxPanel.add(virtexToMtgoxProfit);
                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                virtexToMtgoxProfit.setFont(new Font("Agency FB", Font.BOLD, 28)); // NOI18N
                                                                                                                                                                                                                                                                                virtexToMtgoxProfit.setText("% profit");
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                panel_3 = new JPanel();
                                                                                                                                                                                                                                                                                panel_3.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
                                                                                                                                                                                                                                                                                panel_3.setBackground(new Color(153, 153, 153));
                                                                                                                                                                                                                                                                                panel_3.setBounds(10, 67, 539, 45);
                                                                                                                                                                                                                                                                                virtexToGoxPanel.add(panel_3);
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                lblOfBtc_1 = new JLabel("# of BTC to Trade: ");
                                                                                                                                                                                                                                                                                panel_3.add(lblOfBtc_1);
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                virtexToGoxInput = new JTextField();
                                                                                                                                                                                                                                                                                panel_3.add(virtexToGoxInput);
                                                                                                                                                                                                                                                                                virtexToGoxInput.setColumns(10);
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                virexToGoxGetPotentialProfitBtn = new JButton("Get Potential Profit");
                                                                                                                                                                                                                                                                                virexToGoxGetPotentialProfitBtn.addActionListener(this);
                                                                                                                                                                                                                                                                                panel_3.add(virexToGoxGetPotentialProfitBtn);
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                virtexToGoxCalcProfitLbl = new JLabel("   $ USD");
                                                                                                                                                                                                                                                                                panel_3.add(virtexToGoxCalcProfitLbl);
                                                                                                                                                                                                                                                                                goxToVirtexPanel.setLayout(null);
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                JPanel panel = new JPanel();
                                                                                                                                                                                                                                                                                panel.setBackground(new Color(153, 153, 153));
                                                                                                                                                                                                                                                                                panel.setBounds(10, 75, 538, 45);
                                                                                                                                                                                                                                                                                goxToVirtexPanel.add(panel);
                                                                                                                                                                                                                                                                                panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
                                                                                                                                                                                                                                                                                panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                JLabel lblOfBtc = new JLabel("# of BTC to Trade: ");
                                                                                                                                                                                                                                                                                panel.add(lblOfBtc);
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                goxToVirtexInput = new JTextField();
                                                                                                                                                                                                                                                                                panel.add(goxToVirtexInput);
                                                                                                                                                                                                                                                                                goxToVirtexInput.setColumns(10);
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                goxToVirtexGetPotentialProfitBtn = new JButton("Get Potential Profit");
                                                                                                                                                                                                                                                                                goxToVirtexGetPotentialProfitBtn.addActionListener(this);
                                                                                                                                                                                                                                                                                panel.add(goxToVirtexGetPotentialProfitBtn);
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                goxToVirtexCalcProfitLbl = new JLabel("    $ USD");
                                                                                                                                                                                                                                                                                panel.add(goxToVirtexCalcProfitLbl);
                                                                                                                                                                                                                                                                                mtgoxToVirtexProfitLabel = new javax.swing.JLabel();
                                                                                                                                                                                                                                                                                mtgoxToVirtexProfitLabel.setBounds(223, 23, 190, 33);
                                                                                                                                                                                                                                                                                goxToVirtexPanel.add(mtgoxToVirtexProfitLabel);
                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                        mtgoxToVirtexProfitLabel.setFont(new Font("Agency FB", Font.BOLD, 28)); // NOI18N
                                                                                                                                                                                                                                                                                        mtgoxToVirtexProfitLabel.setText("% profit");
                                                                                                                                                                                                                                                                                        centerPanel.setLayout(gl_centerPanel);
                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                mainPanel.add(centerPanel, java.awt.BorderLayout.CENTER);
                                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                Trades = new JPanel();
                                                                                                                                                                                                                                                                                                Trades.setBackground(Color.GRAY);
                                                                                                                                                                                                                                                                                                tabbedPane.addTab("Trades", null, Trades, null);
                                                                                                                                                                                                                                                                                                tabbedPane.setForegroundAt(1, Color.BLACK);
                                                                                                                                                                                                                                                                                                tabbedPane.setEnabledAt(1, true);
                                                                                                                                                                                                                                                                                                tabbedPane.setBackgroundAt(1, Color.WHITE);
                                                                                                                                                                                                                                                                                                Trades.setLayout(null);
                                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                table = new JTable();
                                                                                                                                                                                                                                                                                                table.setForeground(Color.WHITE);
                                                                                                                                                                                                                                                                                                table.setBackground(Color.DARK_GRAY);
                                                                                                                                                                                                                                                                                                table.setColumnSelectionAllowed(true);
                                                                                                                                                                                                                                                                                                table.setCellSelectionEnabled(true);
                                                                                                                                                                                                                                                                                                table.setModel(new DefaultTableModel(
                                                                                                                                                                                                                                                                                                	new Object[][] {
                                                                                                                                                                                                                                                                                                		{new Integer(1), null, null, "CAD", "virtex"},
                                                                                                                                                                                                                                                                                                		{new Integer(2), null, null, "CAD", "virtex"},
                                                                                                                                                                                                                                                                                                		{new Integer(3), null, null, "CAD", "virtex"},
                                                                                                                                                                                                                                                                                                		{new Integer(4), null, null, "CAD", "virtex"},
                                                                                                                                                                                                                                                                                                		{new Integer(5), null, null, "CAD", "virtex"},
                                                                                                                                                                                                                                                                                                		{new Integer(6), null, null, "USD", "mtgox"},
                                                                                                                                                                                                                                                                                                		{new Integer(7), null, null, "USD", "mtgox"},
                                                                                                                                                                                                                                                                                                		{new Integer(8), null, null, "USD", "mtgox"},
                                                                                                                                                                                                                                                                                                		{new Integer(9), null, null, "USD", "mtgox"},
                                                                                                                                                                                                                                                                                                		{new Integer(10), null, null, "USD", "mtgox"},
                                                                                                                                                                                                                                                                                                	},
                                                                                                                                                                                                                                                                                                	new String[] {
                                                                                                                                                                                                                                                                                                		"ID", "BTC", "Price", "Currency", "Exchange"
                                                                                                                                                                                                                                                                                                	}
                                                                                                                                                                                                                                                                                                ) {
                                                                                                                                                                                                                                                                                                	Class[] columnTypes = new Class[] {
                                                                                                                                                                                                                                                                                                		Integer.class, Double.class, Double.class, String.class, String.class
                                                                                                                                                                                                                                                                                                	};
                                                                                                                                                                                                                                                                                                	public Class getColumnClass(int columnIndex) {
                                                                                                                                                                                                                                                                                                		return columnTypes[columnIndex];
                                                                                                                                                                                                                                                                                                	}
                                                                                                                                                                                                                                                                                                	boolean[] columnEditables = new boolean[] {
                                                                                                                                                                                                                                                                                                		false, true, true, true, true
                                                                                                                                                                                                                                                                                                	};
                                                                                                                                                                                                                                                                                                	public boolean isCellEditable(int row, int column) {
                                                                                                                                                                                                                                                                                                		return columnEditables[column];
                                                                                                                                                                                                                                                                                                	}
                                                                                                                                                                                                                                                                                                });
                                                                                                                                                                                                                                                                                                table.setBounds(0, 26, 710, 160);
                                                                                                                                                                                                                                                                                                Trades.add(table);
                                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                JTextPane txtpnId = new JTextPane();
                                                                                                                                                                                                                                                                                                txtpnId.setBackground(Color.GRAY);
                                                                                                                                                                                                                                                                                                txtpnId.setText("ID");
                                                                                                                                                                                                                                                                                                txtpnId.setBounds(64, 0, 18, 22);
                                                                                                                                                                                                                                                                                                Trades.add(txtpnId);
                                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                JTextPane txtpnBtc = new JTextPane();
                                                                                                                                                                                                                                                                                                txtpnBtc.setBackground(Color.GRAY);
                                                                                                                                                                                                                                                                                                txtpnBtc.setText("BTC");
                                                                                                                                                                                                                                                                                                txtpnBtc.setBounds(190, 0, 29, 22);
                                                                                                                                                                                                                                                                                                Trades.add(txtpnBtc);
                                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                JTextPane txtpnPrice = new JTextPane();
                                                                                                                                                                                                                                                                                                txtpnPrice.setBackground(Color.GRAY);
                                                                                                                                                                                                                                                                                                txtpnPrice.setText("Price");
                                                                                                                                                                                                                                                                                                txtpnPrice.setBounds(336, 0, 34, 22);
                                                                                                                                                                                                                                                                                                Trades.add(txtpnPrice);
                                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                JTextPane txtpnCurrency = new JTextPane();
                                                                                                                                                                                                                                                                                                txtpnCurrency.setBackground(Color.GRAY);
                                                                                                                                                                                                                                                                                                txtpnCurrency.setText("Currency");
                                                                                                                                                                                                                                                                                                txtpnCurrency.setBounds(463, 0, 57, 22);
                                                                                                                                                                                                                                                                                                Trades.add(txtpnCurrency);
                                                                                                                                                                                                                                                                                                
                                                                                                                                                                                                                                                                                                JTextPane txtpnExchange = new JTextPane();
                                                                                                                                                                                                                                                                                                txtpnExchange.setBackground(Color.GRAY);
                                                                                                                                                                                                                                                                                                txtpnExchange.setText("Exchange");
                                                                                                                                                                                                                                                                                                txtpnExchange.setBounds(609, 0, 60, 22);
                                                                                                                                                                                                                                                                                                Trades.add(txtpnExchange);

        pack();
    }// </editor-fold>

     

    // Variables declaration - do not modify
    private JPanel centerPanel;
    private JLabel currencyConversionLabel;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JSeparator jSeparator3;
    private JSeparator jSeparator4;
    private JSeparator jSeparator5;
    private JSeparator jSeparator6;
    private JSeparator jSeparator7;
    private JSeparator jSeparator8;
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JMenuItem mtgoxAccountMenuItem;
    private JLabel mtgoxAverage;
    private ButtonGroup mtgoxButtonGroup;
    private JLabel mtgoxBuyFeeLabel;
    private JLabel mtgoxBuyLabel;
    private JRadioButton mtgoxCADButton;
    private JLabel mtgoxHighLabel;
    private JLabel mtgoxLatestPriceLabel;
    private JLabel mtgoxLowLabel;
    private JPanel mtgoxPanel;
    private JLabel mtgoxSellFeeLabel;
    private JLabel mtgoxSellLabel;
    private JLabel mtgoxToVirtexProfitLabel;
    private JRadioButton mtgoxUSDButton;
    private JMenu optionMenu;
    private JMenuItem refreshMenuItem;
    private JPanel topPanel;
    private JMenuItem virtexAccountMenuItem;
    private JLabel virtexAverage;
    private ButtonGroup virtexButtonGroup;
    private JLabel virtexBuyFeeLabel;
    private JLabel virtexBuyLabel;
    private JRadioButton virtexCADButton;
    private JLabel virtexHighLabel;
    private JLabel virtexLatestPriceLabel;
    private JLabel virtexLowLabel;
    private JPanel virtexPanel;
    private JLabel virtexSellFeeLabel;
    private JLabel virtexSellLabel;
    private JLabel virtexToMtgoxProfit;
    private JRadioButton virtexUSDButton;
    private JTextField goxToVirtexInput;
    private JPanel goxToVirtexPanel;
    private JPanel virtexToGoxPanel;
    private JPanel panel_3;
    private JLabel lblOfBtc_1;
    private JTextField virtexToGoxInput;
    private JButton virexToGoxGetPotentialProfitBtn;
    private JLabel virtexToGoxCalcProfitLbl;
    private JButton goxToVirtexGetPotentialProfitBtn;
    private JLabel goxToVirtexCalcProfitLbl;
    private JTabbedPane tabbedPane;
    private JPanel Trades;
    private JTable table;
    private JLabel lblCalculatedAvgGox;
    private JLabel lblCalculatedAvgVirtex;
    private JLabel calcAvgGoxPercent;
    private JLabel calcAvgVirtexPercent;
}
