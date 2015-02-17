package migrateCalendar;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 * MigrateCalendar has as a goal to fix some of the major calendar issues such as:
 * -Organizer issue
 * -Group errors
 * -Invalid mail addresses
 * @author Vitor Silva Lima
 * @since 08/02/2015
 */
public class MigrateCalendar 
{
	/**
	 * Main method which has all the functionalities for data input and it also has a ReadCalendar object
	 * @param args an array of elements of type String which is used to pass information that comes from the runtime system 
	 */
	public static void main(String[] args) 
	{
		//the new user mail address
		String newOrganizer="";
		//the old user mail address
		String oldOrganizer="";
		//the user name, because outlook sometimes saves organizer information as the user's name
		String oldOrganizerName="";
		String fileLocation="";
		//input
		do
		{
			oldOrganizerName=(JOptionPane.showInputDialog("Please, enter old organizer's name:"));
			
		}while(oldOrganizerName==null || oldOrganizerName.equals(""));
		do
		{
			oldOrganizer=JOptionPane.showInputDialog("Please, enter old organizer's mail:");
		}while(oldOrganizer==null ||oldOrganizer.equals(""));
		do
		{
			newOrganizer=JOptionPane.showInputDialog("Please, enter new organizer's mail:");
		}while(newOrganizer==null || newOrganizer.equals(""));
		JFileChooser chooser = new JFileChooser(); 
		try
		{
			chooser.setFileFilter(new FileNameExtensionFilter(".ics", "ics"));
			while(chooser.showOpenDialog(null) !=JFileChooser.APPROVE_OPTION)
			{}
				File cal1 = chooser.getSelectedFile();
				fileLocation=cal1.getParent();
				ReadCalendar calendarReader = new ReadCalendar(cal1, oldOrganizerName,oldOrganizer, newOrganizer,fileLocation);
				int appointmentChanges =calendarReader.fixCalendar();
				if(appointmentChanges>=0)
				{
					JOptionPane.showMessageDialog(null, "The new file has been saved in the same location"
							+ "\n of the selected calendar!");
				}
				else
				{
					JOptionPane.showMessageDialog(null,"No appointment with the entered organizer was found!!!");
				}

		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Warning!!!", 1);
			e.printStackTrace();
		}
	}
}
