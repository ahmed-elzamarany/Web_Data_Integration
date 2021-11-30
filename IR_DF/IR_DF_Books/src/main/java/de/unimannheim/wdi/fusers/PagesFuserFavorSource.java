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
package de.unimannheim.wdi.fusers;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.FavourSources;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.unimannheim.wdi.model.Books;

/**
 * {@link AttributeValueFuser} for the director of {@link Movie}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class PagesFuserFavorSource extends AttributeValueFuser<String, Books, Attribute> {

	public PagesFuserFavorSource() {
		super(new FavourSources<String, Books, Attribute>());
	}

	@Override
	public boolean hasValue(Books record, Correspondence<Attribute, Matchable> correspondence) {
		return record.hasValue(Books.PAGES);
	}

	@Override
	public String getValue(Books record, Correspondence<Attribute, Matchable> correspondence) {
		return record.getPages();
	}

	@Override
	public void fuse(RecordGroup<Books, Attribute> group, Books fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		FusedValue<String, Books, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		fusedRecord.setPages(fused.getValue());
		fusedRecord.setAttributeProvenance(Books.PAGES,
				fused.getOriginalIds());
	}

}
