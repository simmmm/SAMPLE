import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Template
{
	private Template pParent;
	private String pHtml;
	private String pSection;
	private String pUniqId;

	private HashMap<String, List<Template>> pListTemplateItem = new HashMap<String, List<Template>>();
	private HashMap<String, String> pHtmlTemplate = new HashMap<String, String>();  
	private HashMap<String, String> pHashVariable = new HashMap<String, String>();

	private static String PATTERN = "<!--[ ]*BEGIN[ ]*__SECTION__.([a-zA-Z0-9]+)[ ]*-->[ ]*(.*?)[ ]*<!--[ ]*END[ ]*__SECTION__.([a-zA-Z0-9]+)[ ]*-->";

	public Template(String fileTemplateHtml) throws FileNotFoundException, IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(fileTemplateHtml));
		String line = br.readLine();
		pHtml = "";		

		while(line != null)
		{
			pHtml += line;
			line = br.readLine();
		}
		
		pSection = "ROOT";
		pUniqId = "ROOT";

		initAllAvailableSection();
	}

	public Template(Template parent, String html, String section, String uniqId)
	{
		pParent = parent;
		pHtml = html;
		pSection = section;
		pUniqId = uniqId;

		initAllAvailableSection();		
	}

	private void initAllAvailableSection()
	{
		Pattern p = Pattern.compile(PATTERN.replaceAll("__SECTION__", pSection));
		
		Matcher m = p.matcher(pHtml);

		while(m.find())
		{
			pListTemplateItem.put(m.group(1), new ArrayList<Template>());		
			pHtmlTemplate.put(m.group(1), m.group(2));
		}
	}	

	public void addVar(String variable, String valeur)
	{
		pHashVariable.put(variable, valeur);
	}

	public Template initNewSection(String initSection, String nom)
	{
		String htmlSection = pHtmlTemplate.get(initSection);

		if(htmlSection == null)
			return null;

		Template t = new Template(this, htmlSection, initSection, nom);

		pListTemplateItem.get(initSection).add(t);

		return t;		
	}

	public String getHtmlParsed()
	{
		String htmlTemplate = pHtml;
		
		for(String key : pHashVariable.keySet())
		{
			htmlTemplate = htmlTemplate.replace("<VARSUB>" + key + "</VARSUB>", pHashVariable.get(key));		
		}

		return htmlTemplate;
	}

	public boolean haveChild()
	{
		for(String key : pListTemplateItem.keySet())
		{
			if(pListTemplateItem.get(key).size() > 0 )
				return true;			
		}

		return false;
	}

	public String parse()
	{
		String result = getHtmlParsed();
		
		for(String key : pListTemplateItem.keySet())
		{
			String info = "";
			
			for(Template t : pListTemplateItem.get(key))
			{
				if(haveChild())
					info += t.parse();
				else
					return result;

			}

			result = result.replace(pHtmlTemplate.get(key), info);
				
			result = result.replaceAll("<!--[ ]*(BEGIN|END)[ ]*" + pSection + "." + key + "[ ]*-->", "");
			//result = result.replaceAll("<!--[ ]*BEGIN[ ]*" + pSection + "." + key + "[ ]*-->", "");
			//result = result.replaceAll("<!--[ ]*END[ ]*" + pSection + "." + key + "[ ]*-->", "");
		}

		return result;	
	}

	public static void main(String args[]) throws FileNotFoundException, IOException
	{
		Template root = new Template("TEMPLATE.html");
	
		Template t2 = root.initNewSection("TEST", "");

		t2.addVar("TATA", "GOOD JOB!");

		Template th = root.initNewSection("TH", "");
		th.addVar("TITLE", "T");			

		Template rows = root.initNewSection("ROWS", "");
		Template td = rows.initNewSection("TD", "");
		td.addVar("DATA", "connard!");

		//root.parse();
		System.out.println(root.parse());

	} 
}
