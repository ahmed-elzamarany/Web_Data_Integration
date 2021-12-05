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
 * {@link XMLFormatter} for {@link Books}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class BookXMLFormatter extends XMLFormatter<Books> {

	AuthorXMLFormatter authorFormatter = new AuthorXMLFormatter();

	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("books");
	}

	@Override
	public Element createElementFromRecord(Books record, Document doc) {
		Element book = doc.createElement("book");

		book.appendChild(createTextElement("id", record.getIdentifier(), doc));
		//book.appendChild(createTextElement("isbn", record.getIsbn(), doc));
		book.appendChild(createTextElementWithProvenance("title",
				record.getTitle(),
				record.getMergedAttributeProvenance(Books.TITLE), doc));

		book.appendChild(createTextElementWithProvenance("rating",
				record.getRating(),
				record.getMergedAttributeProvenance(Books.RATING), doc));
		
		book.appendChild(createTextElementWithProvenance("pages",
				record.getPages(),
				record.getMergedAttributeProvenance(Books.PAGES), doc));
		
		book.appendChild(createTextElementWithProvenance("price",
				record.getPrice(),
				record.getMergedAttributeProvenance(Books.PRICE), doc));
		
		if (record.getPublishing().getPublisher().equals("")) {
			book.appendChild(createTextElementWithProvenance("publisher",
					record.getPublisher(),
					record.getMergedAttributeProvenance(Books.PUBLISHER), doc));
		}
		else {
			book.appendChild(createTextElementWithProvenance("publisher",
					record.getPublishing().getPublisher(),
					record.getMergedAttributeProvenance(Books.PUBLISHING), doc));
		}
		
		if (record.getPublishing().getYear().equals("")) {
			book.appendChild(createTextElementWithProvenance("year", 
					record.getYear(),
					record.getMergedAttributeProvenance(Books.YEAR), doc));
		}
		else {
			book.appendChild(createTextElementWithProvenance("year",
					record.getPublishing().getYear(),
					record.getMergedAttributeProvenance(Books.PUBLISHING), doc));
		}
		/*
		
		book.appendChild(createTextElementWithProvenance("publisherPublishing",
				record.getPublishing().getPublisher(),
				record.getMergedAttributeProvenance(Books.PUBLISHING), doc));
		
		book.appendChild(createTextElementWithProvenance("yearPublishing",
				record.getPublishing().getYear(),
				record.getMergedAttributeProvenance(Books.PUBLISHING), doc));
		
		book.appendChild(createTextElementWithProvenance("publisher",
				record.getPublisher(),
				record.getMergedAttributeProvenance(Books.PUBLISHER), doc));
		book.appendChild(createTextElementWithProvenance("year", record
				.getYear(), record
				.getMergedAttributeProvenance(Books.YEAR), doc));
		*/
		
		
		book.appendChild(createAuthorsElement(record, doc));
		book.appendChild(createGenresElement(record, doc));
		
		

		return book;
	}

	protected Element createTextElementWithProvenance(String name,
			String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

	protected Element createAuthorsElement(Books record, Document doc) {
		Element authorRoot = authorFormatter.createRootElement(doc);
		authorRoot.setAttribute("provenance",
				record.getMergedAttributeProvenance(Books.AUTHORS));

		for (Author a : record.getAuthors()) {
			authorRoot.appendChild(authorFormatter
					.createElementFromRecord(a, doc));
		}

		return authorRoot;
	}

	protected Element createGenresElement(Books record, Document doc) {
		//Element genresRoot = authorFormatter.createRootElement(doc);
		Element genresRoot = doc.createElement("genres");
		
		genresRoot.setAttribute("provenance",
				record.getMergedAttributeProvenance(Books.GENRES));

		for (String gt : record.getGenres()) {
			genresRoot.appendChild(createTextElement("genre_type", gt, doc));
		}

		return genresRoot;
	}

	
}
