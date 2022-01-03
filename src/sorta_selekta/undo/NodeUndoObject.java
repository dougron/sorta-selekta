package sorta_selekta.undo;
import java.util.ArrayList;

import UndoList.UndoListInterface;
import sorta_selekta.node_list_generator.NodeListGenerator;
import sorta_selekta.node_list_generator.node.Node;

public class NodeUndoObject implements UndoListInterface{

	public Node[] nodeArr;
	
	public NodeUndoObject(ArrayList<NodeListGenerator> nlgList){
		nodeArr = new Node[nlgList.size()];
		int index = 0;
		for (NodeListGenerator nlg: nlgList){
			if (nlg.playingNode != null){
				nodeArr[index] = nlg.playingNode;
			} else {
				nodeArr[index] = Node.nullNode;
			}
			index++;
		}
	}
	public String undoListString(){
		String ret = "";
		for (Node node: nodeArr){
			ret += "-" + node.ID;
		}
		return ret;
	}
	@Override
	public String listDescription() {

		return undoListString();
	}
	public Node getNode(int index){
		return nodeArr[index];
	}
	@Override
	public Object getUndoObject(int index) {
		
		return nodeArr[index];
	}
}
