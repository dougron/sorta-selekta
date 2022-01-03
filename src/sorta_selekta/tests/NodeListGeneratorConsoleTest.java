package sorta_selekta.tests;
import java.util.ArrayList;

import ChordScaleDictionary.ChordScaleDictionary;
import PlugIns.ED;
import PlugIns.PlugInBassAddEmbellishmentOne;
import PlugIns.PlugInBassFromRhythmBuffer;
import ResourceUtils.AccentTemplate;
import ResourceUtils.ChordForm;
import TestUtils.TestData;
import acm.program.ConsoleProgram;
import sorta_selekta.gui_objects.FilterRadioButton;
import sorta_selekta.node_list_generator.NodeListGenerator;
import sorta_selekta.node_list_generator.node.Node;
import sorta_selekta.option_maker.OptionMaker;

public class NodeListGeneratorConsoleTest extends ConsoleProgram{

	public ChordScaleDictionary csd = new ChordScaleDictionary();
	
	public void run(){
		setSize(800, 800);
		NodeListGenerator nlg = new NodeListGenerator("Bass", "B");
		addPlugs(nlg);
		addChordForms(nlg);
		addAccentTemplates(nlg);
		nlg.makeNodeList();
		
		
		//println(nlg.pipeListToString());
		//println(nlg.cfListToString());
		//println(nlg.atListToString());
		println(nlg.nodeListToString());
		println(nlg.buttonListToString());
//		println(nlg.filterObjectList.size() + " items in filterObjectList");
//		println(nlg.includeButtonList.size() + " items in includeButtonList");
		
		for (FilterRadioButton frb: nlg.includeButtonList){
			frb.setSelected(true);
		}
		nlg.excludeButtonList.get(3).setSelected(true);
		nlg.makeFilteredNodeList();
		println(nlg.buttonListToString());
		println(nlg.concurrentFilteredNodeList.size() + " items in filteredNodeList");
		for (Node node: nlg.concurrentFilteredNodeList){
			println(node.ID);
		}
	}
	
// privates --------------------------------------------------------------------
	private void addAccentTemplates(NodeListGenerator nlg){
		nlg.atList.add(new AccentTemplate(TestData.accentTemplateOne()));
		//nlg.atList.add(new AccentTemplate(TestData.accentTemplateTwo()));
	}
	private void addChordForms(NodeListGenerator nlg){
		ChordForm cf = new ChordForm(TestData.liveClipForTestForm());
		nlg.cfList.add(cf);
	}
	private void addPlugs(NodeListGenerator nlg){
		nlg.genList.add(new PlugInBassFromRhythmBuffer());
		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
				new double[]{-0.25, -0.5},
				new double[]{1.0, 3.0},
				1.0,
				new ED[]{new ED("s", 0)},
				new double[]{1.0}));
//		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
//				new double[]{-0.25, -0.5},
//				new double[]{1.0, 3.0},
//				1.0,
//				new ED[]{new ED("s", 0), new ED("s", -1)},
//				new double[]{1.0, 1.0}));
//		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
//				new double[]{-0.25, -0.5},
//				new double[]{1.0, 3.0},
//				1.0,
//				new ED[]{new ED("s", 0), new ED("s", -1), new ED("s", -5)},
//				new double[]{1.0, 1.0, 1.0}));
//		nlg.proList.add(new PlugInBassAddEmbellishmentOne(
//				new double[]{-0.5, -1.0},
//				new double[]{1.0, 3.0},
//				1.0,
//				new ED[]{new ED("s", 0), new ED("s", -1), new ED("s", -5)},
//				new double[]{1.0, 1.0, 1.0}));
	//	nlg.proList.add(new PlugInBassAddEmbellishmentOne(
	//			new double[]{-0.75, -1.0, -1.25},
	//			new double[]{1.0, 3.0, 1.5},
	//			1.0,
	//			new ED[]{new ED("s", 0), new ED("s", -1), new ED("s", -5)},
	//			new double[]{1.0, 1.0, 1.0}));

	}
	private void optionMakerDemo(){
		for (ArrayList<Integer> list: OptionMaker.getOptionList(10)){
			for (Integer i: list){
				print(i + ",");
			}
			println("");
		}
	}
}
