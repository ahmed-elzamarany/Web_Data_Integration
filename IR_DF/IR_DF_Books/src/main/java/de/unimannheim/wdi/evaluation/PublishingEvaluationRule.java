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
package de.unimannheim.wdi.evaluation;

import de.unimannheim.wdi.model.Books;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

/**
 * {@link EvaluationRule} for the titles of {@link Books}s. The rule simply
 * compares the titles of two {@link Books}s and returns true, in case their
 * similarity based on {@link TokenizingJaccardSimilarity} is 1.0.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class PublishingEvaluationRule extends EvaluationRule<Books, Attribute> {

	@Override
	public boolean isEqual(Books record1, Books record2, Attribute schemaElement) {
		if(record1.getPublishing().getYear()== null && record2.getPublishing().getYear()==null)
			return true;
		else if(record1.getPublishing().getYear()== null ^ record2.getPublishing().getYear()==null)
			return false;
		else 
			return record1.getPublishing().getYear().equals(record2.getPublishing().getYear());
	}

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.datafusion.EvaluationRule#isEqual(java.lang.Object, java.lang.Object, de.uni_mannheim.informatik.wdi.model.Correspondence)
	 */
	@Override
	public boolean isEqual(Books record1, Books record2,
			Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}
	
	
}
