package migrateCalendar;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;
/**
 * This class is responsible for searching and changing a calendar's oldOrganizerMail
 * @author vsilvalima
 *
 */
public class ReadCalendar 
{
	private File cal1;
	private String oldOrganizerMail;
	private String newOrganizer;
	private String oldOrganizerName;
	private String fileLocation;
	/**
	 * All the data is required to create a ReadClaendar Object
	 * @param cal the old calendar file
	 * @param oldOrganizerName the oldOrganizerMail name
	 * @param oldOrganizerMail the organizer's mail address
	 * @param newOrganizer the new organizer's mail address
	 * @param fileLocation the file location of the original Calendar file
	 */
	public ReadCalendar(File cal, String oldOrganizerName,String oldOrganizerMail,String newOrganizer,String fileLocation)
	{
		cal1=cal;
		this.oldOrganizerMail=oldOrganizerMail;
		this.newOrganizer=newOrganizer;
		this.oldOrganizerName=oldOrganizerName;
		this.fileLocation=fileLocation;
	}
	public void setOldOrganizerName(String oldOrganizerName)
	{
		this.oldOrganizerName=oldOrganizerName;
	}

	public void setCalendar(File cal)
	{
		cal1=cal;
	}
	public void setFileLocation(String fileLocation)
	{
		this.fileLocation=fileLocation;
	}
	public void setOrganizer(String oldOrganizerMail)
	{
		this.oldOrganizerMail=oldOrganizerMail;
	}
	public void setNewOrganizer(String newOrganizer)
	{
		this.newOrganizer=newOrganizer;
	}
	/**
	 * 
	 * @return
	 */
	public  int lookForOrganizer()
	{
		int count=-1;
		if(cal1!=null && oldOrganizerMail!=null )
		{
			//Scanner readCal=null;
			FileReader fis = null;
			BufferedReader readCal=null;
			BufferedWriter output=null;
			File outputFile=new File(fileLocation+"/newCalendar.ics");
			try 
			{
				fis = new FileReader(cal1);
				readCal = new BufferedReader(fis);
				FileWriter fWriter = new FileWriter(outputFile,false);
				output=new BufferedWriter(fWriter);
				String line=readCal.readLine();
				long countLines=0;
				int countrGroupErrors=0;
				int countInvalidMailAddress=0;
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
					}
					else
					{
						if(checkOrganizer(line))
						{
							String changeOrg =changeOrganizer();
							if(changeOrg.length()>75)
							{
								output.write(changeOrg.substring(0, 74));
								output.newLine();
								line=readCal.readLine();
								countLines++;
								line = '\t'+changeOrg.substring(75);
							}
							else
							{
								line=changeOrganizer();
							}
							count++;
						}
						else if(line.contains("invalid:nomail"))
						{
							//String invalidString=line.replace("invalid:nomail","mail")
							//File contactFile = new File("/newContact.txt");
							InputStream contactFile =this.getClass().getClassLoader().getResourceAsStream("newContact.txt");
							Scanner scan = new Scanner(contactFile);
							int index1 = line.indexOf('"');
							int index2 = line.indexOf('"', index1+1);
							if(index1!=-1 && index2!=-1)
							{
								String search = line.substring(index1+1, index2);
								boolean find=false;
								while(scan.hasNextLine() && !find)
								{
									String info= scan.nextLine();
									if(info.contains(search))
									{
										find=true;
										line=line.replace("invalid:nomail",scan.nextLine());
									}
								}
								scan.close();
								if(!find)
								{
									line=line.replace("invalid:nomail","none@langara.bc.ca");
								}
								if(line.length()>=75)
								{
									output.write(line.substring(0, 74));
									output.newLine();
									line=line.substring(75);
								}
							}
							countInvalidMailAddress++;
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
		String checkOrganizerName = "ORGANIZER;CN="+'"'+oldOrganizerName+'"'+":mailto:"+oldOrganizerMail;
		if(checkOrganizerName.equals(line))
		{
			return true;
		}
		else if(checkOrganizerName.length()>= 75 && line.length()>=75 && checkOrganizerName.substring(0, 74).equals(line.substring(0,74)))
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
		return "ORGANIZER;CN="+'"'+oldOrganizerName+'"'+":mailto:"+newOrganizer;
	}
}
