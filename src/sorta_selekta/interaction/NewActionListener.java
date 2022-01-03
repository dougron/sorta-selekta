package sorta_selekta.interaction;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sorta_selekta.gui_objects.FilterRadioButton;

public class NewActionListener implements ActionListener{
	
	FilterRadioButton button;

	public FilterRadioButton getButton ()
	{
		return button;
	}


	public void setButton (FilterRadioButton button)
	{
		this.button = button;
	}


	public NewActionListener(FilterRadioButton button) {
		this.button = button;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}
}