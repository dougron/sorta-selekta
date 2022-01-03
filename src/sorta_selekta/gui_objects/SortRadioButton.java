package sorta_selekta.gui_objects;
import javax.swing.JRadioButton;

import DataObjects.combo_variables.IntAndString;

public class SortRadioButton extends JRadioButton {
	
	public IntAndString sortItem;

	public SortRadioButton(IntAndString ins){
		super(ins.str);
		sortItem = ins;
		
	}
}
