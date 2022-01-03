package sorta_selekta.tests;
import java.util.ArrayList;

import DataObjects.ableton_device_control_utils.DeviceParamInfo;
import DataObjects.ableton_device_control_utils.SendInfo;
import PipelineUtils.Pipeline;
import PlugIns.PlugInKikTwoOnFloor;
import ResourceUtils.AccentTemplate;
import ResourceUtils.ChordForm;
import TestUtils.TestData;
import UDPUtils.OSCMessMaker;
import acm.program.ConsoleProgram;
import sorta_selekta.clip_injector.ClipInjectorObject;
import sorta_selekta.node_list_generator.NodeListGenerator;
import sorta_selekta.node_list_generator.node.Node;


public class ClipInjectorObjectConsoleTest extends ConsoleProgram{

	
	public void run(){
		setSize(700, 700);
		ClipInjectorObject cioKik = new ClipInjectorObject(0, 0, ClipInjectorObject.ofTrackType);
		ClipInjectorObject cioSnr = new ClipInjectorObject(1, 0, ClipInjectorObject.ofTrackType);
		addControllers(cioKik);
		addControllers(cioSnr);
		
		ArrayList<NodeListGenerator> nlgList = new NodeTestSetup().makeNLGList();
//		println(nlgList.toString());
		NodeListGenerator nlgKik = nlgList.get(0);
		Node nodeKik = nlgKik.nodeList.get(1);
		NodeListGenerator nlgSnr = nlgList.get(1);
		Node nodeSnr = nlgSnr.nodeList.get(2);
		println(nodeSnr.toString());
		
		// below is important initialization sequence for ClipObjects in Live:
		// 1. send reset message
		// 2. send separate init messages for each ClipObject to be set up in Live
		// 3. .....
		// 4. then send the controller initialization messages
		ClipInjectorObject.sendResetInitializationMessage();
		OSCMessMaker mm;
		for (ClipInjectorObject cio: new ClipInjectorObject[]{cioKik, cioSnr}){
			mm = new OSCMessMaker();
			mm.addItem(DeviceParamInfo.initString);
			cio.instrumentInitializationMessage(mm);
			ClipInjectorObject.conn.sendUDPMessage(mm);
		}
		for (ClipInjectorObject cio: new ClipInjectorObject[]{cioKik, cioSnr}){
			cio.sendControllerInitializationMessages();
		}

		
		cioKik.sendNode(nodeKik);
		cioSnr.sendNode(nodeSnr);
//		println(nlg.toString());
	}
	
	private NodeListGenerator makeNLG(){
		NodeListGenerator nlg = new NodeListGenerator();
		NodeTestSetup nts = new NodeTestSetup();
		
		
		return nlg;
	}
	
	private Node makeNodeOne(){
		Node node = new Node(makePipeline(), makeChordForm(), makeAccentTemplate(), "XX");
		return node;
	}
	private Pipeline makePipeline(){
		Pipeline p = new Pipeline();
		p.addPlugInOption(new PlugInKikTwoOnFloor());
		for (int i = 0; i < p.plugOptionList.size(); i++){
			p.addPlugIn(i);
		}
		return p;
	}
	private ChordForm makeChordForm(){
		return new ChordForm(TestData.liveClipForTestForm());
	}
	private AccentTemplate makeAccentTemplate(){
		return new AccentTemplate(TestData.accentTemplateOne());
	}
	private void addControllers(ClipInjectorObject cio){

		cio.controllerList.add(new SendInfo("delaySend", cio.trackIndex, DeviceParamInfo.defaultDelaySendIndex, DeviceParamInfo.defaultDelayOffValue, cio.trackType));
		cio.controllerList.add(new DeviceParamInfo(DeviceParamInfo.default_HP, cio.trackIndex, cio.trackType));
	}

}
