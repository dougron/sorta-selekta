package sorta_selekta.one_shot_output;
import java.util.ArrayList;

import DataObjects.ableton_live_clip.LiveClip;
import DataObjects.ableton_live_clip.LiveMidiNote;
import DataObjects.combo_variables.DoubleAndString;
import PlugIns.Pluggable;
import XMLMaker.MXM;
import XMLMaker.MusicXMLMaker;
import XMLMaker.XMLTimeSignatureZone;
import XMLMaker.XMLKeyZone;
import acm.program.ConsoleProgram;
import sorta_selekta.node_list_generator.NodeListGenerator;
import sorta_selekta.node_list_generator.node.Node;
import sorta_selekta.node_manager.NodeManager;
import sorta_selekta.tests.NodeTestSetup;

public class NodesToXML extends ConsoleProgram {
	
	private String path = "D:/_DALooperTXT/XMLMakerFiles/nodesToXML.xml";
	double beatsRequired = 0.0;
	ArrayList<DoubleAndString> directionList = new ArrayList<DoubleAndString>();
	
	public void run(){
		setSize(400, 700);
		NodeManager nm = new NodeManager(new NodeTestSetup());
		MusicXMLMaker mx = new MusicXMLMaker(MXM.KEY_OF_C);
		double totalBeatsRequired = 0.0;
		for (NodeListGenerator nlg: nm.nlgList){			
			LiveClip lc = null;
			
//			int count = 10;
//			lc = getCountOfNLGList(nlg, count);
			
			int[] indexArr = new int[]{2, 3};	//{2, 3, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
//			redoSelectedNodes(indexArr, nlg);
			lc = getLCFromIndexArray(nlg, indexArr);
			do16thsTo8ths(lc);		// not working so good

			if (totalBeatsRequired < beatsRequired) totalBeatsRequired = beatsRequired;
			
			println(nlg.name + "========================================");
			println(lc.toString());
			
			mx.addPart(nlg.name, lc);
			double pos = 0.0;
			for (DoubleAndString dns: directionList){
				mx.addTextDirection(nlg.name, dns.str, dns.d, MXM.PLACEMENT_ABOVE);

			}
			
		}
		mx.measureMap.addNewTimeSignatureZone(new XMLTimeSignatureZone(4, 4, (int)(totalBeatsRequired / 4)));
		mx.keyMap.addNewKeyZone(new XMLKeyZone(MXM.KEY_OF_Eb, (int)(totalBeatsRequired / 4)));
		mx.makeXML(path);
	}


	private void do16thsTo8ths(LiveClip lc) {
		ArrayList<LiveMidiNote> tempNoteList = new ArrayList<LiveMidiNote>();
		LiveMidiNote previousNote = lc.firstNote();
		for (LiveMidiNote lmn: lc.noteList){
			if (tempNoteList.size() == 0){
				if (isSixteenth(lmn) && isOnEighthBeat(lmn)){
					tempNoteList.add(lmn);					
				}
				previousNote = lmn;
				
			} else {
				if (closeEnough(lmn.position, previousNote.position, 0.01)){
					tempNoteList.add(lmn);
					previousNote = lmn;
				} else {
					if(currentNoteIsFarEnoughAhead(previousNote, lmn)){
						for (LiveMidiNote lll: tempNoteList){
							lll.length = 0.5;
						}
						tempNoteList.clear();
						if (isSixteenth(lmn) && isOnEighthBeat(lmn)){
							tempNoteList.add(lmn);	
						}
						previousNote = lmn;
					}
				}
			}
		}
		
	}


	private boolean isSixteenth(LiveMidiNote previousNote) {
		
		if (closeEnough(previousNote.length, 0.25, 0.01)){
			return true;
		} else {
			return false;
		}		
	}


	private boolean closeEnough(double length, double d, double e) {
		if (length >= d - e && length <= d + e){
			return true;
		} else {
			return false;
		}
		
	}


	private boolean isOnEighthBeat(LiveMidiNote previousNote) {
		double modPos = previousNote.position % 1.0;
		if (closeEnough(modPos, 0.0, 0.01) || closeEnough(modPos, 0.5, 0.01)){
			return true;
		} else {
			return false;
		}
		
	}


	private boolean currentNoteIsFarEnoughAhead(LiveMidiNote previousNote, LiveMidiNote lmn) {
		double distance = lmn.position - previousNote.position;
		if (distance >= 0.5){
			return true;
		} else {
			return false;
		}
		
	}


	private void redoSelectedNodes(int[] indexArr, NodeListGenerator nlg) {
		LiveClip clip;
		for (int i: indexArr){
			Node node = nlg.nodeList.get(i);
			System.out.println("Node " + node.ID + " remaking clip");
			
			clip = node.reRenderAndGetClip();
			System.out.println(clip.toString());
		}
		
	}


	private LiveClip getLCFromIndexArray(NodeListGenerator nlg, int[] indexArr) {
		LiveClip lc = null;
		directionList.clear();
		beatsRequired = 0;
		for (int index: indexArr){
			if (index < nlg.nodeList.size()){
				Node node = nlg.nodeList.get(index);
				if (lc == null){
					lc = node.clip.clone();
				} else {
					lc.appendClip(node.clip);
				}
				
				directionList.add(new DoubleAndString(beatsRequired, nodeNameAndPipeList(node)));
				beatsRequired += node.clip.length;
			}
		}
		return lc;
	}


	private String nodeNameAndPipeList(Node node) {
		String str = node.ID + " - ";
		for (Pluggable plug: node.pipe.plugList){
			str += plug.originalName() + ", ";
		}
		return str;
	}


	private LiveClip getCountOfNLGList(NodeListGenerator nlg, int count) {
		LiveClip lc = null;
		for (Node node: nlg.nodeList){
			if (count > 0){
				if (lc == null){
					lc = node.clip.clone();
				} else {
					lc.appendClip(node.clip);
				}
				beatsRequired += lc.length;
				count--;
			}
			
		}
		return lc;
	}
}
