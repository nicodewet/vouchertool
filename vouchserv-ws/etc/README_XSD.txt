
	== XSD DESIGN DECISIONS == 
	
	== Versioning == 
	
	There are four versioning approaches, and we are selecting the first. 
	
	The options for identifying a new schema version are to:
	
	1. Change the (internal) schema version attribute.
	2. Create a schemaVersion attribute on the root element.
	3. Change the schema's targetNamespace.
	4. Change the name/location of the schema.
	
	Advantages of option 1.
	
	* Easy. Part of the schema specification.
	* Instance documents would not have to change if they remain valid with the new version of the schema 
	  (case 2 above).
	* The schema contains information that informs applications that it has changed.
	* An application could interrogate the version attribute, recognize that this is a new version of the 
	  schema, and take appropriate action.
	
	Disadvantages of option 1.
	
	* The validator ignores the version attribute. Therefore, it is not an enforceable constraint.
	
	Reference: http://www.xfront.com/Versioning.pdf
	