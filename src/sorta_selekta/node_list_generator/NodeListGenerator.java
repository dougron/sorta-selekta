package sorta_selekta.node_list_generator;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;

import DataObjects.ableton_device_control_utils.DeviceParamInfo;
import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.combo_variables.IntAndString;
import PipelineUtils.Pipeline;
import PlugIns.FilterObject;
import PlugIns.Pluggable;
import ResourceUtils.AccentTemplate;
import ResourceUtils.ChordForm;
import ResourceUtils.ContourData;
import UDPUtils.OSCMessMaker;
import sorta_selekta.clip_injector.ClipInjectorObject;
import sorta_selekta.gui_objects.FilterRadioButton;
import sorta_selekta.gui_objects.NodeListDisplay;
import sorta_selekta.gui_objects.SortRadioButton;
import sorta_selekta.interaction.NewActionListener;
import sorta_selekta.interaction.NewMouseAdapter;
import sorta_selekta.node_list_generator.node.Node;
import sorta_selekta.option_maker.OptionMaker;



public class NodeListGenerator {
	
	
	public ArrayList<ChordForm> cfList = new ArrayList<ChordForm>();
	public ArrayList<AccentTemplate> atList = new ArrayList<AccentTemplate>();
	public ArrayList<ContourData> cdList = new ArrayList<ContourData>();
	public ArrayList<Pluggable> genList = new ArrayList<Pluggable>();
	public ArrayList<Pluggable> proList = new ArrayList<Pluggable>();
	public ArrayList<Pluggable> alwaysInList = new ArrayList<Pluggable>();
	public ArrayList<Pipeline> pipeList = new ArrayList<Pipeline>();
	public ArrayList<Node> nodeList = new ArrayList<Node>();
	private ArrayList<Node> filteredNodeList = new ArrayList<Node>();
	public ArrayList<Node> concurrentFilteredNodeList = new ArrayList<Node>();	// same as filteredNodeList but the only one publicly available to avoid concurrency error
	public int repeatCount = 0;
//	public ArrayList<String> filterList = new ArrayList<String>();
	public ArrayList<FilterObject> filterObjectList = new ArrayList<FilterObject>();
	public String name = "no name";
	public String idLetter = "";
	public ArrayList<FilterRadioButton> includeButtonList = new ArrayList<FilterRadioButton>();
	public ArrayList<FilterRadioButton> excludeButtonList = new ArrayList<FilterRadioButton>();
	public JLabel sizeLabel = new JLabel("----------");
	public IntAndString[] sortItemList = new IntAndString[]{};
	public SortRadioButton[] xSortButtonArr;
	public SortRadioButton[] ySortButtonArr;
//	public JToggleButton offClipButton;

	public IntAndString xSortObject = LiveClip.scoreNameObjectMap.get(0);
	public IntAndString ySortObject = LiveClip.scoreNameObjectMap.get(0);
	
	private static double xmax = 1;
	private static double ymax = 1;
	
	public Node mouseOveredNode;
	public Node playingNode;
	public Node cuedNode;
	public Node centreNode;
	
