package sorta_selekta.node_list_generator.node;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Comparator;

import DLEditDistance.DLEditDistance;
import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import PipelineUtils.Pipeline;
import PipelineUtils.PipelineNoteList;
import PipelineUtils.PipelineNoteObject;
import PipelineUtils.PlayPlugArgument;
import PlugIns.FilterObject;
import PlugIns.Pluggable;
import ResourceUtils.AccentTemplate;
import ResourceUtils.ChordForm;
import ResourceUtils.ContourData;
import ResourceUtils.RandomNumberSequence;

public class Node {

	public Pipeline pipe;
	public PlayPlugArgument ppa;
	public PipelineNoteList pnl;
	public LiveClip clip;
	//public ChordScaleDictionary csd = new ChordScaleDictionary();
//	public ArrayList<String> filterList = new ArrayList<String>();
	public ArrayList<FilterObject> filterObjectList = new ArrayList<FilterObject>();
	public int number = 0;
	public String idLetter = "";
	public String ID;
	
	public double xscore;		// position of node based on NodeListGenerator sort criteria
	public double yscore;		// range is 0.0 - 1.0 and may need to be calculated after finding the max for that score in the entire list
	public NodeMover screenx = new NodeMover(0);	// actual position of Node to cater for movement etc
	public NodeMover screeny = new NodeMover(0);
	public double incx = 0.01;
	public double incy = 0.01;
	
	private Color textColor = new Color(0, 0, 0);
	public Color nodeColor = new Color(0, 100, 175, 150);
	private Color mouseOverRingColor = new Color(0, 100, 0, 150);
	private Color cuedRingColor = new Color(100, 100, 0, 150);
	private Color playingRingColor = new Color(100, 0, 0, 150);
	public double nodeSize = 40;
	public String textName = "Tahoma";
	public int textStyle = Font.BOLD;
	public int fontSize = 8;
	private static RandomNumberSequence rnd = new RandomNumberSequence(127, 2);	// for testing
	
	public boolean isMouseOvered = false;
	public boolean isPlaying = false;
	public boolean isCued = false;
	Ellipse2D.Double circle;
	
	public double tempSimilScore = 0.0;
	private static DLEditDistance dled = new DLEditDistance();
	public static Node nullNode = new Node();
	public boolean isOffNode = false;
	
	public int sortForExpansion = 0;
	
//	private static double xmax = 1;
//	private static double ymax = 1;
	
	public Node (){
		// nullNode for SIMIL calculations also for OFF clip
		clip = new LiveClip(0, 0);
		pnl = new PipelineNoteList(0);
		pipe = new Pipeline();
		ID = "OFF";
		clip.name = ID;
	}
	
