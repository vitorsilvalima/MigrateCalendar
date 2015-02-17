package migrateCalendar;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
/**
 * This class is responsible for searching and changing a calendar's oldOrganizerMail
 * @author Vitor Silva Lima
 * @since 08/02/2015
 */
public class ReadCalendar 
{
	private File calendarFile;
	private String oldOrganizerMail;
	private String newOrganizerMail;
	private String oldOrganizerName;
	private String fileLocation;
	/**
	 * All the data is required to create a ReadClaendar Object
	 * @param cal the old calendar file
	 * @param oldOrganizerName the oldOrganizerMail name
	 * @param oldOrganizerMail the organizer's mail address
	 * @param newOrganizerMail the new organizer's mail address
	 * @param fileLocation the file location of the original Calendar file
	 */
	public ReadCalendar(File cal, String oldOrganizerName,String oldOrganizerMail,String newOrganizerMail,String fileLocation)
	{
		calendarFile=cal;
		this.oldOrganizerMail=oldOrganizerMail;
		this.newOrganizerMail=newOrganizerMail;
		this.oldOrganizerName=oldOrganizerName;
		this.fileLocation=fileLocation;
	}
	/**
	 * Sets the organizer's name
	 * @param oldOrganizerName the organizer's name
	 */
	public void setOldOrganizerName(String oldOrganizerName)
	{
		this.oldOrganizerName=oldOrganizerName;
	}
	/**
	 * Sets the Calendar file
	 * @param cal the calendar file object
	 */
	public void setCalendar(File cal)
	{
		calendarFile=cal;
	}
	/**
	 * Sets the file location of the new calendar
	 * @param fileLocation the location for the new calendar file
	 */
	public void setFileLocation(String fileLocation)
	{
		this.fileLocation=fileLocation;
	}
	/**
	 * Sets the old organizer's mail address
	 * @param oldOrganizerMail the old organizer's mail address
	 */
	public void setOrganizer(String oldOrganizerMail)
	{
		this.oldOrganizerMail=oldOrganizerMail;
	}
	/**
	 * Sets new organizer's mail address
	 * @param newOrganizerMail
	 */
	public void setNewOrganizer(String newOrganizerMail)
	{
		this.newOrganizerMail=newOrganizerMail;
	}
	/**
	 * Fixes the calendar by going through each line and searching by group errors and invalid mail addresses. Furthermore, it also fixes organizer's issues
	 * @return -1 if the calendar or old organizer mail is equal to null
	 */
	public  int fixCalendar()
	{
		int count=-1;
		if(calendarFile!=null && oldOrganizerMail!=null )
		{
			FileReader fis = null;
			BufferedReader readCal=null;
			BufferedWriter output=null;
			/*
			 * Counters
			 */
			long countLines=0;
			int countrGroupErrors=0;
			int countInvalidMailAddress=0;
			
			File outputFile=new File(fileLocation+"/newCalendar.ics");//creates a new calendar file
			try 
			{
				fis = new FileReader(calendarFile);
				readCal = new BufferedReader(fis);
				FileWriter fWriter = new FileWriter(outputFile,false);
				output=new BufferedWriter(fWriter);
				String line=readCal.readLine();
				while(line!=null)
				{
					countLines++;
					if(checkGroupError(line))
					{
						countrGroupErrors++;
						do
						{
							line=readCal.readLine();
							countLines++;
						}while(!(line.contains("ATTENDEE") || line.contains("PRIORITY") || line.contains("CLASS")));
						count++;
					}
					else
					{
						if(checkOrganizer(line))
						{
							line=changeOrganizer();
							if(line.length()>75)
							{
								output.write(line.substring(0, 75));
								output.newLine();
								readCal.readLine();
								countLines++;
								line = '\t'+line.substring(75);
							}
							count++;
						}
						else if(line.contains("invalid:nomail"))
						{
							SearchContacInfo searchContact = new SearchContacInfo("./newContact.txt");
							line = line.replace("invalid:nomail", searchContact.getContactMail(getContactName(line)));
							if(line.length()>=75)
							{
								output.write(line.substring(0, 75));
								output.newLine();
								line=line.substring(75);
							}
							countInvalidMailAddress++;
							count++;
						}
						output.write(line);
						output.newLine();
						output.flush();
						line=readCal.readLine();
					}
				}
				JOptionPane.showMessageDialog(null,countrGroupErrors+" group errors were successfully fixed!!!");
				JOptionPane.showMessageDialog(null,countInvalidMailAddress+" invalid mail addresses were successfully fixed!!!");
				System.out.println("Number of lines = "+countLines);
				readCal.close();
				output.close();
				return count;
			} catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			catch( IOException e)
			{
				e.printStackTrace();
			}
		}
		return count;
	}
	/**
	 * Gets the contact name contained in the line
	 * @param line
	 * @return the contact name
	 */
	private String getContactName(String line)
	{
		int index1 = line.indexOf('"');
		int index2 = line.indexOf('"', index1+1);
		if(index1!=-1 && index2!=-1)
		{
			return line.substring(index1+1, index2);
		}
		return "Not Found";
	}
	/**
	 * Check for groups errors in a specific line
	 * @param line
	 * @return true if it finds a group error
	 */
	private boolean checkGroupError(String line)
	{
		if(line.toUpperCase().contains("O=EXCHA"))
		{
			return true;
		}
		return false;
	}
	/**
	 * Checks whether the appointment belongs to a specific organizer or not
	 * @param line from the ics file
	 * @return returns true if there is a match
	 */
	private boolean checkOrganizer(String line)
	{
		if(line.contains("ORGANIZER;CN="+'"'+oldOrganizerName+'"')||(line.contains("ORGANIZER;CN=")&&line.contains(":mailto:"+oldOrganizerMail)))
		{
			return true;
		}
		return false;
	}
	/**
	 * Changes the organizers from the original to the new mail address
	 * @return a new line to replace the old organizer
	 */
	private String changeOrganizer()
	{
		return "ORGANIZER;CN="+'"'+oldOrganizerName+'"'+":mailto:"+newOrganizerMail;
	}
}
