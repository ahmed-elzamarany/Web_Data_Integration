package de.unimannheim.wdi.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class Books extends AbstractRecord<Attribute> implements Serializable {
    
	public Books(String identifier, String provenance) {
    	super(identifier, provenance);
        authors = new LinkedList<>();
    }
	private static final long serialVersionUID = 1L;
    private String isbn;
    private String title;
    private List<Author> authors;
    private String rating;
    private String pages;
    private String price;
    private String language;
    private String year;
    private String publisher;
    private List<String> genres;


    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String getIdentifier() {
        return id;
    }


	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}
	

	private Map<Attribute, Collection<String>> provenance = new HashMap<>();
	private Collection<String> recordProvenance;

	public void setRecordProvenance(Collection<String> provenance) {
		recordProvenance = provenance;
	}

	public Collection<String> getRecordProvenance() {
		return recordProvenance;
	}

	public void setAttributeProvenance(Attribute attribute,
			Collection<String> provenance) {
		this.provenance.put(attribute, provenance);
	}

	public Collection<String> getAttributeProvenance(String attribute) {
		return provenance.get(attribute);
	}

	public String getMergedAttributeProvenance(Attribute attribute) {
		Collection<String> prov = provenance.get(attribute);

		if (prov != null) {
			return StringUtils.join(prov, "+");
		} else {
			return "";
		}
	}


	public static final Attribute ISBN = new Attribute("Isbn");
	public static final Attribute TITLE = new Attribute("Title");
	public static final Attribute RATING = new Attribute("Rating");
	public static final Attribute PAGES = new Attribute("Pages");
	public static final Attribute AUTHORS = new Attribute("Authors");
	public static final Attribute PRICE = new Attribute("Price");
	public static final Attribute LANGUAGE = new Attribute("Language");
	public static final Attribute YEAR = new Attribute("Year");
	public static final Attribute PUBLISHER = new Attribute("Publisher");
	public static final Attribute GENRES = new Attribute("Genres");
	
	
	@Override
	public boolean hasValue(Attribute attribute) {
		if(attribute==TITLE)
			return getTitle() != null && !getTitle().isEmpty();
		else if(attribute==LANGUAGE)
			return getLanguage() != null && !getLanguage().isEmpty();
		else if(attribute==RATING)
			return getRating() != null && !getRating().isEmpty();
		else if(attribute==PAGES)
			return getPages() != null && !getPages().isEmpty();
		else if(attribute==PRICE)
			return getPrice() != null && !getPrice().isEmpty();
		else if(attribute==AUTHORS)
			return getAuthors() != null && getAuthors().size() > 0;
		else if(attribute==GENRES)
			return getGenres() != null && getGenres().size() > 0;
		else if(attribute==ISBN)
			return getIsbn() != null && !getIsbn().isEmpty();
		else if(attribute==YEAR)
			return getYear() != null && !getYear().isEmpty();
		else if(attribute==PUBLISHER)
			return getPublisher() != null && !getPublisher().isEmpty();
		else
			return false;
	}
	@Override
	public String toString() {
		return String.format("[Book %s: %s / %s / %s]", getIdentifier(), getTitle(),
				getRating(), getPages(), getAuthors().toString(), getPrice(), getLanguage());
	}

	@Override
	public int hashCode() {
		return getIdentifier().hashCode();
	}


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Books){
            return this.getIdentifier().equals(((Books) obj).getIdentifier());
        }else
            return false;
    }
	
	


}
