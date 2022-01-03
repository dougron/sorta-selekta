package sorta_selekta.interaction;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

import sorta_selekta.gui_objects.NodeManagerPanel;
import sorta_selekta.node_list_generator.NodeListGenerator;

public class NLGButton extends JRadioButton {
	
	private NodeManagerPanel nmp;
	private NodeListGenerator nlg;
	
	public NLGButton(NodeManagerPanel nmp, NodeListGenerator nlg, Color bg){
		super(nlg.name);
		this.nmp = nmp;
		this.nlg = nlg;
		setBackground(bg);
		addActionListener(actionListener());
	}

	private ActionListener actionListener() {		
		return new ActionListener(){
			public void actionPerformed(ActionEvent ae){
					if (isSelected()){
						nmp.displayNodeListGenerator(nlg);
						nmp.updatePipelinePanel();
						nmp.updateUndoList();
						//System.out.println("setting timer");
						nmp.getDisplay().getUIPanel().setTimerForOverlapTest(nmp);
					}
			}
		};
	}
	public NodeListGenerator getNodeListGenerator(){
		return nlg;
	}

//	public NLGButton() {
//		// TODO Auto-generated constructor stub
//	}

//	public NLGButton(Icon arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}

//	public NLGButton(Action arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}

//	public NLGButton(String arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}

//	public NLGButton(Icon arg0, boolean arg1) {
//		super(arg0, arg1);
//		// TODO Auto-generated constructor stub
//	}

//	public NLGButton(String arg0, boolean arg1) {
//		super(arg0, arg1);
//		// TODO Auto-generated constructor stub
//	}

//	public NLGButton(String arg0, Icon arg1) {
//		super(arg0, arg1);
//		// TODO Auto-generated constructor stub
//	}

//	public NLGButton(String arg0, Icon arg1, boolean arg2) {
//		super(arg0, arg1, arg2);
//		// TODO Auto-generated constructor stub
//	}

}
