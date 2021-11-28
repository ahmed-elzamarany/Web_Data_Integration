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
import de.unimannheim.wdi.model.Books;

/**
 * {@link Comparator} for {@link Books}s based on the
 * {@link Books#getPublisher()} ()} ()} values, and their
 * {@link TokenizingJaccardSimilarity} similarity.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class BookPublisherComparatorLevenshtein implements Comparator<Books, Attribute> {

	private static final long serialVersionUID = 1L;
	LevenshteinSimilarity sim = new LevenshteinSimilarity();
	
	private ComparatorLogger comparisonLog;
	
	@Override
	public double compare(
			Books record1,
			Books record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		String s1 = record1.getPublisher();
		String s2 = record2.getPublisher();
		if (s1 != null) {
			s1 = s1.toLowerCase();
		}

		if (s2 != null) {
			s2 = s2.toLowerCase();
		}
		// calculate similarity
		double similarity = sim.calculate(s1, s2);

		// postprocessing
		int postSimilarity = 0;
		if (similarity <= 0.3) {
			postSimilarity = 0;
		}else{
			postSimilarity = (int)Math.round(similarity);

		}

		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(s1);
			this.comparisonLog.setRecord2Value(s2);
    	
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
