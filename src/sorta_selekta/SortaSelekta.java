package sorta_selekta;
import java.awt.EventQueue;

import javax.swing.JFrame;

import sorta_selekta.gui_objects.NodeManagerPanel;
import sorta_selekta.setup.Setup_SelektaAnalysis_001;





public class SortaSelekta extends JFrame{
	
	// if you are looking to change the setup, go the Setup files

	public static int sendPort = 7800;
//	public static int receivePort = 7801;	// not being used currently but conforms to 2019 UDPReceiver patch setup: receivePort = sendPort + 1
	private int xsize = 1100;
	private int ysize = 700;
	private NodeManagerPanel ui;
	//private ChordScaleDictionary csd = new ChordScaleDictionary();

	public SortaSelekta(){
		initUI();
	}

// privates ===========================================================================

		private void initUI(){				
			setTitle("SortaSelektaGameLoopTest");
			setSize(xsize, ysize);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			statusLine = addStatusBar();
			ui = new NodeManagerPanel(new Setup_SelektaAnalysis_001());		// this is the setup file
			
			add(ui);
			//statusMessage("urg...");
		}
	
	
// runnable =-=-=-=--=-====--------------------------------------------------
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	SortaSelekta ex = new SortaSelekta();
                ex.setVisible(true);
            }
        });
    }
}