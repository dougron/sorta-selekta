package sorta_selekta.interaction;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ButtonPanelFrame extends JFrame implements PlayButtonParent{
	
	private int xsize = 500;
	private int ysize = 300;
	
	public ButtonPanelFrame(){
		initUI();
	}
	
	private void initUI(){
		setTitle("ButtonTestPanel");
		setSize(xsize, ysize);
		JButton button = getPlayButton();
		setLayout(new FlowLayout());
		add(button);
	}
	
	private JButton getPlayButton(){
		JButton button = new PlayButton();
		
		return button;
	}

	
	// runnable =-=-=-=--=-====--------------------------------------------------
		public static void main(String[] args) {
	        EventQueue.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	            	ButtonPanelFrame ex = new ButtonPanelFrame();
	                ex.setVisible(true);
	            }
	        });
	    }

}
