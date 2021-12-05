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

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link XMLMatchableReader} for {@link Books}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class BookXMLReader extends XMLMatchableReader<Books, Attribute> implements FusibleFactory<Books, Attribute>  {

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.model.io.XMLMatchableReader#initialiseDataset(de.uni_mannheim.informatik.wdi.model.DataSet)
	 */
	@Override
	protected void initialiseDataset(DataSet<Books, Attribute> dataset) {
		super.initialiseDataset(dataset);
		
		// the schema is defined in the Movie class and not interpreted from the file, so we have to set the attributes manually
		dataset.addAttribute(Books.TITLE);
		dataset.addAttribute(Books.AUTHORS);
		dataset.addAttribute(Books.RATING);
		dataset.addAttribute(Books.PAGES);
		dataset.addAttribute(Books.PRICE);
		dataset.addAttribute(Books.LANGUAGE);
		dataset.addAttribute(Books.ISBN);
		dataset.addAttribute(Books.YEAR);
		dataset.addAttribute(Books.PUBLISHER);
		dataset.addAttribute(Books.GENRES);
		dataset.addAttribute(Books.PUBLISHING);
	}
	
	@Override
	public Books createModelFromElement(Node node, String provenanceInfo) {
		String id = getValueFromChildElement(node, "id");

		// create the object with id and provenance information
		Books book = new Books(id, provenanceInfo);

		// fill the attributes
		// TODO  also parses non-int values
		book.setTitle(getValueFromChildElement(node, "title"));
		book.setRating(getValueFromChildElement(node, "rating"));
		book.setPages(getValueFromChildElement(node, "pages"));
		book.setPrice(getValueFromChildElement(node, "price"));
		book.setLanguage(getValueFromChildElement(node, "language"));
		book.setPublisher(getValueFromChildElement(node, "publisher"));
		book.setIsbn(getValueFromChildElement(node, "isbn"));
		book.setYear(getValueFromChildElement(node, "year"));
		book.setGenres(getListFromChildElement(node, "genres"));
		
		Publishing pub = new Publishing(id, provenanceInfo);
		pub.setPublisher(getValueFromChildElement(node, "publisher"));
		pub.setYear(getValueFromChildElement(node, "year"));
		book.setPublishing(pub);
		
		//System.out.println(book.getPublishing().getYear() + " ---- " + book.getPublishing().getPublisher());

		// load the list of authors
		List<Author> authors = getObjectListFromChildElement(node, "authors",
				"author", new AuthorXMLReader(), provenanceInfo);
		book.setAuthors(authors);
		
		return book;
	}

	@Override
	public Books createInstanceForFusion(RecordGroup<Books, Attribute> cluster) {
		List<String> ids = new LinkedList<>();
		
		// collect the ids of all records that are fused in this group
		for (Books b : cluster.getRecords()) {
			ids.add(b.getIdentifier());
		}
		
		// sort and merge the ids to create an id for the fused record
		Collections.sort(ids);
		
		String mergedId = StringUtils.join(ids, '+');
		
		// create the new fused record
		return new Books(mergedId, "fused");
		
	}

}
