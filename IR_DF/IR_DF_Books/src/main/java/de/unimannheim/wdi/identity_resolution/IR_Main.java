package de.unimannheim.wdi.identity_resolution;

import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
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
import de.unimannheim.wdi.model.BookXMLReader;
import de.unimannheim.wdi.model.Books;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;
public class IR_Main
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
	public static String IR_using_linear_combination(String name1, String name2, Comparator<Books, Attribute>[]comparators, double[]weights) throws Exception {

		long startTime =TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
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
		long endTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

		return p+r+f + "No. of correspondences: "+correspondences.size()+" ,time:"+(endTime-startTime);

	}
	public static String IR_using_machine_learning(String name1, String name2, String modelType) throws Exception
	{
		long startTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
	// loading data
		logger.info("*\tLoading datasets\t*");
		HashedDataSet<Books, Attribute> data1 = new HashedDataSet<>();
		new BookXMLReader().loadFromXML(new File("data/input/"+name1+".xml"), "/books/book", data1);
		HashedDataSet<Books, Attribute> data2 = new HashedDataSet<>();
		new BookXMLReader().loadFromXML(new File("data/input/"+name2+".xml"), "/books/book", data2);

		// load the training set
		MatchingGoldStandard gsTraining = new MatchingGoldStandard();
		gsTraining.loadFromCSVFile(new File(
				"data/goldstandard/ML/gs_"+name1+"_"+name2+"_train.csv"));
		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/ML/gs_"+name1+"_"+name2+"_test.csv"));

//		// load the gold standard (test set)
//		logger.info("*\tLoading gold standard\t*");
//		MatchingGoldStandard gsTest = new MatchingGoldStandard();
//		gsTest.loadFromCSVFile(new File("data/goldstandard/gs_"+name1+"_"+name2+".csv"));
// Set class to last attribute
//
//				DataSource source = new DataSource("/some/where/data.csv");
//				Instances data = source.getDataSet();
//		if (data.classIndex() == -1)
//			data.setClassIndex(data.numAttributes() - 1);
//
//// use StratifiedRemoveFolds to randomly split the data
//		StratifiedRemoveFolds filter = new StratifiedRemoveFolds();
//
//// set options for creating the subset of data
//		String[] options = new String[6];
//
//		options[0] = "-N";                 // indicate we want to set the number of folds
//		options[1] = Integer.toString(2);  // split the data into five random folds
//		options[2] = "-F";                 // indicate we want to select a specific fold
//		options[3] = Integer.toString(1);  // select the first fold
//		options[4] = "-S";                 // indicate we want to set the random seed
//		options[5] = Integer.toString(1);  // set the random seed to 1
//
//		filter.setOptions(options);        // set the filter options
//		filter.setInputFormat(data);       // prepare the filter for the data format
//		filter.setInvertSelection(false);  // do not invert the selection
//
//// apply filter for test data here
//		Instances test = Filter.useFilter(data, filter);
//
////  prepare and apply filter for training data here
//		filter.setInvertSelection(true);     // invert the selection to get other data
//		Instances train = Filter.useFilter(data, filter);


		// create a matching rule
		String options[] = new String[] { "-S" };
		WekaMatchingRule<Books, Attribute> matchingRule = new WekaMatchingRule<>(0.7, modelType, options);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", 1000, gsTraining);

		// add comparators
		matchingRule.addComparator(new BookTitleComparatorEqual());
		matchingRule.addComparator(new BookAuthorComparatorJaccard());
		matchingRule.addComparator(new BookAuthorComparatorLevenshtein());
		matchingRule.addComparator(new BookPublisherComparatorJaccard());
		matchingRule.addComparator(new BookPublisherComparatorLevenshtein());
		matchingRule.addComparator(new BookPageComparator100Page());
		matchingRule.addComparator(new BookPageComparator50Page());


		// train the matching rule's model
		logger.info("*\tLearning matching rule\t*");
		RuleLearner<Books, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(data1, data2, null, matchingRule, gsTraining);
		logger.info(String.format("Matching rule is:\n%s", matchingRule.getModelDescription()));

		// create a blocker (blocking strategy)
		StandardRecordBlocker<Books, Attribute> blocker = new StandardRecordBlocker<Books, Attribute>(new BooksBlockingKeyByTitleGenerator());
		//SortedNeighbourhoodBlocker<Movie, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByYearGenerator(), 30);

		blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);

		// Initialize Matching Engine
		MatchingEngine<Books, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		logger.info("*\tRunning identity resolution\t*");
		Processable<Correspondence<Books, Attribute>> correspondences = engine.runIdentityResolution(
				data1, data2, null, matchingRule,
				blocker);
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
		long endTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

		return p+r+f + "No. of correspondences: "+correspondences.size()+" ,time:"+(endTime-startTime);
	}
	public static void main( String[] args ) throws Exception
    {
    	// Over all, the compartores proved better if they are lower cased
		String rkMLs=IR_using_machine_learning("recommendation","kindle","SimpleLogistic");
		String rkMLj=IR_using_machine_learning("recommendation","kindle","J48");
		String rk1=IR_using_linear_combination("recommendation","kindle",
				new Comparator[]{new BookTitleComparatorEqual(),new BookAuthorComparatorLevenshtein(), new BookPublisherComparatorLevenshtein()},
				new double[]{0.5, 0.2, 0.3});//  Precision: 0.6188 Recall: 1.0000 F1: 0.7646
		String rk2= IR_using_linear_combination("recommendation","kindle",
				new Comparator[]{new BookTitleComparatorEqual(),new BookAuthorComparatorLevenshtein(), new BookPublisherComparatorLevenshtein()},
				new double[]{0.4, 0.2, 0.4}); // Precision: 0.9725 Recall: 0.8023 F1: 0.8792
		String rk3=IR_using_linear_combination("recommendation","kindle",
				new Comparator[]{new BookTitleComparatorEqual(),new BookAuthorComparatorJaccard(), new BookPublisherComparatorJaccard()},
				new double[]{0.2, 0.2, 0.6}); //  Precision: 0.9942 Recall: 0.7841 F1: 0.8767




		String gk1=IR_using_linear_combination("goodreads","kindle",
				new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorJaccard(),new BookPageComparator100Page()},
				new double[]{0.6, 0.2,0.2}); // Precision: 0.6809 Recall: 0.9940 F1: 0.8082
		String gk2=IR_using_linear_combination("goodreads","kindle",
				new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein(),new BookPageComparator100Page()},
				new double[]{0.7, 0.2,0.1}); // Precision: 0.3696 Recall: 1.0000 F1: 0.5397
		String gk3= IR_using_linear_combination("goodreads","kindle",
				new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein(),new BookPageComparator50Page()},
				new double[]{0.5, 0.4,0.1}); // Precision: 0.9878 Recall: 0.9819 F1: 0.9848
		String gkMLs=IR_using_machine_learning("goodreads","kindle","SimpleLogistic");
		String gkMLj=IR_using_machine_learning("goodreads","kindle","J48");


		String rg1=IR_using_linear_combination("recommendation","goodreads",
				new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorJaccard()},
				new double[]{0.5, 0.5});//  Precision: 0.9331 Recall: 0.8040 F1: 0.8637
		String rg2=IR_using_linear_combination("recommendation","goodreads",
				new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein()},
				new double[]{0.7, 0.3});//  Precision: 0.3648 Recall: 1.0000 F1: 0.5346
		String rg3= IR_using_linear_combination("recommendation","goodreads",
				new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein()},
				new double[]{0.6, 0.4}); // Precision: 0.9501 Recall: 0.9094 F1: 0.9293
		String rgMLs=IR_using_machine_learning("recommendation","goodreads","SimpleLogistic");
		String rgMLj=IR_using_machine_learning("recommendation","goodreads","J48");

		System.out.println("------------------------recommendation and kindle--------------------------------");
		System.out.println("ML ,SimpleLogistic :" +rkMLs);
		System.out.println("ML ,J48 :" +rkMLj);
		System.out.println("L 0.5 BookTitleComparatorEqual, 0.2 BookAuthorComparatorLevenshtein, 0.3 BookPublisherComparatorLevenshtein :"+rk1);
		System.out.println("L 0.4BookTitleComparatorEqual, 0.2 BookAuthorComparatorLevenshtein, 0.4 BookPublisherComparatorLevenshtein :"+rk2);
		System.out.println("L 0.2 BookTitleComparatorEqual, 0.2 BookAuthorComparatorJaccard, 0.6 BookPublisherComparatorJaccard :"+rk3);
		System.out.println("------------------------goodreads and kindle--------------------------------");
		System.out.println("ML,SimpleLogistic " +gkMLs);
		System.out.println("ML ,J48 :" +gkMLj);
		System.out.println("L 0.6 BookTitleComparatorEqual, 0.2 BookAuthorComparatorJaccard, 0.2 BookPageComparator100Page :"+gk1);
		System.out.println("L 0.7 BookTitleComparatorEqual, 0.2 BookAuthorComparatorLevenshtein, 0.1 BookPageComparator100Page :"+gk2);
		System.out.println("L 0.5 BookTitleComparatorEqual, 0.4 BookAuthorComparatorLevenshtein, 0.1 BookPageComparator50Page :"+gk3);
		System.out.println("------------------------recommendation and goodreads--------------------------------");
		System.out.println("ML,SimpleLogistic " +rgMLs);
		System.out.println("ML ,J48 :" +rgMLj);
		System.out.println("L 0.5 BookTitleComparatorEqual, 0.5 BookAuthorComparatorJaccard :"+rg1);
		System.out.println("L 0.7 BookTitleComparatorEqual, 0.3 BookAuthorComparatorLevenshtein :"+rg2);
		System.out.println("L 0.6 BookTitleComparatorEqual, 0.4 BookAuthorComparatorLevenshtein :"+rg3);

		// Rerun the best models to keep the correspondences:
		IR_using_linear_combination("recommendation","kindle",
				new Comparator[]{new BookTitleComparatorEqual(),new BookAuthorComparatorLevenshtein(), new BookPublisherComparatorLevenshtein()},
				new double[]{0.4, 0.2, 0.4});// Precision: 0.9725 Recall: 0.8023 F1: 0.8792
		IR_using_linear_combination("goodreads","kindle",
				new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein(),new BookPageComparator50Page()},
				new double[]{0.5, 0.4,0.1}); // Precision: 0.9878 Recall: 0.9819 F1: 0.9848
		 IR_using_linear_combination("recommendation","goodreads",
				new Comparator[]{new BookTitleComparatorEqual(), new BookAuthorComparatorLevenshtein()},
				new double[]{0.6, 0.4}); // Precision: 0.9501 Recall: 0.9094 F1: 0.9293
	}
}
