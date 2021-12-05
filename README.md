# Web_Data_Integration_Project

## Project Description

The objective of our project work is to create an integrated dataset consisting of several attributes related to books. This task can have several use cases, with the most straightforward one being enacting a catalog of books, similar to those maintained by several National Libraries. Other use cases may include building a content-based recommender system or observing pricing attributes across different platforms where the respective books are sold. Relevant data is widely available across various platforms, such as Google Books, Goodreads, or Amazon. As book data is actively maintained, we can exploit many other data sources should unexpected issues arise during data integration.

## Schema and Basic Profile of each Data Set

In this project, 3 datasets are being dealt with. All in csv format.


### 1. [Kindle dataset](https://www.kaggle.com/snathjr/kindle-books-dataset)
This dataset contains 49197 entities. 

### 2. [Goodreads dataset](https://www.kaggle.com/meetnaren/goodreads-best-books)
This dataset contains 27159 entities. 

### 3. [Book recommendation dataset](https://www.kaggle.com/arashnic/book-recommendation-dataset)
This dataset contains 271360 entities. 


## Target Schema
8 attributes are included in the target schema: title, authors, rating, pages, year, publisher, genres, price.

Example of the target schema in xml format:
```xml
<book>
		<id>book1</id>
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
```

## Files description/ Pipline

### 0- Exploring the data: 
An overview of the datasets and the count of nulls/attributes will be found in the [Exploring the data.ipynb](Notebooks/Exploring%20the%20data.ipynb).

### 1- Data Translation: 
The Data Translation was done using Altova mapfore2021 software. The files can be found in [the Data Translation folder](DT).

### 2- Cleaning the data:
The data was cleaned and examined for duplicates. The nulls in Authors were dropped. More detailes are in [Cleaning.ipynb](Notebooks/Cleaning.ipynb).

### 3- Creating the gold standard between each 2 datasets
In order to create the gold standard by hand, a mixed work between python and manualy looking into the data was used. More details can be found in the [Creating_GS.ipynb](Notebooks/Creating_GS.ipynb)

Note that by running this notebook, you will generate files like _goodreads_recommendation_H.csv_ which are meant to be examined by hand. These files aren't included in the repository.

### 4- Identity Resolution
With the help of winter framework in java, the identity resolution was created and evaluted with the gold standard. The work is included in [IR_Main.java](IR_DF/IR_DF_Books/src/main/java/de/unimannheim/wdi/identity_resolution/IR_Main.java)

### 5- Creating the unified gold standard
The gold standard was created using the correspondences from the identity resolution and going manualy through the data. More details can be found in the [GSxml.ipynb](Notebooks/GSxml.ipynb)

### 6- Data Fusion
With the help of winter framework in java, the 3 datasets were merged into a single xml file and evaluted with the gold standard. The work is included in [DataFusion_Main.java](IR_DF/IR_DF_Books/src/main/java/de/unimannheim/wdi/data_fusion/DataFusion_Main.java)
