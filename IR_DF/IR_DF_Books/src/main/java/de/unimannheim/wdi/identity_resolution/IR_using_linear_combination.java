package de.unimannheim.wdi.identity_resolution;

import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.model.*;
import de.unimannheim.wdi.Blocking.BooksBlockingKeyByTitleGenerator;
import de.unimannheim.wdi.Comparators.*;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import de.unimannheim.wdi.model.Author;
import de.unimannheim.wdi.model.BookXMLReader;
import de.unimannheim.wdi.model.Books;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;

public class IR_using_linear_combination
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
	public static String IR(String name1, String name2, Comparator<Books, Attribute>[]comparators,double[]weights) throws Exception {
		// loading data
		logger.info("*\tLoading datasets\t*");
		HashedDataSet<Books, Attribute> data1 = new HashedDataSet<>();
		new BookXMLReader().loadFromXML(new File("data/input/"+name1+".xml"), "/books/book", data1);
		HashedDataSet<Books, Attribute> data2 = new HashedDataSet<>();
		new BookXMLReader().loadFromXML(new File("data/input/"+name2+".xml"), "/books/book", data2);

		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/gs_"+name1+"_"+name2+".csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Books, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.7);
		matchingRule.activateDebugReport("data/identity_output/debugResultsMatchingRule.csv", 1000, gsTest);

		// add comparators
		for (int i=0;i<comparators.length;i++) {
			matchingRule.addComparator(comparators[i], weights[i]);
		}



		// create a blocker (blocking strategy)
		StandardRecordBlocker<Books, Attribute> blocker = new StandardRecordBlocker<Books, Attribute>(new BooksBlockingKeyByTitleGenerator());
//		NoBlocker<Books, Attribute> blocker = new NoBlocker<>();
//		SortedNeighbourhoodBlocker<Movie, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByTitleGenerator(), 1);
		blocker.setMeasureBlockSizes(true);
		//Write debug results to file:
		blocker.collectBlockSizeData("data/identity_output/debugResultsBlocking.csv", 100);

		// Initialize Matching Engine
		MatchingEngine<Books, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		logger.info("*\tRunning identity resolution\t*");
		Processable<Correspondence<Books, Attribute>> correspondences = engine.runIdentityResolution(
				data1, data2, null, matchingRule,
				blocker);

		// Create a top-1 global matching
//		  correspondences = engine.getTopKInstanceCorrespondences(correspondences, 1, 0.0);

//		 Alternative: Create a maximum-weight, bipartite matching
//		 MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
//		 maxWeight.run();
//		 correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/correspondences/"+name1+"_"+name2+"_correspondences.csv"), correspondences);

		logger.info("*\tEvaluating result\t*");
		// evaluate your result
		MatchingEvaluator<Books, Attribute> evaluator = new MatchingEvaluator<Books, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);

		// print the evaluation result
		logger.info(name1+" <-> "+name2);
		String p=String.format(" Precision: %.4f",perfTest.getPrecision());
		String r=String.format(" Recall: %.4f",	perfTest.getRecall());
		String f=String.format(" F1: %.4f",perfTest.getF1());
		logger.info(p);
		logger.info(r);
		logger.info(f);
		return p+r+f;

	}
    public static void main( String[] args ) throws Exception
    {

    	// Over all, the compartores proved better if they are lower cased
//		String rk1=IR("recommendation","kindle",new Comparator[]{new BookTitleComparatorEqual(),new BookAuthorComparatorLevenshtein(), new BookPublisherComparatorLevenshtein()}, new double[]{0.5, 0.2, 0.3});//  Precision: 0.6188 Recall: 1.0000 F1: 0.7646
		String rk2=IR("recommendation","kindle",new Comparator[]{new BookTitleComparatorEqual(),new BookAuthorComparatorLevenshtein(), new BookPublisherComparatorLevenshtein()}, new double[]{0.4, 0.2, 0.4}); // Precision: 0.9725 Recall: 0.8023 F1: 0.8792
//		String rk3=IR("recommendation","kindle",new Comparator[]{new BookTitleComparatorEqual(),new BookAuthorComparatorJaccard(), new BookPublisherComparatorJaccard()}, new double[]{0.2, 0.2, 0.6}); //  Precision: 0.9942 Recall: 0.7841 F1: 0.8767
//
//		System.out.println(rk1);
//		System.out.println(rk2);
//		System.out.println(rk3);

		// We can see the best for recommendation and kindle datasets is while using these comparators with these wights {BookTitleComparatorEqual 0.4, BookAuthorComparatorLevenshtein 0.2, BookPublisherComparatorLevenshtein, 0.4]


//		String gk1=IR("goodreads","kindle",new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorJaccard(),new BookPageComparator100Page()}, new double[]{0.6, 0.2,0.2}); // Precision: 0.6809 Recall: 0.9940 F1: 0.8082
//		String gk2=IR("goodreads","kindle",new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein(),new BookPageComparator100Page()}, new double[]{0.7, 0.2,0.1}); // Precision: 0.3696 Recall: 1.0000 F1: 0.5397
		String gk3=IR("goodreads","kindle",new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein(),new BookPageComparator50Page()}, new double[]{0.5, 0.4,0.1}); // Precision: 0.9878 Recall: 0.9819 F1: 0.9848

//		System.out.println(gk1);
//		System.out.println(gk2);
//		System.out.println(gk3);

		// We can see the best for goodreads and kindle datasets is while using these comparators with these wights {BookTitleComparatorEqual 0.5, BookAuthorComparatorLevenshtein 0.4, BookPageComparator50Page, 0.1]
//		String rg1=IR("recommendation","goodreads",new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorJaccard()}, new double[]{0.5, 0.5});//  Precision: 0.9331 Recall: 0.8040 F1: 0.8637
//		String rg2=IR("recommendation","goodreads",new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein()}, new double[]{0.7, 0.3});//  Precision: 0.3648 Recall: 1.0000 F1: 0.5346
		String rg3=IR("recommendation","goodreads",new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein()}, new double[]{0.6, 0.4}); // Precision: 0.9501 Recall: 0.9094 F1: 0.9293

//		System.out.println(rg1);
//		System.out.println(rg2);
//		System.out.println(rg3);
		// We can see the best for goodreads and recommendation datasets is while using these comparators with these wights {BookTitleComparatorEqual 0.6, BookAuthorComparatorLevenshtein 0.4]

	}
}
