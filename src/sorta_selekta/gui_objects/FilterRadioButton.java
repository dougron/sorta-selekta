package sorta_selekta.gui_objects;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JRadioButton;

import PlugIns.FilterObject;

public class FilterRadioButton extends JRadioButton {
	
	private ButtonGroup group;
	private FilterObject filterObject;
	private boolean whenEnteredWas = false;

	public FilterRadioButton() {
		// TODO Auto-generated constructor stub
	}

	public FilterRadioButton(Icon arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public FilterRadioButton(Action arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public FilterRadioButton(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public FilterRadioButton(Icon arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public FilterRadioButton(String arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public FilterRadioButton(String arg0, Icon arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public FilterRadioButton(String arg0, Icon arg1, boolean arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}
	public FilterRadioButton(FilterObject fo) {
		super(fo.name);
		filterObject = fo;
		// TODO Auto-generated constructor stub
	}
	public void setWhenEntered(){
		whenEnteredWas = isSelected();
	}

	public ButtonGroup getGroup (){
		return group;
	}

	public void setGroup (ButtonGroup group){
		this.group = group;
	}

	public FilterObject getFilterObject (){
		return filterObject;
	}

	public void setFilterObject (FilterObject filterObject){
		this.filterObject = filterObject;
	}

	public boolean isWhenEnteredWas (){
		return whenEnteredWas;
	}

	public void setWhenEnteredWas (boolean whenEnteredWas){
		this.whenEnteredWas = whenEnteredWas;
	}
	
	

}