	public ArrayList<Node> undoNodeList = new ArrayList<Node>();
	private static final int OFF_NODE_INDEX = 0;
	private static final double DISTANCE_THAT_MEANS_OVERLAP = 0.02;
	
	
	public ClipInjectorObject playInject;
	public ClipInjectorObject cueInject;
//	public ClipInjectorObject fillInject;		// not planning on using this as yet
	public boolean isExpanded = false;
	public ArrayList<Node> overlapList = new ArrayList<Node>();
	public ArrayList<Node> nonOverlapList = new ArrayList<Node>();
	public Node centreNodeOfOverlap;
	public int countdown = -1;
	
	
	public NodeListGenerator(){
		
	}
	public NodeListGenerator(String name, String idLetter){
		this.name = name;
		this.idLetter = idLetter;
	}
	public void makeNodeList(){
		repeatCount = 0;
		nodeList.clear();
		makePipeList();
//		systemOutPipeList();		// for testing
		//filterList.clear();
		addNullNode();
		int nodeCount = 0;
		for (ChordForm cf: cfList){
			for (AccentTemplate at: atList){
				for (Pipeline p: pipeList){
//					System.out.println("\n\n\nnodeCount=" + nodeCount + " -----------------");
					if (cdList.size() > 0){
						for (ContourData cd: cdList){
							Node n = new Node(p, cf, at, cd, idLetter);
							nodeCount = doNode(n, nodeCount);
						}
					} else {
						Node n = new Node(p, cf, at, idLetter);
						nodeCount = doNode(n, nodeCount);
					}	
					
				}
			}
		}

		makeFilterList();
		makeFilterButtons();
		makeSortButtons();
//		makeOffButton();
	}
	public Node getNodeFromNumPadNavigation(int direction){
		//System.out.println("looking for direction=" + direction);
		if (cuedNode == null){
			return null;
		} else {
			Node newNode = null;
			double distance = 2.0;
			//System.out.println("cuedNode.x=" + cuedNode.screenx.value + " cuedNode.y=" + cuedNode.screeny.value);
			for (Node node: filteredNodeList){
				if (node != cuedNode){
					//System.out.println(node.ID + " node.x=" + node.screenx.value + " cuedNode.yscore=" + node.screeny.value);
					if (nodeIsInOctant(cuedNode, node, direction)){
						
						double nodeDistance = getNodeDistance(cuedNode, node);
						//System.out.println(node.ID + " in Octant distance=" + distance + " new distance=" + nodeDistance);
						if (nodeDistance < distance){
							distance = nodeDistance;
							newNode = node;
						}
					}
				}
				
			}
			return newNode;
		}
	}
	public void moveCuedNode(int direction){
		Node node = getNodeFromNumPadNavigation(direction);
		//System.out.println(node.toString());
		if (node != null){
			setThisNodeAsCued(node);
		}
	}
	public void setThisNodeAsCued(Node node){
		cuedNode.isCued = false;
		cuedNode = node;
		cuedNode.isCued = true;
		cueInject.sendNode(cuedNode);
	}
	public void addCuedNodeToFilteredNodeListIfNeccesary(){
		if (!filteredNodeList.contains(cuedNode)){
			makeFilteredNodeList();
		}
	}
	private double getNodeDistance(Node rootNode, Node node) {
		return Math.sqrt(Math.pow(node.screenx.value - rootNode.screenx.value, 2) + Math.pow(node.screeny.value - rootNode.screeny.value, 2));

	}
	private boolean nodeIsInOctant(Node rootNode, Node node, int direction) {
		int dir = OctantUtil.OctantUtil.getOctant(rootNode.screenx.value, rootNode.screeny.value, node.screenx.value, node.screeny.value);
		//System.out.println("direction=" + dir);
		if (dir == direction){
			return true;
		}
		return false;
	}
	private void addNullNode() {
		Node n = new Node();
		n.nodeColor = new Color(100, 100, 100, 100);
		n.nodeSize = 80;
		n.isOffNode = true;
		nodeList.add(n);
//		playingNode = n;
		cuedNode = n;
		n.isPlaying = true;
//		n.isCued = true;
	}
	//	private void makeOffButton() {
//		offClipButton = new JToggleButton("sendOffClip");
//		offClipButton.addActionListener(offClipButtonAction());
//		
//	}
	private void systemOutPipeList() {
		System.out.println("NodeListGenerator.systemOutPipeList:");
		for (Pipeline p: pipeList){
			System.out.println(p.plugNameOneLineToString());
		}
		
	}
	private void makeFilterList(){
		for (ChordForm cf: cfList){
			FilterObject fo = new FilterObject(cf.minusChordsToString(), cf);
			filterObjectList.add(fo);
		}
		for (AccentTemplate at: atList){
			FilterObject fo = new FilterObject(at.name, at);
			filterObjectList.add(fo);
		}
		for (ContourData cd: cdList){
			FilterObject fo = new FilterObject(cd.name, cd);
			filterObjectList.add(fo);
		}
		for (Pluggable ppi: genList){
			FilterObject fo = new FilterObject(ppi.originalName(), ppi);
			filterObjectList.add(fo);
		}
		for (Pluggable ppi: proList){
			FilterObject fo = new FilterObject(ppi.originalName(), ppi);
			filterObjectList.add(fo);
		}
		
		
	}
	private int doNode(Node n, int nodeCount){
		if (!isRepeated(n)){
			
			n.setNumber(nodeCount);
			nodeCount++;
			nodeList.add(n);
	//		System.out.println("nodeCount=" + (nodeCount - 1) + " -----end-----------");
	//		for (String str: n.filterList){
	//			addFilter(str);
	//		}
	//		for (Object obj: n.filterObjectList){
	//			addFilterObject(obj);
	//		}
		}
		return nodeCount;
	}
	public void makePipeList(){
		pipeList.clear();
		ArrayList<ArrayList<Integer>> proOptions = OptionMaker.getOptionList(proList.size());
		//System.out.println(proOptions.size() + " items in proOptions");
		for (Pluggable ppi: genList){
			pipeList.add(makePipeline(ppi));
			for (ArrayList<Integer> indexList: proOptions){
				pipeList.add(makePipeline(ppi, indexList));
			}
		}
 	}
	