	public Node(Pipeline pipe, ChordForm cf, AccentTemplate at, String idLetter){
		ppa = new PlayPlugArgument();
		ppa.at = at;
		ppa.cf = cf;
		ppa.rnd = new RandomNumberSequence(32, 0);
//		setRandomTestScreenPositions(rnd);
		this.pipe = pipe;
		this.idLetter = idLetter;
		makeFilterObjectList();
		reRenderAndGetClip();
//		pnl = pipe.makeNoteList(ppa);
//		clip = pnl.makeLiveClip();
	}
	public Node(Pipeline pipe, ChordForm cf, AccentTemplate at, ContourData cd, String idLetter){
		ppa = new PlayPlugArgument();
		ppa.at = at;
		ppa.cf = cf;
		ppa.cd = cd;
		ppa.rnd = new RandomNumberSequence(32, 0);
//		setRandomTestScreenPositions(rnd);
		this.pipe = pipe;
		this.idLetter = idLetter;
		makeFilterObjectList();
		reRenderAndGetClip();
//		pnl = pipe.makeNoteList(ppa);
//		clip = pnl.makeLiveClip();
	}
//	private void setRandomTestScreenPositions(RandomNumberSequence rnd) {
//		screenx.moveTo(rnd.next());
//		screeny.moveTo(rnd.next());
//	}
	public LiveClip reRenderAndGetClip(){
		pnl = pipe.makeNoteList(ppa);
		clip = pnl.makeLiveClip();
		return clip;
	}
	public String toString(){
		String ret = "Node:----" + ID + "\n";
		if (pipe != null){
			ret += pipe.toString();
		} else {
			ret += "no Pipeline\n";
		}
		if (ppa != null){
			ret += ppa.toString();
		} else {
			ret += "no PlayPlugArgument\n";
		}
		if (pnl != null){
			ret += pnl.posListToString();
		} else {
			ret += "no PipelineNoteList";
		}
		if (clip != null){
			ret += clip.toString() + "\n";
		} else {
			ret += "no LiveClip\n";
		}
		ret += "filterList: ";
		for (FilterObject fo: filterObjectList){
			ret += fo.name + ",";
		}
		ret += "\nscreenpos=" + screenx.oneLineToString() + "," + screeny.oneLineToString();
		return ret;
	}
	public String filterObjectListToString(){
		String ret = "filterList: ";
		for (FilterObject fo: filterObjectList){
			ret += fo.name + ",";
		}
		return ret;
	}
 	public String singleLineToString(){
		String ret = ID + ": ";
		if (pipe != null){
			for (Pluggable p: pipe.plugList){
				ret += p.originalName() + ",";
			}
		}
		if (pnl != null){
			ret += " - pnl=" + pnl.pnoList.size() + " items";
		}
		if (clip != null){
			ret += " - clip=" + clip.noteList.size() + " items";
		}
		ret += " xscore=" + xscore + " yscore=" + yscore;
		return ret;
	}
	public void setNumber(int i){
		number = i;
		makeID();
		clip.name = ID;
	}
	public void addGUIObject(Graphics2D g2d, double xsize, double ysize){
		//double xpos = screenx.next();
		//double ypos = screeny.next();
		double xpos = screenx.next() * xsize - nodeSize / 2;
		double ypos = (1 - screeny.next()) * ysize - nodeSize / 2;
		addNodeToGUI(g2d, xsize, ysize, xpos, ypos);
		addTextToGUI(g2d, xsize, ysize, xpos, ypos);
		addRing(g2d, xsize, ysize, xpos, ypos);
	}

	//	public void moveObject(){
//		screenx += incx;
//		screeny += incy;
//		if (screenx < 0.0 || screenx > 1.0){
//			incx *= -1;
//			screenx += incx;
//			incx = rnd.next() * Math.signum(incx) * 0.01;;
//		}
///		if (screeny < 0.0 || screeny > 1.0){
//			incy *= -1;
//			screeny += incy;
//			incy = rnd.next() * Math.signum(incy) * 0.01;
//		}
//	}
//	public void setXMax(IntAndString sortItem){
//		double x = getScoreValue(sortItem.i);
//		
//	}
	public double getXScore(int sortIndex){
		double d = getScoreValue(sortIndex);
		xscore = d;
		return d;
	}
	public double getYScore(int sortIndex){
		double d = getScoreValue(sortIndex);
		yscore = d;
		return d;
	}
	public boolean isMouseOvered(int x, int y){
		if (circle.contains(x, y)){
			//System.out.println(ID + " Node.isMouseOvered=true");
			return true;
		} else {
			return false;
		}
	}
	public void setTempSimilScore(Node nodeSimil){
		tempSimilScore = dled.getShortestEditDistance(clip.dlDistanceLengthString(), nodeSimil.clip.dlDistanceLengthString(), DLEditDistance.SEPARATOR);
	}
	
// privates ---------------------------------------------------------------------------
	
