/*
 * Copyright (c) 2017 Data and Web Science Group, University of Mannheim, Germany (http://dws.informatik.uni-mannheim.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.unimannheim.wdi.model;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link XMLFormatter} for {@link Author}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class AuthorXMLFormatter extends XMLFormatter<Author> {

	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("authors");
	}

	@Override
	public Element createElementFromRecord(Author record, Document doc) {
		Element Author = doc.createElement("Author");

		Author.appendChild(createTextElement("name", record.getName(), doc));
		return Author;
	}

}
