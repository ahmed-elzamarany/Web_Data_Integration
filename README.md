# Web_Data_Integration_Project

## Project Description

The objective of our project work is to create an integrated dataset consisting of several attributes related to books. This task can have several use cases, with the most straightforward one being enacting a catalog of books, similar to those maintained by several National Libraries. Other use cases may include building a content-based recommender system or observing pricing attributes across different platforms where the respective books are sold. Relevant data is widely available across various platforms, such as Google Books, Goodreads, or Amazon. As book data is actively maintained, we can exploit many other data sources should unexpected issues arise during data integration.

## Schema and Basic Profile of each Data Set

In this project, 3 datasets are being dealt with. All in csv format.


### 1.Kindle dataset [a link](https://www.kaggle.com/snathjr/kindle-books-dataset)
This dataset contains 49197 entities. 

### 2.Goodreads dataset [a link](https://www.kaggle.com/meetnaren/goodreads-best-books)
This dataset contains 27159 entities. 

### 3.Book_recommendation dataset [a link](https://www.kaggle.com/arashnic/book-recommendation-dataset)
This dataset contains 271360 entities. 


## Target Schema
8 attributes are included in the target schema: title, authors, rating, pages, year, publisher, genres, price.

Example of the target schema in xml format:

<book>
		<id>book1</id>
		<isbn>098765432x</isbn>
		<title>bookTtitle</title>
		<authors> 
			<author>
				<name>author</name>
			</author>
		</authors>
		<rating>4</rating>
		<pages>0</pages>
		<year>1000</year>
		<publisher>xyz</publisher>
		<genres>
				<genre_type>comedy</genre_type>
		</genres>
		<price>15</price>
		<language> english </language>
	</book>

## Files description/ Pipline

### 1- Exploring the data: 
an overview of the datasets and the count of nulls/attributes will be found in the [Exploring the data.ipynb relative link](Notebooks/Exploring%20the%20data.ipynb)