	private double getScoreValue(int switchIndex){
		switch(switchIndex){
		case LiveClip.SYNC:
			return clip.syncopationScore();
		case LiveClip.RDENS:
			return clip.rhythmicDensity();
		case LiveClip.REGUL:
			return clip.rhythmicRegularity();
		case LiveClip.SIMIL:
			return tempSimilScore;
		case LiveClip.EVEN_4_DURATION:
			return clip.evenQuarterDurationScore();
		case LiveClip.EVEN_8_DURATION:
			return clip.evenEighthsDurationScore();
		case LiveClip.EVEN_16_DURATION:
			return clip.evenSixteenthsDurationScore();
		case LiveClip.ODD_4_DURATION:
			return clip.oddQuarterDurationScore();
		case LiveClip.ODD_8_DURATION:
			return clip.oddEighthsDurationScore();
		case LiveClip.ODD_16_DURATION:
			return clip.oddSixteenthsDurationScore();
		case LiveClip.EVEN_4_POSITION:
			return clip.evenQuarterNotePosScore();
		case LiveClip.EVEN_8_POSITION:
			return clip.evenEighthsNotePosScore();
		case LiveClip.EVEN_16_POSITION:
			return clip.evenSixteenthsNotePosScore();
		case LiveClip.ODD_4_POSITION:
			return clip.oddQuarterNotePosScore();
		case LiveClip.ODD_8_POSITION:
			return clip.oddEighthsNotePosScore();
		case LiveClip.ODD_16_POSITION:
			return clip.oddSixteenthsNotePosScore();
		case LiveClip.ZONE_2_COUNT:
			return pipe.zone2Count();
		default:
			return 0.0;
		}
	}
	private void addTextToGUI(Graphics2D g2d, double xsize, double ysize, double xpos, double ypos){
		Font font = new Font(textName, textStyle, fontSize);	
		g2d.setFont(font);
		g2d.setColor(textColor);
		g2d.drawString(ID, (int)(xpos + (nodeSize / 2)),  (int)(ypos + (nodeSize / 2)));
	}
	private void addNodeToGUI(Graphics2D g2d, double xsize, double ysize, double xpos, double ypos){
		circle = new Ellipse2D.Double(xpos, ypos, nodeSize, nodeSize);
		g2d.setColor(nodeColor);
		g2d.fill(circle);
	}
	private void addRing(Graphics2D g2d, double xsize, double ysize, double xpos, double ypos) {
		if (isMouseOvered || isCued || isPlaying){
			Ellipse2D.Double ring = new Ellipse2D.Double(xpos, ypos, nodeSize, nodeSize);
			if (isMouseOvered) g2d.setColor(mouseOverRingColor);
			if (isCued) g2d.setColor(cuedRingColor);
			if (isPlaying) g2d.setColor(playingRingColor);
			BasicStroke bs = new BasicStroke(5);
			g2d.setStroke(bs);
			g2d.draw(ring);
		}		
	}
	private PipelineNoteList removeDoubles(PipelineNoteList pnl){
		PipelineNoteList newPNL = pnl.cloneWithoutPNOList();
		for (PipelineNoteObject pno: pnl.pnoList){
			double pnoPos = pno.position;
			if (!containsPosition(pno, newPNL)){
				newPNL.addNoteObject(pno);
			} else {
				System.out.println("7777777777777777777777777777777777777");
			}
		}
		return newPNL;
		
	}
	private boolean containsPosition(PipelineNoteObject pno, PipelineNoteList pnl){
		boolean flag = false;
		for (PipelineNoteObject testpno: pnl.pnoList){
			String str = pno.position + " vs " + testpno.position;
			System.out.println(str);
			if (testpno.position == pno.position){
				System.out.println("######## TAKEN #######");
				flag = true;
			}
		}
		return flag;
	}
	private void makeID(){
		String end = Integer.toString(number);
		ID = idLetter;
		for (int i = 0; i < idLength - end.length(); i++){
			ID += "0";
		}
		ID += end;
	}
	private void makeFilterObjectList(){
		filterObjectList.clear();
//		addFilterObject(ppa.cf.getFilterObject());
		addFilterObject(ppa.at.getFilterObject());
		for (Pluggable ppi: pipe.plugList){
			addFilterObject(ppi.getFilterObject());
		}
	}
	private void addFilterObject(FilterObject obj){
		for (FilterObject filterObj: filterObjectList){
			if (filterObj.object == obj.object){
				return;
			}
		}
		filterObjectList.add(obj);
	}
//	private void addFilter(String str){
//		for (String filterStr: filterList){
//			if (filterStr.equals(str)){
//				return;
//			}
//		}
//		filterList.add(str);
//	}
	public static Comparator<Node> sortExpandComparator = new Comparator<Node>(){
		public int compare(Node node1, Node node2){
			if (node1.sortForExpansion < node2.sortForExpansion) return 1;
			if (node1.sortForExpansion > node2.sortForExpansion) return -1;
			return 0;
		}
	};
	
	private static final int idLength = 3;
	public static final int SORT_TO_BOTTOM = 0;
	public static final int SORT_TO_TOP = 1;
	
}
