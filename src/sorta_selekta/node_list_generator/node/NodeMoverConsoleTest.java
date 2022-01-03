package sorta_selekta.node_list_generator.node;
import acm.program.ConsoleProgram;

public class NodeMoverConsoleTest extends ConsoleProgram {

	
	public void run(){
		setSize(700, 700);
		NodeMover nm = new NodeMover(0);
//		for (int i = 0; i < 20; i++){
//			println("step: " + i + " - " + nm.next());
//		}
		nm.moveTo(10, 20);
		for (int i = 0; i < 25; i++){
			println("step: " + i + " - " + nm.next());
		}
	}
}