	public String nodeListToString(){
		String ret = "nodeList:------" + nodeList.size() + " items-------\n";
		ret += repeatCount + " repeats found during generation\n";
		ret += "nodeList filterList: ";
		for (FilterObject fo: filterObjectList){
			ret += fo.name + ", ";
		}
		ret += "\n";
		for (Node node: nodeList){
			ret += "\n" + node.toString() + "\n";
		}
//		ret += nodeList.size() + " items in nodeList";
		return ret;
	}
	public String shortNodeListToString() {
		String ret = "nodeList:------" + nodeList.size() + " items-------\n";
		ret += repeatCount + " repeats found during generation\n";
		ret += "nodeList filterList: ";
		for (FilterObject fo: filterObjectList){
			ret += fo.name + ", ";
		}
		ret += "\n";
		for (Node node: nodeList){
			ret += node.singleLineToString() + "\n";
		}
		return ret;
	}
	
	public String pipeListToString(){
		String ret = "pipeList:----------------------------\n";
		for (Pipeline p: pipeList){
			ret += p.toString();
		}
		return ret;
	}
	public String cfListToString(){
		String ret = "cfList:----------------------------\n";
		for (ChordForm cf: cfList){
			ret += cf.toString();
			ret += "\n" + cf.slashChordsToString();
		}
		return ret;
	}
	public String atListToString(){
		String ret = "atList:----------------------------\n";
		for (AccentTemplate at: atList){
			ret += at.lengthsToString() + "\n";
		}
		
		return ret;
	}
	public String buttonListToString(){
		String ret = "buttonLists:-\nincludeList\n";
		ret += makeButtonListToString(includeButtonList);
		ret += "excludeList\n";
		ret += makeButtonListToString(excludeButtonList);
		return ret;
	}
	public int makeFilteredNodeList() {
		//System.out.println("update filtered list: filteredNodeList.size()=" + filteredNodeList.size());
		filteredNodeList = new ArrayList<Node>();
		boolean cuedAdded = false;
		boolean playingAdded = false;
		boolean flag = false;
		for (Node node: nodeList){
			flag = false;
			if (node.isOffNode){
				flag = true;
			} else {
				for (FilterRadioButton frb: includeButtonList){
					if (frb.isSelected()){
						if (nodeContainsFilterObject(node, frb.getFilterObject())){
							flag = true;
						}
					}
				}
				for (FilterRadioButton frb: excludeButtonList){
					if (frb.isSelected()){
						if (nodeContainsFilterObject(node, frb.getFilterObject())){
							flag = false;
						}
					}
				}
			}			
			if (flag){
				filteredNodeList.add(node);
				if (cuedNode != null && node == cuedNode) cuedAdded = true;
				if (playingNode != null && node == playingNode) playingAdded = true;
			}
		}
		if (cuedNode != null && !cuedAdded) filteredNodeList.add(cuedNode);
		if (playingNode != null && !playingAdded) filteredNodeList.add(playingNode);
		//System.out.println("new filteredNodeList.size()=" + filteredNodeList.size());
		concurrentFilteredNodeList = filteredNodeList;
		return filteredNodeList.size();
	}
	public void setSizeLabel(){
		sizeLabel.setText("filt= " + filteredNodeList.size() + "items");
	}
	public void setNodePositions(){
		setXMax();
		setYMax();
		setNewNodeScreenPositions();
	}
//	public void consoleOutNodeList(){
//		// when this is decommissioned, Node.getScoreValue(int switchIndex) can be made private
//		System.out.println("---------------------------------");
//		for (Node node: filteredNodeList){
//			System.out.println(node.ID + " x=" + node.getScoreValue(xSortObject.i) + " y=" + node.getScoreValue(ySortObject.i) 
//			+ " screenx: " + node.screenx.oneLineToString() + " screeny: " + node.screeny.oneLineToString());;
//		}
//	}
	public void setCuedToPlay(){
		if (cuedNode != null){
			//System.out.println(name + " has CuedNode.");
			if (playingNode != null){
				undoNodeList.add(playingNode);
				playingNode.isPlaying = false;
			}
			playingNode = cuedNode;
			playingNode.isPlaying = true;
			playInject.sendNode(playingNode);
		}
	}
	public void sendLiveClipObjectInitializationMessages(){
		ClipInjectorObject.conn.sendUDPMessage(liveClipObjectInitializationMessage(playInject));
		ClipInjectorObject.conn.sendUDPMessage(liveClipObjectInitializationMessage(cueInject));
	}
	public void sendLiveControllerInitializationMessage(){
		playInject.sendControllerInitializationMessages();
		cueInject.sendControllerInitializationMessages();
	}
	public void clearSelectedNodes(){
		if (cuedNode != null) cuedNode.isCued = false;
		cuedNode = null;
		if(playingNode != null) playingNode.isPlaying = false;
		playingNode = null;
	}
	public void cueOff() {
		if (cuedNode != null) cuedNode.isCued = false;		
		cuedNode = nodeList.get(OFF_NODE_INDEX);
		cuedNode.isCued = true;
		cueInject.sendNode(cuedNode);
	}

// privates -------------------------------------------------------------------
	
