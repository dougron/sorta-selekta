package sorta_selekta.gui_objects;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import sorta_selekta.node_list_generator.NodeListGenerator;
import sorta_selekta.node_list_generator.node.Node;

public class NodeUIPanel extends JPanel implements 
MouseListener, 
MouseMotionListener, 
//MouseWheelListener, 
Runnable{

	private final int DELAY = 25;
	private Thread animator;
	private Graphics2D g2d;
	private Color bgColor = new Color(200, 220, 220);
	private Color bgShiftColor = new Color(150, 150, 100);
//	private ArrayList<Node> finalNodeList = new ArrayList<Node>();
	private NodeListGenerator nlg;
//	private Node currentMouseOveredNode;
	private boolean shift = false;
	private NodeListDisplay parent;
	private NodeManagerPanel nmp;
	
	
	public NodeUIPanel(){
		// only to avoid error in NodeUIPanelTestFrame
	}
	public NodeUIPanel(NodeListDisplay disp){
//		udpReceiveThread = new Thread(new ReceiveSocketThread(udpRecList));
//		udpReceiveThread.start();
		parent = disp;
		addMouseListener(this);
		addMouseMotionListener(this);
//		addMouseWheelListener(this);

		setPreferredSize(new Dimension(500, 500));
		setOpaque(true);
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		doDrawing(g);
	}
	public void setNodeListGenerator(NodeListGenerator nlg){
		this.nlg = nlg;
		//System.out.println("NodeUIPanel: nlg set to " + nlg.name);
	}
	public void setShift(boolean b){
		shift = b;
	}
	
// Runnable methods -------------------------------------------------------------
	@Override
	public void run() {
		long beforeTime, timeDiff, sleep;
		beforeTime = System.currentTimeMillis();


		while (true) {		
		    cycle();
		    repaint();
		    timeDiff = System.currentTimeMillis() - beforeTime;
		    sleep = DELAY - timeDiff;
		
		    if (sleep < 0) {
		        sleep = 2;
		    }
		
		    try {
		        Thread.sleep(sleep);
		    } catch (InterruptedException e) {
		        System.out.println("Interrupted: " + e.getMessage());
		    }
		
		    beforeTime = System.currentTimeMillis();
		}
	}
// this could possibly be a JPanel thing after all.............
	@Override
	public void addNotify() {
		super.addNotify();
		animator = new Thread(this);
		animator.start();
	}
// MouseListener methods and dependants-----------------------------------------------------------
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
//		System.out.println("NodeUIPanel.mouseMoved x=" + x + " y=" + y);
		testMouseOver(x, y);
		//systemOutNodes();
	}
	private void systemOutNodes(){
		// for testing, can be erased if irrelevant..................
		if (nlg == null){
			System.out.println("no nlg present");
		} else {
			System.out.println("--------------------------");
			System.out.print("mouseOveredNode: ");
			if (nlg.mouseOveredNode == null){
				System.out.println("null");
			} else {
				System.out.println(nlg.mouseOveredNode.ID);
			}
			System.out.print("cuedNode: ");
			if (nlg.cuedNode == null){
				System.out.println("null");
			} else {
				System.out.println(nlg.cuedNode.ID);
			}
			System.out.print("playingNode: ");
			if (nlg.playingNode == null){
				System.out.println("null");
			} else {
				System.out.println(nlg.playingNode.ID);
			}
		}	
	}
	private void testMouseOver(int x, int y){
		if (nlg != null){
			boolean wasFound = false;
			for (Node node: nlg.concurrentFilteredNodeList){
				if (node.isMouseOvered(x, y)){
					node.isMouseOvered = true;

					if (nlg.mouseOveredNode != null && nlg.mouseOveredNode != node){
						nlg.mouseOveredNode.isMouseOvered = false;
					}	
					nlg.mouseOveredNode = node;
					wasFound = true;
					//dealWithOverlappingNodes(node); //##################
					
					//nlg.mouseOveredNode.screenx.moveTo(0.5);  // testing
					break;
				}
			}
			if (!wasFound && nlg.mouseOveredNode != null){
				nlg.mouseOveredNode.isMouseOvered = false;
				nlg.mouseOveredNode = null;
				nlg.setNodePositions();
			}
		}		
	}
	

