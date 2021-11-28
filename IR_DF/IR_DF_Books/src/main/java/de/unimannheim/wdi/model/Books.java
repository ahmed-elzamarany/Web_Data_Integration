package de.unimannheim.wdi.model;

import java.util.*;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import org.apache.commons.lang3.StringUtils;

public class Books extends AbstractRecord<Attribute>  implements Matchable, Fusible<Attribute> {

    protected String id;
    private Map<Attribute, Collection<String>> provenance = new HashMap<>();
    private int isbn;
    private String title;
    private List<Author> authors;
    private int rating;
    private int pages;
    private double price;
    private String language;
    private String year;
    private String publisher;
    public static final Attribute TITLE = new Attribute("title");
    public static final Attribute PUBLISHER = new Attribute("publisher");
    public static final Attribute YEAR = new Attribute("year");
    public static final Attribute AUTHORS = new Attribute("authors");
    public Books(String identifier, String provenance) {
        super(identifier, provenance);
        id = identifier;
        authors = new LinkedList<Author>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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

    public String getMergedAttributeProvenance(Attribute attribute) {
        Collection<String> prov = provenance.get(attribute);

        if (prov != null) {
            return StringUtils.join(prov, "+");
        } else {
            return "";
        }
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
    public void setAttributeProvenance(Attribute attribute,
                                       Collection<String> provenance) {
        this.provenance.put(attribute, provenance);
    }


    @Override
    public boolean hasValue(Attribute attribute) {
        if(attribute==TITLE)
            return getTitle() != null && !getTitle().isEmpty();
        else if(attribute==PUBLISHER)
            return getPublisher() != null && !getPublisher().isEmpty();
        else if(attribute==YEAR)
            return getYear() != null&& !getYear().isEmpty();
        else if(attribute==AUTHORS)
            return getAuthors() != null && getAuthors().size() > 0;
        else
            return false;
    }
}
