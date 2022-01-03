package sorta_selekta.gui_objects;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import DataObjects.ableton_live_clip.LiveClip;
import sorta_selekta.interaction.NLGButton;
import sorta_selekta.interaction.PlayButton;
import sorta_selekta.node_list_generator.NodeListGenerator;
import sorta_selekta.node_list_generator.node.Node;
import sorta_selekta.node_manager.NodeManager;
import sorta_selekta.setup.NodeManagerSetupObject;

public class NodeManagerPanel extends JPanel{

	public NodeManager nm;
	
	private int buttonWrapLength = 8;
	private NodeListDisplay display;
	private JPanel instrumentButtonPanel;
	private ArrayList<NLGButton> instrumentButtonList = new ArrayList<NLGButton>();
	
	
	private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	private static final String PLAY = "play";
	private static final String UNDO = "undo";
	private static final String SHIFT_DOWN = "shift down";
	private static final String SHIFT_UP = "shift up";
	private static final String LIST = "list to system";
	private static final String INITIALIZE = "initialize Live ClipObjects";
	private static final String PLAYSTOP = "toggle transport";
	private static final String NEXT_INSTRUMENT_LEFT = "instrument left";
	private static final String NEXT_INSTRUMENT_RIGHT = "instrument right";
	private static final String NUMPAD_NORTH = "numpad north";
	private static final String NUMPAD_NORTHEAST = "numpad northeast";
	private static final String NUMPAD_NORTHWEST = "numpad northwest";
	private static final String NUMPAD_EAST = "numpad east";
	private static final String NUMPAD_WEST = "numpad west";
	private static final String NUMPAD_SOUTH = "numpad south";
	private static final String NUMPAD_SOUTHEAST = "numpad southeast";
	private static final String NUMPAD_SOUTHWEST = "numpad southwest";
	private static final String ARROW_UP = "arrow up";
	private static final String ARROW_DOWN = "arrow down";

	private static final Object NUMPAD_NOUGHT = "numpad nought";
	
	private Color bgColor = new Color(100, 100, 100);
	private PlayButton playButton;
	
	public NodeManagerPanel(NodeManagerSetupObject nmso){
		nm = new NodeManager(nmso);
		setBackground(bgColor);
		setLayout(new BorderLayout());
		instrumentButtonPanel = makeButtonPanel(nm, bgColor);
		add(instrumentButtonPanel, BorderLayout.SOUTH);
		display = new NodeListDisplay(bgColor);
		add(display, BorderLayout.NORTH);
		addKeyBindings();
	}
	
	public void displayNodeListGenerator(NodeListGenerator nlg){
		//System.out.println("NodeManagerPanel: " + nlg.name + " passed to NodeDisplay");
		display.setNodeList(nlg);
		
	}
	public void updatePipelinePanel(){
		display.updatePipelinePanel();
	}
	public void updateUndoList(){
		display.updateUndoPanel(nm.undoList, nm.undoListPlayIndex, nm.undoListCueIndex);
	}
	public NodeListDisplay getDisplay(){
		return display;
	}
	    
	
// privates --------------------------------------------------------------
 	private void addKeyBindings() {
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("P"), PLAY);
//		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("U"), UNDO);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("Z"), SHIFT_DOWN);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("released Z"), SHIFT_UP);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("L"), LIST);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("R"), INITIALIZE);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("ENTER"), PLAYSTOP);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("A"), NEXT_INSTRUMENT_LEFT);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("D"), NEXT_INSTRUMENT_RIGHT);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("W"), ARROW_UP);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("S"), ARROW_DOWN);
//		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("LEFT"), NEXT_INSTRUMENT_LEFT);
//		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("RIGHT"), NEXT_INSTRUMENT_RIGHT);
//		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("UP"), ARROW_UP);
//		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("DOWN"), ARROW_DOWN);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD7"), NUMPAD_NORTHWEST);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD8"), NUMPAD_NORTH);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD9"), NUMPAD_NORTHEAST);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD6"), NUMPAD_EAST);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD4"), NUMPAD_WEST);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD1"), NUMPAD_SOUTHWEST);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD2"), NUMPAD_SOUTH);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD3"), NUMPAD_SOUTHEAST);
		this.getInputMap(IFW).put(KeyStroke.getKeyStroke("NUMPAD0"), NUMPAD_NOUGHT);
		
