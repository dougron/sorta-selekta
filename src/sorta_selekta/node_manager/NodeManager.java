package sorta_selekta.node_manager;
import java.util.ArrayList;


import DataObjects.ableton_live_clip.LiveClip;
import UndoList.UndoListInterface;
import sorta_selekta.clip_injector.ClipInjectorObject;
import sorta_selekta.node_list_generator.NodeListGenerator;
import sorta_selekta.node_list_generator.node.Node;
import sorta_selekta.undo.NodeUndoObject;
import sorta_selekta.setup.NodeManagerSetupObject;

public class NodeManager{

	public ArrayList<NodeListGenerator> nlgList; 
	public ArrayList<UndoListInterface> undoList = new ArrayList<UndoListInterface>();
//	public int undoIndex = 0;
	public int undoListPlayIndex = 0;
	public int undoListCueIndex = 0;
	
	
	public NodeManager(NodeManagerSetupObject n){
		nlgList = n.makeNLGList();
		renderAllFilteredNodeLists();
		initializeLiveClipObjects();
		resetCueClips();
		setCuedToPlay();
//		addCurrentPlayingNodesAsUndoObject();
	}
	public void addCurrentPlayingNodesAsUndoObject() {
		int cueUndoIndex = currentCuesAreNew();
		if (cueUndoIndex == -1){
			if (undoList.size() == 0){
				undoList.add(new NodeUndoObject(nlgList));
				undoListPlayIndex = 0;
				undoListCueIndex = 0;
			} else {
				undoListPlayIndex++;
				undoListCueIndex = undoListPlayIndex;
				undoList.add(undoListPlayIndex, new NodeUndoObject(nlgList));
			}
		} else {
			undoListPlayIndex = cueUndoIndex;
			undoListCueIndex = cueUndoIndex;
		}
		
		
		
	}
	private int currentCuesAreNew() {
		boolean test = false;
		for (int uIndex = 0; uIndex < undoList.size(); uIndex++){
			UndoListInterface uli = undoList.get(uIndex);
			//System.out.println(uli.listDescription());
			test = true;
			for (int i = 0; i < nlgList.size(); i++){
				NodeListGenerator nlg = nlgList.get(i);
				Node node = (Node)uli.getUndoObject(i);
				if (nlg.playingNode != null && nlg.playingNode == node){
					//test = true;
				} else {
					test = false;
				}
				//System.out.println(nlg.playingNode.ID + " " + test);
			}
			//System.out.println("test=" + test);
			if (test == true){
				
				return uIndex;
			}
		}
		return -1;
	}
	public void resetCueClips(){
		for (NodeListGenerator nlg: nlgList){
//			nlg.playInject.sendClip(nlg.playingNode.clip);
			nlg.cueInject.sendClip(LiveClip.emptyClip());
//			nlg.clearSelectedNodes();
		}
	}
	private void renderAllFilteredNodeLists(){
		for (NodeListGenerator nlg: nlgList){
			nlg.makeFilteredNodeList();
		}
	}
	public void initializeLiveClipObjects(){
//		System.out.println("NodeManager.initializeLiveClipObjects sending OSC messages");
		ClipInjectorObject.sendResetInitializationMessage();
		for (NodeListGenerator nlg: nlgList){
			nlg.sendLiveClipObjectInitializationMessages();
			nlg.sendLiveControllerInitializationMessage();
		}
	}
	
	
	public String toString(){
		String ret = "NodeManager:----\n";
		for (NodeListGenerator nlg: nlgList){
			ret += "\n" + nlg.name + "################################################\n";
//			ret += nlg.nodeListToString();			// this makes detailed list
			ret += nlg.shortNodeListToString();		// this makes shortened list
		}
		
		return ret;
	}
	public void setCuedToPlay(){
		//System.out.println("NodeManager.setCuedToPlay called");
		for (NodeListGenerator nlg: nlgList){
			nlg.setCuedToPlay();
		}
		addCurrentPlayingNodesAsUndoObject();
	}
	public void changeUndoCueIndex(int i) {
		if (i == 1){
			if (undoListCueIndex < undoList.size() - 1){
				undoListCueIndex++;
			}
		} else {
			if (undoListCueIndex > 0){
				undoListCueIndex--;
			}
		}
		
	}
	public void cueUpCurrentlyCuedUndoItem() {
		//System.out.println("cue up undo item call");
		for (int i = 0; i < nlgList.size(); i++){
			UndoListInterface uli = undoList.get(undoListCueIndex);
			NodeListGenerator nlg = nlgList.get(i);
			nlg.setThisNodeAsCued((Node)uli.getUndoObject(i));
			nlg.addCuedNodeToFilteredNodeListIfNeccesary();
		}
		
	}
	
	
}
