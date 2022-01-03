package sorta_selekta.tests;
import ChordScaleDictionary.ChordScaleDictionary;
import acm.program.ConsoleProgram;
import sorta_selekta.node_manager.NodeManager;

public class NodeManagerConsoleTest extends ConsoleProgram {


	public void run(){
		ChordScaleDictionary csd = new ChordScaleDictionary();
		setSize(1100, 700);
		double time0 = System.currentTimeMillis();
		NodeManager nm = new NodeManager(new NodeTestSetup());

		double time1 = System.currentTimeMillis();
		println(nm.toString());
	
		double time2 = System.currentTimeMillis();
		double processTime = time1 - time0;
		double printTime = time2 - time1;
		double totalTime = time2 - time0;
		println("processTime=" + processTime);
		println("printTime=" + printTime);
		println("totalTime=" + totalTime);
	}

}
