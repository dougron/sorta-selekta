package sorta_selekta.tests;
import PipelineUtils.Pipeline;
import PlugIns.PlugInBassFromRhythmBuffer;
import ResourceUtils.AccentTemplate;
import ResourceUtils.ChordForm;
import TestUtils.TestData;
import acm.program.ConsoleProgram;
import sorta_selekta.node_list_generator.node.Node;

public class NodeConsoleTest extends ConsoleProgram{

	
	public void run(){
		setSize(800, 800);
		Pipeline p = new Pipeline();
		p.addPlugInOption(new PlugInBassFromRhythmBuffer());
		p.addPlugIn(0);
		ChordForm cf = new ChordForm(TestData.liveClipForTestForm());
		AccentTemplate at = new AccentTemplate(TestData.accentTemplateOne());
		Node n = new Node(p, cf, at, "X");
		println(n.toString());
	}
}
