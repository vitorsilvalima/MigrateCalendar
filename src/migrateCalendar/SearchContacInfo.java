package migrateCalendar;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/**
 * The script goes through a contact file(name+mail) and searches for a specific name.
 * If it finds the specified name, it returns the person's mail address.
 * @author Vitor Silva Lima
 * @since 17/02/2015
 */
public class SearchContacInfo
{
	/**
	 * stores the location of the contact file
	 */
	private String contactFileLocation;
	/**
	 * has a File object that refers to a specific contact file
	 */
	private File contactFile;
	/**
	 * Creates a SearchContactInfor object and instatiates its variables
	 * @param contactLocation location of the contact file
	 * @throws IllegalArgumentException if the user did not specify the file location
	 */
	public SearchContacInfo(String contactLocation) throws IllegalArgumentException
	{
		if(contactLocation!=null && !contactLocation.equals(""))
		{
			contactFileLocation=contactLocation;
		}
		else
		{
			throw new IllegalArgumentException("The specified location for the contact information\n"
					+ " file is not valid!");
		}
		setContactFile();
	}
	/**
	 * Sets the location of the contact file
	 * @param contactLocation location of the contact file
	 */
	public void setContactLocation(String contactLocation)
	{
		contactFileLocation=contactLocation;
	}
	/**
	 * Sets the File object 
	 */
	private void setContactFile()
	{
		contactFile=new File(contactFileLocation);
	}
	/**
	 * Gets the contact mail of the specified person's name
	 * @param contactName
	 * @return the person's email or none@langara.bc.ca if it does not find 
	 * @throws FileNotFoundException
	 */
	public String getContactMail(String contactName) throws FileNotFoundException
	{
		Scanner scanContactList = new Scanner(contactFile);
		String contactMail="";
		String line;
		while(scanContactList.hasNextLine() && contactMail.contains(""))
		{
			line=scanContactList.nextLine();
			if(line.contains(contactName))
			{
				contactMail=scanContactList.nextLine();
			}
		}
		if(contactMail.contains(""))
		{
			contactMail="none@langara.bc.ca";
		}
		scanContactList.close();
		return contactMail;
	}
}
