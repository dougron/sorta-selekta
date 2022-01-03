package sorta_selekta.interaction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import UDPUtils.OSCMessMaker;
import UDPUtils.StaticUDPConnection;

public class PlayButton extends JButton {


	private int state = 0;		// 0 = stop, 1 = play
//	private PlayButtonParent parent;
	private StaticUDPConnection conn = new StaticUDPConnection(7800);
	
	public PlayButton(){
		super();
//		parent = p;
		setButtonText();
		addActionListener(actionListener());
	}
	private ActionListener actionListener() {		
		return new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				toggleTransport();
				
//				System.out.println(mm.toString());
//				System.out.println("state changed to " + state);
			}	
		};
	}
	public void toggleTransport(){
		state = (state + 1) % 2;
		setButtonText();
		sendStateMessage(state);
	}
	public void sendStopTransportMessage(){
		sendStateMessage(0);		// 0 - stop transport
	}
	private void sendStateMessage(int i){
		OSCMessMaker mm = new OSCMessMaker();
		mm.addItem(GLOBAL);
		if (i == 0){
			mm.addItem(STOP_MESSAGE);
		} else {
			mm.addItem(PLAY_MESSAGE);
		}
		conn.sendUDPMessage(mm);
	}
	private void setButtonText() {
		if (state == 0){
			setText(PLAY_TEXT);
		} else {
			setText(STOP_TEXT);
		}
		
	}
	
	private static final String GLOBAL = "global";
	protected static final String PLAY_MESSAGE = "play";
	protected static final String STOP_MESSAGE = "stop";
	protected static final String PLAY_TEXT = "PLAY";
	protected static final String STOP_TEXT = "STOP";
}

