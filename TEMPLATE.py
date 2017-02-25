#!/usr/bin/python

import re

class TemplateItem:
	def __init__(self, parent, htmlFile, html=None, section="ROOT", uniqID="ROOT"):
		if parent is None:
			with open(htmlFile, 'r') as myFile:
				data = myFile.read()
		else:
			data = html		
	
		self.Parent = parent
		self.Html = data
		self.Section = section
		self.UniqID = uniqID
		
		self.ListTemplateItem = {}
		self.HtmlTemplate = {}

		self.initAllAvailableSection()
	

	def initAllAvailableSection(self):
		
		strPattern = r'<!--[ ]*BEGIN[ ]*' + self.Section + r'\.([a-zA-Z0-9]+)[ ]*-->[ ]*(.*?)[ ]*<!--[ ]*END[ ]*' + self.Section + r'\.([a-zA-Z0-9]+)[ ]*-->'
		pattern = re.compile(strPattern, re.MULTILINE|re.DOTALL)

		for m in re.finditer(pattern, self.Html):
			self.ListTemplateItem[m.group(1)] = []
			self.HtmlTemplate[m.group(1)] = m.group(2)
		
		self.HashVariable = {}		

	def AddVar(self, variable, valeur):
		self.HashVariable[variable] = valeur

	def initNewSection(self, initSection, nom):
		htmlSection = self.HtmlTemplate[initSection]

		if htmlSection is None:
			return None
		
		template = TemplateItem(self, None, htmlSection, initSection, nom)
		
		self.ListTemplateItem[initSection].append(template)

		return template

	def GetHtmlParsed(self):
		htmlTemplate = self.Html
		
		for key in self.HashVariable.keys():
			htmlTemplate = htmlTemplate.replace('<VARSUB>' + key + '</VARSUB>', self.HashVariable[key])
				
		return htmlTemplate

	def HaveChild(self):
		for key in self.ListTemplateItem.keys():
			if len(self.ListTemplateItem[key]) > 0 :
				return true

		return false


	def Parse(self):
		result = self.GetHtmlParsed()

		for key in self.ListTemplateItem.keys():
			info = ""
			print "SECTION " + key
			listTemplate = self.ListTemplateItem[key]


			for template in listTemplate:
				if template.HaveChild :
					info = template.Parse()
				else:
					return self.GetHtmlParsed()
		
			result = result.replace(self.HtmlTemplate[key], info)
			
			strPattern = r'<!--[ ]*(BEGIN|END)[ ]*' + self.Section + r'\.' + key + r'[ ]*-->'
			
			pattern = re.compile(strPattern, re.MULTILINE|re.DOTALL)
			
			result = pattern.sub('', result)
			
		return result
		


x = TemplateItem(None, "TEMPLATE.HTML")

x2 = x.initNewSection('TEST', '')

x2.AddVar("TATA", "GOOD JOB!")

print x.Parse()
