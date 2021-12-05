package de.unimannheim.wdi.data_fusion;

import java.io.File;
import java.util.Iterator;
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
import de.unimannheim.wdi.evaluation.GenresEvaluationRule;
import de.unimannheim.wdi.evaluation.PagesEvaluationRule;
import de.unimannheim.wdi.evaluation.PriceEvaluationRule;
import de.unimannheim.wdi.evaluation.PublisherEvaluationRule;
import de.unimannheim.wdi.evaluation.PublishingEvaluationRule;
import de.unimannheim.wdi.evaluation.RatingEvaluationRule;
import de.unimannheim.wdi.evaluation.TitleEvaluationRule;
import de.unimannheim.wdi.evaluation.YearEvaluationRule;
import de.unimannheim.wdi.fusers.AuthorsFuserIntersection;
import de.unimannheim.wdi.fusers.AuthorsFuserMostRecent;
import de.unimannheim.wdi.fusers.AuthorsFuserUnion;
import de.unimannheim.wdi.fusers.GenresFuserIntersection;
import de.unimannheim.wdi.fusers.PagesFuserFavorSource;
import de.unimannheim.wdi.fusers.PriceFuserLowestPrice;
import de.unimannheim.wdi.fusers.PublisherFuserLongestString;
import de.unimannheim.wdi.fusers.PublishingFuserMostRecentEdition;
import de.unimannheim.wdi.fusers.RatingsFuserFavorSource;
import de.unimannheim.wdi.model.BookXMLFormatter;
import de.unimannheim.wdi.model.BookXMLReader;
import de.unimannheim.wdi.model.Books;
import de.unimannheim.wdi.fusers.TitleFuserLongestString;
import de.unimannheim.wdi.fusers.YearFuserRecentYear;

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
	
    /**
     * @param args
     * @throws Exception
     */
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
		ds1.setScore(2.0);
		ds2.setScore(1.0);
		ds3.setScore(3.0);
		
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
		strategy.addAttributeFuser(Books.PUBLISHING,new PublishingFuserMostRecentEdition(), new PublishingEvaluationRule());
		strategy.addAttributeFuser(Books.AUTHORS,new AuthorsFuserUnion(),new AuthorsEvaluationRule());
		strategy.addAttributeFuser(Books.RATING,new RatingsFuserFavorSource(),new RatingEvaluationRule());
		strategy.addAttributeFuser(Books.PAGES,new PagesFuserFavorSource(),new PagesEvaluationRule());
		strategy.addAttributeFuser(Books.PRICE,new PriceFuserLowestPrice(),new PriceEvaluationRule());
		strategy.addAttributeFuser(Books.YEAR,new YearFuserRecentYear(),new YearEvaluationRule());
		strategy.addAttributeFuser(Books.GENRES,new GenresFuserIntersection(),new GenresEvaluationRule());
		
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
