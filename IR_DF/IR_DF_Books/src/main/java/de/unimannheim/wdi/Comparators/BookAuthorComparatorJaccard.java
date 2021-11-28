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
package de.unimannheim.wdi.Comparators;

import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;
import de.unimannheim.wdi.model.Author;
import de.unimannheim.wdi.model.Books;

import java.util.List;

/**
 * {@link Comparator} for {@link Books}s based on the
 * {@link Books#getAuthors()} ()} values, and their
 * {@link TokenizingJaccardSimilarity} similarity.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class BookAuthorComparatorJaccard implements Comparator<Books, Attribute> {

	private static final long serialVersionUID = 1L;
	TokenizingJaccardSimilarity sim = new TokenizingJaccardSimilarity();
	
	private ComparatorLogger comparisonLog;
	
	@Override
	public double compare(
			Books record1,
			Books record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		List<Author> s1 = record1.getAuthors();
		List<Author> s2 = record2.getAuthors();
		double similarity=0 ;
		String a1="No Author";
		String a2="No Author";
		for (Author a : s1) {
			for (Author b : s2) {
				double s = sim.calculate(a.getName().toLowerCase(), b.getName().toLowerCase());
				if(similarity<s){
					similarity=s;
					a1=a.getName();
					a2=b.getName();
				}
			}
		}
		// calculate similarity

		// postprocessing
		int postSimilarity = 0;
		if (similarity <= 0.3) {
			postSimilarity = 0;
		}else{
			postSimilarity = (int)Math.round(similarity);

		}


		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(a1);
			this.comparisonLog.setRecord2Value(a2);
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
			this.comparisonLog.setPostprocessedSimilarity(Double.toString(postSimilarity));
		}
		return postSimilarity;
	}

	@Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}

}
