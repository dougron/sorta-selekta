package sorta_selekta.one_shot_output;
import ChordScaleDictionary.ChordScaleDictionary;
import DataObjects.ableton_device_control_utils.DeviceParamInfo;
import DataObjects.ableton_live_clip.LiveClip;
import UDPUtils.OSCMessMaker;
import acm.program.ConsoleProgram;
import sorta_selekta.clip_injector.ClipInjectorObject;
import sorta_selekta.node_list_generator.NodeListGenerator;
import sorta_selekta.node_list_generator.node.Node;
import sorta_selekta.node_manager.NodeManager;
import sorta_selekta.tests.NodeTestSetup;

public class NodeManagerMidiOutput extends ConsoleProgram{

	
	public void run(){
		ChordScaleDictionary csd = new ChordScaleDictionary();
		setSize(1100, 700);
		double time0 = System.currentTimeMillis();
		
		// the actual test -------------
		NodeManager nm = new NodeManager(new NodeTestSetup());
		NodeListGenerator melodyNLG = nm.nlgList.get(5);
		ClipInjectorObject cio = melodyNLG.cueInject;
//		LiveClip lc = melodyNLG.nodeList.get(1).clip;
		LiveClip lc = makeTotalClip(melodyNLG);
		cio.sendClip(lc);
		// end of actual test -----------

		double time1 = System.currentTimeMillis();
		println(nm.toString());
		println(lc.toString());
	
		double time2 = System.currentTimeMillis();
		double processTime = time1 - time0;
		double printTime = time2 - time1;
		double totalTime = time2 - time0;
		println("processTime=" + processTime);
		println("printTime=" + printTime);
		println("totalTime=" + totalTime);
		
	}
	private LiveClip makeTotalClip(NodeListGenerator melodyNLG) {
		LiveClip lc = new LiveClip(0, 0);
		boolean lcExists = false;
		int nodeCount = 0;
		for (Node node: melodyNLG.nodeList){
			if (!lcExists){
				lc = node.clip.clone();
				lcExists = true;
			} else {
				if (nodeCount < 20){
					lc.appendClip(node.clip);
					nodeCount++;
				}
				
			}
		}
		 
		return lc;
	}
	private OSCMessMaker liveClipObjectInitializationMessage(ClipInjectorObject cio) {
		OSCMessMaker mm = new OSCMessMaker();
		mm.addItem(DeviceParamInfo.initString);
		cio.instrumentInitializationMessage(mm);
//		System.out.println("NodeListGenerator.liveClipObjectInitializationMessage-------");
//		System.out.println(mm.toString());
		return mm;
	}

	
}