	private OSCMessMaker liveClipObjectInitializationMessage(ClipInjectorObject cio) {
		OSCMessMaker mm = new OSCMessMaker();
		mm.addItem(DeviceParamInfo.initString);
		cio.instrumentInitializationMessage(mm);
//		System.out.println("NodeListGenerator.liveClipObjectInitializationMessage-------");
//		System.out.println(mm.toString());
		return mm;
	}
	private void makeSortButtons(){
		ButtonGroup xgroup = new ButtonGroup();
		ButtonGroup ygroup = new ButtonGroup();
		xSortButtonArr = new SortRadioButton[sortItemList.length];
		ySortButtonArr = new SortRadioButton[sortItemList.length];
		for (int i =0; i < sortItemList.length; i++){
			SortRadioButton srbx = new SortRadioButton(sortItemList[i]);
			srbx.setBackground(NodeListDisplay.xSortPanelBGColor);
			srbx.addActionListener(xSortButtonAction());
			xSortButtonArr[i] = srbx;
			xgroup.add(srbx);
			SortRadioButton srby = new SortRadioButton(sortItemList[i]);
			srby.setBackground(NodeListDisplay.ySortPanelBGColor);
			srby.addActionListener(ySortButtonAction());
			ySortButtonArr[i] = srby;
			ygroup.add(srby);
		}
		if (sortItemList.length > 0){
			xSortButtonArr[0].setSelected(true);
			ySortButtonArr[0].setSelected(true);
		}
		if (sortItemList.length > 1){
			xSortButtonArr[1].setSelected(true);
		}
		//setNodePositions();
		//setXSortIndex();
		//setYSortIndex();
	}
//	private ActionListener xSortActionListener(){
//		return new ActionListener()
//	}
//	private ActionListener offClipButtonAction() {		
//		return new ActionListener(){
//			public void actionPerformed(ActionEvent ae){
//					if (offClipButton.isSelected()){
//						if (cuedNode != null){
//							cuedNode.isCued = false;
//						}
//						cuedNode = Node.nullNode;
//					} else {
//						if (playingNode != null){
//							cuedNode = playingNode;
//							cuedNode.isCued = true;
//						} else {
	//						cuedNode = null;
//						}
//					}
//			}
//		};
//	}
	private ActionListener xSortButtonAction() {		
		return new ActionListener(){
			public void actionPerformed(ActionEvent ae){
					setXSortIndex();
					//consoleOutNodeList();
			}
		};
	}
	private ActionListener ySortButtonAction() {		
		return new ActionListener(){
			public void actionPerformed(ActionEvent ae){
					setYSortIndex();
					//consoleOutNodeList();
			}
		};
	}
	public void setXSortIndex(){
		for (SortRadioButton srb: xSortButtonArr){
			if (srb.isSelected()){
				xSortObject = srb.sortItem;
				if (xSortObject.i == LiveClip.SIMIL){
					Node testNode;
					for (Node node: nodeList){
						if (playingNode != null){
							testNode = playingNode;
						} else {
							testNode = new Node();
						}
						node.setTempSimilScore(testNode);
					}
				}
				setXMax();
				setNewNodeScreenPositions();
				break;
			}
		}
	}
	private void setXMax(){
		xmax = 0;
		double temp = 0;
		for (Node node: nodeList){
			temp = node.getXScore(xSortObject.i);
			if (temp > xmax){
				xmax = temp;
			}
		}
		if (xmax < 1) xmax = 1;
	}
	public void setYSortIndex(){
		for (SortRadioButton srb: ySortButtonArr){
			if (srb.isSelected()){
				ySortObject = srb.sortItem;
				if (xSortObject.i == LiveClip.SIMIL){
					Node testNode;
					for (Node node: nodeList){
						if (playingNode != null){
							testNode = playingNode;
						} else {
							testNode = new Node();
						}
						node.setTempSimilScore(testNode);
					}
				}
				setYMax();
				setNewNodeScreenPositions();
				break;
			}
		}
	}
	private void setYMax(){
		ymax = 0;
		double temp = 0;
		for (Node node: nodeList){
			temp = node.getYScore(ySortObject.i);
			if (temp > ymax){
				ymax = temp;
			}
		}
		if (ymax < 1) ymax = 1;
	}
	public void setNewNodeScreenPositions(){
		// needs to be called after calling setXMax() and setYMax()
		for (Node node: nodeList){
			node.screenx.moveTo(node.xscore / xmax);
			node.screeny.moveTo(node.yscore / ymax);
		}
	}
//	public void setNewNodeScreenPositionsAndCountdownToOverlapTest(){
//		setNewNodeScreenPositions();
//		countdown = COUNTDOWN_TO_OVERLAP_CHECK;
//	}
 	private String makeButtonListToString(ArrayList<FilterRadioButton> buttList) {
		String ret = "";
		for (FilterRadioButton frb: buttList){
			ret += "    " + frb.getFilterObject().name + "-" + frb.isSelected() + "\n";
		}
		return ret;
	}
	private void makeFilterButtons(){
		includeButtonList.clear();
		excludeButtonList.clear();
		boolean setDefault = true;
		for (FilterObject fo: filterObjectList){
			//System.out.println("makeFilterButtons(): " + fo.toString());
			ButtonGroup group = new ButtonGroup();
			FilterRadioButton inc = getFilterButton(fo);
			if (setDefault){
				inc.setSelected(true);
//				setDefault = false;							// sets 1st include item to be true to avoid blank NodeUIPanels on startup
															// this dores not seem to work as of July 2018, so I commented out the above line
			}
			FilterRadioButton exc = getFilterButton(fo);
			inc.setBackground(NodeListDisplay.includePanelBGColor);
			exc.setBackground(NodeListDisplay.excludePanelBGColor);
			group.add(inc);
			inc.setGroup(group);
			group.add(exc);
			exc.setGroup(group);
			includeButtonList.add(inc);
			excludeButtonList.add(exc);
		}
	}
	private FilterRadioButton getFilterButton(FilterObject fo){
		FilterRadioButton button = new FilterRadioButton(fo);
		NewActionListener l = new NewActionListener(button){
			public void actionPerformed(ActionEvent ae){
				//System.out.println("ActionListener: " + button.isSelected());
				if (getButton().isWhenEnteredWas()){
					//System.out.println("when entered was true");
					getButton().getGroup().clearSelection();
				} 
				getButton().setWhenEntered();
				makeFilteredNodeList();
				setSizeLabel();
			}
		};

		NewMouseAdapter m = new NewMouseAdapter(button){
			public void mouseEntered(MouseEvent me){
				//System.out.println("Mouse entered " + button.isSelected());
				getButton().setWhenEntered();
			}
		};
		button.addActionListener(l);
		button.addMouseListener(m);

		return button;
	}

 
	private boolean nodeContainsFilterObject(Node node, FilterObject filterObject) {
		for (FilterObject fo: node.filterObjectList){
			//FilterSortInterface fsi1 = fo.object.getFilteSortObject();
			//FilterSortInterface fsi2 = filterObject.object.getFilteSortObject();
			if (fo.object.getFilteSortObject() == filterObject.object.getFilteSortObject()){
				return true;
			}
		}
		return false;
	}
	private boolean isRepeated(Node newNode){
		for (Node n: nodeList){
			if (n.pnl.isSameAsIncludingVelocity(newNode.pnl)){
				repeatCount++;
				return true;
			}
		}
		return false;
	}
	private Pipeline makePipeline(Pluggable gen){
		Pipeline p = new Pipeline();
		p.addPlugInOption(gen);
		for (Pluggable plug: alwaysInList){
			p.addPlugInOption(plug);
		}
		for (int i = 0; i < p.plugOptionList.size(); i++){
			p.addPlugIn(i);
		}
		
		return p;
	}
	private Pipeline makePipeline(Pluggable gen, ArrayList<Integer> proIndexList){
		Pipeline p = new Pipeline();
		p.addPlugInOption(gen);
		for (Pluggable plug: alwaysInList){
			p.addPlugInOption(plug);
		}
		for (int index: proIndexList){
			p.addPlugInOption(proList.get(index));
		}
		for (int i = 0; i < p.plugOptionList.size(); i++){
			p.addPlugIn(i);
		}
		return p;
	}
	public boolean isThereOverlap() {
		// makes new overlapList and nonOverlapList, sets isExpanded
		centreNodeOfOverlap = cuedNode;
		overlapList.clear();
		nonOverlapList.clear();
		for (Node node: filteredNodeList){
			if (node != cuedNode){
				if (getNodeDistance(cuedNode, node) < DISTANCE_THAT_MEANS_OVERLAP){
					overlapList.add(node);
					node.sortForExpansion = Node.SORT_TO_TOP;
				} else {
					nonOverlapList.add(node);
					node.sortForExpansion = Node.SORT_TO_BOTTOM;
				}
			} else {
				node.sortForExpansion = Node.SORT_TO_TOP;
			}
		}
		Collections.sort(filteredNodeList, Node.sortExpandComparator);
		if (overlapList.size() > 0) isExpanded = true; else isExpanded = false;
 		return isExpanded;
	}
	public double moveOverlappingNodes() {
		double spAmount = splooshFactor;
		int spVectorIndex = 0;
		double posx; 
		double posy; 
		double tempx;
		double tempy;
		boolean loop;
		centreNode = cuedNode;
		//System.out.println("---------------------------");
		for (Node node: overlapList){
			//System.out.println(node.ID);
			posx = node.screenx.next();
			posy = node.screeny.next();
			loop = true;
			while (loop){
				double[] vect = splooshVector[spVectorIndex];
				tempx = posx + vect[0] * spAmount;
				tempy = posy + vect[1] * spAmount;
				if (tempx <= 1.0 && tempx >= 0 && tempy <= 1.0 && tempy >= 0){
					loop = false;
					node.screenx.moveTo(tempx);
					node.screeny.moveTo(tempy);
				}
				spVectorIndex++;
				if (spVectorIndex >= splooshVector.length){
					spVectorIndex = 0;
					spAmount += splooshFactor;
				}
				
			}
			
		} 
		return spAmount;
	}
	public void moveNonOverlapNodesOutTheWay(double spAmount) {
		if (overlapList.size() > 0){
			for (Node node: nonOverlapList){
				makeNewPositionToAvoidSploosh(node, spAmount, cuedNode);
			}
		}
		
		
	}
	private void makeNewPositionToAvoidSploosh(Node node, double spAmount, Node centreNode) {
		double centreX = centreNode.screenx.next();
		double currentX = node.screenx.next();
		node.screenx.moveTo(getNewRetractedPos(centreX, currentX, spAmount));
		double centreY = centreNode.screeny.next();
		double currentY = node.screeny.next();
		node.screeny.moveTo(getNewRetractedPos(centreY, currentY, spAmount));
		
	}
	private double getNewRetractedPos(double centrePos, double currentPos, double spAmount){
		double newPos;
		if (currentPos < centrePos){
			newPos = currentPos * ((centrePos - spAmount) / centrePos);
		} else {
			double xa1 = 1 - currentPos;
			double xa2 = 1 - centrePos;
			newPos = 1 - (xa1 * (xa2 - spAmount) / xa2);
		}
		return newPos;
	}
	private static final double splooshFactor = 0.07;
    private static final double[][] splooshVector = new double[][]{
    	new double[]{1.0, 0.0},
    	new double[]{0.71, 0.71},
    	new double[]{0.0, 1.0},
    	new double[]{-0.71, 0.71},
    	new double[]{-1.0, 0.0},
    	new double[]{-0.71, -0.71},
    	new double[]{0.0, -1.0},
    	new double[]{0.71, -0.71},
    	
    };


	public boolean cuedNodeIsInOverlapList() {
		
		return overlapList.contains(cuedNode);
	}
	public void clearExpandedList() {
		overlapList.clear();
		nonOverlapList.clear();
		isExpanded = false;
		centreNode = null;
	}


	
}
