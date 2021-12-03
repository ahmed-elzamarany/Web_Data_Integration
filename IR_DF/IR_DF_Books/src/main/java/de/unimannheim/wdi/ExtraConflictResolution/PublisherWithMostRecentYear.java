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
package de.unimannheim.wdi.ExtraConflictResolution;

import java.util.Collection;

import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.FusibleValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.unimannheim.wdi.model.Publishing;

/**
 * Longest string {@link ConflictResolutionFunction}: Returns the longest string
 * value
 * 
 * @author Gusztav Megyesi
 * 
 * @param <RecordType>	the type that represents a record
 */
public class PublisherWithMostRecentYear<RecordType extends Matchable & Fusible<SchemaElementType>, SchemaElementType extends Matchable> extends
		ConflictResolutionFunction<Publishing, RecordType, SchemaElementType> {

	@Override
	public FusedValue<Publishing, RecordType, SchemaElementType> resolveConflict(
			Collection<FusibleValue<Publishing, RecordType, SchemaElementType>> values) {
		FusibleValue<Publishing, RecordType, SchemaElementType> highest = null;

		for (FusibleValue<Publishing, RecordType, SchemaElementType> value : values) {
			
			String extractedYear = value.getValue().getYear();
			int convertedYear;
			
			try {
				convertedYear = Integer.parseInt(extractedYear);
			} catch (Exception e) {
				System.out.println("Could not convert yearString to Int for Publishing fusion: " + extractedYear);
				continue;
			}
			
			if (highest == null || convertedYear > Integer.parseInt(highest.getValue().getYear())) {
				highest = value;
			}
		}

		return new FusedValue<>(highest);
	}
}
