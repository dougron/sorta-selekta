package sorta_selekta.interaction;


import java.awt.event.MouseAdapter;

import javax.swing.JRadioButton;

import sorta_selekta.gui_objects.FilterRadioButton;

public class NewMouseAdapter extends MouseAdapter{
	
	private FilterRadioButton button;


	public NewMouseAdapter(FilterRadioButton button){
		super();
		this.button = button;
	}
	
	public FilterRadioButton getButton ()
	{
		return button;
	}
	
	public void setButton (FilterRadioButton button)
	{
		this.button = button;
	}
}
