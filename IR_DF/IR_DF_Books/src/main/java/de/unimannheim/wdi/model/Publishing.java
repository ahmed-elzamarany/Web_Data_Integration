package de.unimannheim.wdi.model;

import java.io.Serializable;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class Publishing extends AbstractRecord<Attribute> implements Serializable {

    private static final long serialVersionUID = 1L;
    private String publisher;
    private String year;

    public Publishing(String identifier, String provenance) {
        super(identifier, provenance);
    }

    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public static final Attribute PUBLISHER = new Attribute("publisher");
    public static final Attribute YEAR = new Attribute("year");

    @Override
    public boolean hasValue(Attribute attribute) {
		if(attribute==YEAR)
			return getYear() != null && !getYear().isEmpty();
		else if(attribute==PUBLISHER)
			return getPublisher() != null && !getPublisher().isEmpty();
		else
			return false;
	}

    @Override
    public int hashCode() {
    	StringBuffer concat = new StringBuffer(publisher);
    	concat.append(year);
    	
    	//create String from concatenated publisher+year
    	String checkConcat = new String(concat);
    	
        int result = 31 + ((checkConcat == null) ? 0 : checkConcat.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Publishing other = (Publishing) obj;
        
        if (publisher == null) {
            if (other.publisher != null)
                return false;
        } 
        else if (year == null) {
        	if (other.year != null)
        		return false;
        }
        else if (!publisher.equals(other.publisher))
            return false;
        else if (!year.equals(other.year))
        	return false;
        
        return true;
    }
}