		this.getActionMap().put(SHIFT_DOWN, shiftDownAction);
		this.getActionMap().put(SHIFT_UP, shiftUpAction);
		this.getActionMap().put(PLAY, playAction);
		this.getActionMap().put(LIST, listAction);
		this.getActionMap().put(INITIALIZE, initAction);
		this.getActionMap().put(PLAYSTOP, transportAction);
		this.getActionMap().put(NEXT_INSTRUMENT_LEFT, instrumentLeft);
		this.getActionMap().put(NEXT_INSTRUMENT_RIGHT, instrumentRight);
		this.getActionMap().put(NUMPAD_NORTHWEST, numpadNorthWest);
		this.getActionMap().put(NUMPAD_NORTH, numpadNorth);
		this.getActionMap().put(NUMPAD_NORTHEAST, numpadNorthEast);
		this.getActionMap().put(NUMPAD_EAST, numpadEast);
		this.getActionMap().put(NUMPAD_WEST, numpadWest);
		this.getActionMap().put(NUMPAD_SOUTHWEST, numpadSouthWest);
		this.getActionMap().put(NUMPAD_SOUTH, numpadSouth);
		this.getActionMap().put(NUMPAD_SOUTHEAST, numpadSouthEast);
		this.getActionMap().put(NUMPAD_NOUGHT, numpadNought);
		this.getActionMap().put(ARROW_UP, upUndoList);
		this.getActionMap().put(ARROW_DOWN, downUndoList);
	}
	
	private JPanel makeButtonPanel(NodeManager nm, Color bg){
		JPanel panel = new JPanel();
		panel.setBackground(bg);
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		playButton = new PlayButton();
		playButton.sendStopTransportMessage();
		panel.add(playButton, gbc);
		
		ButtonGroup group = new ButtonGroup();
		int x = 1;
		int y = 0;
		for (NodeListGenerator nlg: nm.nlgList){
			gbc.gridx = x;
			gbc.gridy = y;
			NLGButton button = new NLGButton(this, nlg, bg);
			instrumentButtonList.add(button);
			group.add(button);
			panel.add(button, gbc);
			x++;
			if (x >= buttonWrapLength){
				x = 0;
				y++;
			}
		}
		
		return panel;
	}
	AbstractAction numpadNought = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (display.nlg != null){
				display.nlg.cueOff();
				display.updatePipelinePanel();
			}
		}		
	};
	AbstractAction upUndoList = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			nm.changeUndoCueIndex(1);		// +1 goes up list
			updateUndoList();
			nm.cueUpCurrentlyCuedUndoItem();
		}

			
	};	
	AbstractAction downUndoList = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			nm.changeUndoCueIndex(-1);		// -1 goes down list
			updateUndoList();
			nm.cueUpCurrentlyCuedUndoItem();
		}		
	};
	AbstractAction numpadSouthEast = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (display.nlg != null){
				display.nlg.moveCuedNode(OctantUtil.OctantUtil.SOUTH_EAST);
				display.updatePipelinePanel();
				dealWithOverlapsOnCuedNode();
			}
		}		
	};
	AbstractAction numpadSouthWest = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (display.nlg != null){
				display.nlg.moveCuedNode(OctantUtil.OctantUtil.SOUTH_WEST);
				display.updatePipelinePanel();
				dealWithOverlapsOnCuedNode();
			}
		}		
	};
	AbstractAction numpadSouth = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (display.nlg != null){
				display.nlg.moveCuedNode(OctantUtil.OctantUtil.SOUTH);
				display.updatePipelinePanel();
				dealWithOverlapsOnCuedNode();
				
			}
		}		
	};
	AbstractAction numpadWest = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (display.nlg != null){
				display.nlg.moveCuedNode(OctantUtil.OctantUtil.WEST);
				display.updatePipelinePanel();
				dealWithOverlapsOnCuedNode();
			}
		}		
	};
	AbstractAction numpadEast = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (display.nlg != null){
				display.nlg.moveCuedNode(OctantUtil.OctantUtil.EAST);
				display.updatePipelinePanel();
				dealWithOverlapsOnCuedNode();
			}
		}		
	};
	AbstractAction numpadNorthEast = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (display.nlg != null){
				display.nlg.moveCuedNode(OctantUtil.OctantUtil.NORTH_EAST);
				display.updatePipelinePanel();
				dealWithOverlapsOnCuedNode();
			}
		}		
	};
	AbstractAction numpadNorth = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (display.nlg != null){
				display.nlg.moveCuedNode(OctantUtil.OctantUtil.NORTH);
				display.updatePipelinePanel();
				dealWithOverlapsOnCuedNode();
			}
		}		
	};
	AbstractAction numpadNorthWest = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("NUMPAD_NORTHWEST");
			if (display.nlg != null){
				//System.out.println("display has nlg");
				display.nlg.moveCuedNode(OctantUtil.OctantUtil.NORTH_WEST);
				display.updatePipelinePanel();
				dealWithOverlapsOnCuedNode();
			}
		}		
	};
	AbstractAction instrumentLeft = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("instrument left");
			int index = getSelectedInstrument();
			if (index == 0){
				setInstrument(instrumentButtonList.size() - 1);
			} else {
				setInstrument(index - 1);
			}
			//dealWithOverlapsOnCuedNode();
		}


	};
	private int getSelectedInstrument() {
		for (int i = 0; i < instrumentButtonList.size(); i++){
			if (instrumentButtonList.get(i).isSelected()){
				return i;
			}
		}
		return 0;
	}		
	
	public void dealWithOverlapsOnCuedNode() {
		System.out.println("deal with cuedNode overlap call.....");
		if (!display.nlg.isExpanded){
			if (display.nlg.isThereOverlap()){
				double spAmount = display.nlg.moveOverlappingNodes();
				display.nlg.moveNonOverlapNodesOutTheWay(spAmount);
			} else {
				// no need to do anything
			}
		} else {
			if (display.nlg.cuedNodeIsInOverlapList() || display.nlg.cuedNode == display.nlg.centreNode){
				// do nothing
			} else {
				display.nlg.clearExpandedList();
				display.nlg.setNewNodeScreenPositions();
				display.getUIPanel().setTimerForOverlapTest(this);
			}
		}
//		if (display.nlg != null){
//			if (display.nlg.cuedNode != null){
//				if (!display.nlg.overlapsExpanded){
//					display.nlg.setNewNodeScreenPositionsAndCountdownToOverlapTest();
//					//display.dealWithNodeOverlap(display.nlg.cuedNode);
//				} else if (display.nlg.overlapList.contains(display.nlg.cuedNode) || display.nlg.cuedNode == display.nlg.centreNodeOfOverlap){
//				} else {
//					display.nlg.setNewNodeScreenPositionsAndCountdownToOverlapTest();
//					//display.dealWithNodeOverlap(display.nlg.cuedNode);
//				}
//			}
//		}
		
	}

	protected void setInstrument(int i) {
		instrumentButtonList.get(i).setSelected(true);
		this.displayNodeListGenerator(instrumentButtonList.get(i).getNodeListGenerator());
		updatePipelinePanel();
		updateUndoList();
	}
	AbstractAction instrumentRight = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("instrument right");
			int index = getSelectedInstrument();
			if (index == instrumentButtonList.size() - 1){
				setInstrument(0);
			} else {
				setInstrument(index + 1);
			}
			//dealWithOverlapsOnCuedNode();
		}		
	};
	AbstractAction transportAction = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			playButton.toggleTransport();
		}		
	};
	
	AbstractAction initAction = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			nm.initializeLiveClipObjects();
			nm.resetCueClips();
			for (NodeListGenerator nlg: nm.nlgList){
				nlg.cueOff();
			}
			nm.setCuedToPlay();
		}		
	};
	
	AbstractAction shiftDownAction = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			display.setUIShift(true);	
			//System.out.println("shiftDownAction");
		}		
	};
	AbstractAction shiftUpAction = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			display.setUIShift(false);	
			//System.out.println("shiftUpAction");
		}		
	};
	AbstractAction playAction = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			nm.setCuedToPlay();
			updateUndoList();
			if (display.nlg.xSortObject.i == LiveClip.SIMIL){
				display.nlg.setXSortIndex();
			}
			if (display.nlg.ySortObject.i == LiveClip.SIMIL){
				display.nlg.setYSortIndex();
			}
			//System.out.println("playAction");
		}		
	};
	AbstractAction listAction = new AbstractAction(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (display.nlg == null){
				System.out.println("no nlg currently displayed.....");
			} else {
				System.out.println(display.nlg.name + "==============================================================");
				for (Node node: display.nlg.nodeList){
					if (display.nlg.concurrentFilteredNodeList.contains(node)) System.out.print("***");
					System.out.println(node.singleLineToString());
				}
			}
			
		}		
	};
	
}