// ************** the retracting non splooshed nodes -----------------------------------
		

// MouseListener methods -------------------------------------------------------	

	
	
	@Override
	public void mouseClicked(MouseEvent e) {
//		System.out.println("clicked.....");    
	}   
    @Override
	public void mouseEntered(MouseEvent e) {
//		System.out.println("mouseEntered.....");
	}
	@Override
	public void mouseExited(MouseEvent e) {
		if (nlg != null){
			if (nlg.mouseOveredNode != null) nlg.mouseOveredNode.isMouseOvered = false;
		}		
//		System.out.println("mouseExited.....");	   
	}
	@Override
	public void mousePressed(MouseEvent e) {
//		System.out.println("mousePressed.....");	
		if (nlg != null){
			if (nlg.mouseOveredNode != null){
				if (nlg.cuedNode == null){
					nlg.cuedNode = nlg.mouseOveredNode;
					nlg.cuedNode.isCued = true;
//					nlg.offClipButton.setSelected(false);
//					nlg.offClipButton.updateUI();
					sendCuedNodeClip();
				} else {
					if (nlg.mouseOveredNode == nlg.cuedNode){
						nlg.cuedNode.isCued = false;
						sendPlayingNodeToCue();
						nlg.cuedNode = null;
					} else {
						nlg.cuedNode.isCued = false;
						nlg.cuedNode = nlg.mouseOveredNode;
						nlg.cuedNode.isCued = true;
						sendCuedNodeClip();
					}
				}
				
			}
			parent.updatePipelinePanel();
		}		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
//		System.out.println("mouseReleased.....");
	}
	public void setTimerForOverlapTest(NodeManagerPanel nmp){
		this.nmp = nmp;
		nlg.countdown = COUNTDOWN_TO_OVERLAP_CHECK;
	}
	
// run() method dependants --------------------------------------------
	private void sendPlayingNodeToCue() {
		if (nlg != null){
			nlg.cueInject.sendNode(nlg.playingNode);
		}
		
	}
	private void sendCuedNodeClip(){
		if (nlg != null){
			nlg.cueInject.sendNode(nlg.cuedNode);
		}
	}
    private void cycle(){
    	
    	if (nlg != null){
    		//System.out.println(nlg.countdown);
    		if (nlg.cuedNode != null){
        		if (nlg.countdown == 0){
        			System.out.println("countdown = 0!!!!!!!!!!!!!!!!!!!!!");
            		if (nmp != null){
            			nmp.dealWithOverlapsOnCuedNode();
            		} 
            		nlg.countdown--;
            	} else if (nlg.countdown < 0){
            		
            	} else {
            		nlg.countdown--;
            	}
        	}
    	}
    	
    	
    	// for testing
//    	for (Node node: finalNodeList){
 //   		node.moveObject();
//    	}
//    	testWASD();
//    	dealWithPlobjToLoadList();
//   	dealWithUDPRecList();
//    	doWireSwitching();
//    	testForReRender();
//		testInstrumentsForStopPlay();
//    	sortGobjList();
    }

 // drawing stuff ------------------------------------------------------------
    private void doDrawing(Graphics g){
		g2d = (Graphics2D) g;
		setAntiAliasing();
		if (shift){
			setBackground(bgShiftColor);
		} else {
			setBackground(bgColor);
		}		
		Dimension d = getSize();
		addNodesToGUI(g2d, d.getWidth(), d.getHeight());
    }
    private void addNodesToGUI(Graphics2D g2d, double xsize, double ysize){
    	if (nlg != null){
    		for (Node n: nlg.concurrentFilteredNodeList){
        		n.addGUIObject(g2d, xsize, ysize);
        	}
    	}   	
    }
    private void setAntiAliasing(){
    	g2d.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private static final int COUNTDOWN_TO_OVERLAP_CHECK = 50;

}
