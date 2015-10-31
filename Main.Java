/*
!!!!!
For resolving the problem, I am using org.json files. They can be downloaded from: http://www.json.org/java/index.html 
!!!!!
*/
package Main;

import org.json.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

	static String current_folder;
	public static void src(List<File> installed, JSONObject json, String item)
	{
		JSONArray newJson;
		try{
			newJson = json.getJSONArray(item);
		}
		catch(JSONException e)
		{
			return;
		}
		int newint = newJson.length();		
		System.out.println("Installing "+item+".");
		if(newint!=0) System.out.print("In order to install "+item+", we need to install ");
		else {
			return;
		}
		for(int i = 0;i<newint;i++)
		{
			if(i>0&&i==newint-1) System.out.print( " and ");
			System.out.print(newJson.getString(i));
		}
		System.out.println(".");
		for(int i = 0;i<newint;i++)
		{
				if(!checkIfInst(installed,newJson.getString(i)))
				{
					src(installed, json, newJson.getString(i));
				}
				else
				{
					System.out.println(newJson.getString(i)+" is already isntalled.");
				}		
		}		
	}
	public static String getFileAsString(String current, String filename)
	{
		Scanner sc=null;
		try {
			sc = new Scanner(new File (filename));
		} catch (FileNotFoundException e) {
				System.out.println("File not found! Please insert the file "+filename+" to: "+current);
				return "";
		}
		String test = "";
		while(sc.hasNextLine()){
			test += sc.nextLine()+"\n";
		}
		sc.close();
		return test;
	}
	public static void main(String[] args) throws IOException {
		current_folder = new java.io.File( "." ).getCanonicalPath();
		JSONObject json = new JSONObject(getFileAsString(current_folder, "all_packages.json"));
		JSONObject depen = new JSONObject(getFileAsString(current_folder, "dependencies.json"));
		JSONArray dep = depen.getJSONArray("dependencies");
		List<File> installed = Files.walk(Paths.get(current_folder+"/installed_modules/"))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
		int br = dep.length();
		for(int i=0;i<br;i++)
		{
			if(!checkIfInst(installed,dep.getString(i)))
			{
				src(installed,json, dep.getString(i));
			}
		}
		System.out.println("All done!");
	}
	public static boolean checkIfInst(List<File> installed, String item) 
	{
		int i=0;
		for(i=0;i<installed.size();i++)
		{
			if(installed.get(i).getName().equals(item)) return true;
		}
		installed.add(new File(current_folder+"/installed_modules/"+item));
		try {
			Files.createFile(installed.get(i).toPath());
		} catch (IOException e) {
			System.out.println("Cannot install "+installed.get(i).getName());
		}
		return false;
	}
}