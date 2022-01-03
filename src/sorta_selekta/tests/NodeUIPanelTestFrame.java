package sorta_selekta.tests;
import java.awt.EventQueue;

import javax.swing.JFrame;

import sorta_selekta.gui_objects.NodeUIPanel;

public class NodeUIPanelTestFrame extends JFrame {

	NodeUIPanel ui;
	private int xsize = 500;
	private int ysize = 825;
	
	public NodeUIPanelTestFrame(){
		initUI();
	}
	private void initUI(){				
		setTitle("NodeUIPanelTest");
		setSize(xsize, ysize);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ui = new NodeUIPanel();
		add(ui);
		//statusMessage("urg...");
	}
	// runnable =-=-=-=--=-====--------------------------------------------------
		public static void main(String[] args) {
	        EventQueue.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	            	NodeUIPanelTestFrame ex = new NodeUIPanelTestFrame();
	                ex.setVisible(true);
	            }
	        });
	    }
}
