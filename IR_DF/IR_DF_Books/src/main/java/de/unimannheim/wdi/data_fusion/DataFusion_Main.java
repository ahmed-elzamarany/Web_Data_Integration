package de.unimannheim.wdi.data_fusion;

import java.io.File;
import java.util.Locale;

import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroupFactory;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import de.unimannheim.wdi.evaluation.AuthorsEvaluationRule;
import de.unimannheim.wdi.evaluation.PublisherEvaluationRule;
import de.unimannheim.wdi.evaluation.TitleEvaluationRule;
import de.unimannheim.wdi.fusers.AuthorsFuserIntersection;
import de.unimannheim.wdi.fusers.AuthorsFuserMostRecent;
import de.unimannheim.wdi.fusers.AuthorsFuserUnion;
import de.unimannheim.wdi.fusers.PublisherFuserLongestString;
import de.unimannheim.wdi.model.BookXMLFormatter;
import de.unimannheim.wdi.model.BookXMLReader;
import de.unimannheim.wdi.model.Books;
import de.unimannheim.wdi.solution.TitleFuserLongestString;
import org.slf4j.Logger;

public class DataFusion_Main 
{
	/*
	 * Logging Options:
	 * 		default: 	level INFO	- console
	 * 		trace:		level TRACE     - console
	 * 		infoFile:	level INFO	- console/file
	 * 		traceFile:	level TRACE	- console/file
	 *  
	 * To set the log level to trace and write the log to winter.log and console, 
	 * activate the "traceFile" logger as follows:
	 *     private static final Logger logger = WinterLogManager.activateLogger("traceFile");
	 *
	 */

	private static final Logger logger = WinterLogManager.activateLogger("default");
	
    public static void main( String[] args ) throws Exception
    {
		// Load the Data into FusibleDataSet
		logger.info("*\tLoading datasets\n*");
		FusibleDataSet<Books, Attribute> ds1 = new FusibleHashedDataSet<>();
		new BookXMLReader().loadFromXML(new File("data/input/kindle.xml"), "/books/book", ds1);
		ds1.printDataSetDensityReport();

		FusibleDataSet<Books, Attribute> ds2 = new FusibleHashedDataSet<>();
		new BookXMLReader().loadFromXML(new File("data/input/recommendation.xml"), "/books/book", ds2);
		ds2.printDataSetDensityReport();

		FusibleDataSet<Books, Attribute> ds3 = new FusibleHashedDataSet<>();
		new BookXMLReader().loadFromXML(new File("data/input/goodreads.xml"), "/books/book", ds3);
		ds3.printDataSetDensityReport();

		// Maintain Provenance
		// Scores (e.g. from rating)
		ds1.setScore(3.0);
		ds2.setScore(1.0);
		ds3.setScore(2.0);
		
		// Date (e.g. last update)
//		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
//		        .appendPattern("yyyy-MM-dd")
//		        .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
//		        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
//		        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
//		        .toFormatter(Locale.ENGLISH);
//
//		ds1.setDate(LocalDateTime.parse("2012-01-01", formatter));
//		ds2.setDate(LocalDateTime.parse("2010-01-01", formatter));
//		ds3.setDate(LocalDateTime.parse("2008-01-01", formatter));

		// load correspondences
		logger.info("*\tLoading correspondences\t*");
		CorrespondenceSet<Books, Attribute> correspondences = new CorrespondenceSet<>();
		correspondences.loadCorrespondences(new File("data/correspondences/recommendation_goodreads_correspondences.csv"),ds2, ds3);
		correspondences.loadCorrespondences(new File("data/correspondences/goodreads_kindle_correspondences.csv"),ds3, ds1);

		// write group size distribution
		correspondences.printGroupSizeDistribution();

		// load the gold standard 
		DataSet<Books, Attribute> gs = new FusibleHashedDataSet<>();
		new BookXMLReader().loadFromXML(new File("data/goldstandard/gold.xml"), "/books/book", gs);
	
		
		// define the fusion strategy
		DataFusionStrategy<Books, Attribute> strategy = new DataFusionStrategy<>(new BookXMLReader());
		
		// write debug results to file
		strategy.activateDebugReport("data/output/debugResultsDatafusion.csv", 1000, gs);
		
		// add attribute fusers
		strategy.addAttributeFuser(Books.TITLE, new TitleFuserLongestString(),new TitleEvaluationRule());
		strategy.addAttributeFuser(Books.PUBLISHER,new PublisherFuserLongestString(), new PublisherEvaluationRule());
		strategy.addAttributeFuser(Books.AUTHORS,new AuthorsFuserUnion(),new AuthorsEvaluationRule());
		
		// create the fusion engine
		DataFusionEngine<Books, Attribute> engine = new DataFusionEngine<Books, Attribute>(strategy);

		// print consistency report
		engine.printClusterConsistencyReport(correspondences, null);
		
		// run the fusion
		logger.info("*\tRunning data fusion\t*");
		FusibleDataSet<Books, Attribute> fusedDataSet = engine.run(correspondences, null);
		fusedDataSet.printDataSetDensityReport();
		// write the result
		new BookXMLFormatter().writeXML(new File("data/output/fused.xml"), fusedDataSet);

		// evaluate
		logger.info("*\tEvaluating results\t*");
		DataFusionEvaluator<Books, Attribute> evaluator = new DataFusionEvaluator<>(
				strategy, new RecordGroupFactory<Books, Attribute>());
		double accuracy = evaluator.evaluate(fusedDataSet, gs, null);

		logger.info(String.format("Accuracy: %.2f", accuracy));
    }
    
    
}
