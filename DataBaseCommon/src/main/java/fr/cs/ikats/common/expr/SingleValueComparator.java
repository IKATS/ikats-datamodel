package fr.cs.ikats.common.expr;

import fr.cs.ikats.common.dao.exception.IkatsDaoInvalidValueException;

/**
 * Enum defining comparators dealing with the same type of value on both operand sides, left and right.
 * 
 */
public enum SingleValueComparator {
    EQUAL("="),
    NEQUAL("!="),
    LT("<"),
    GT(">"),
    LE("<="),
    GE(">="),
    LIKE("like"),
    NLIKE("not like"),
    IN("in"),
    NIN("not in") ;

    final private String text; 
    SingleValueComparator(String aText) {
        text = aText;
    }
    public String getText()
    {
        return text;
    }
    
    /**
     * Usefully parses a string into matching SingleValueComparator value
     * @param aText the text which is defining the  SingleValueComparator value
     * @return the parsed SingleValueComparator value
     * @throws IkatsException: failed to retrieve any matching SingleValueComparator
     */
    public static final SingleValueComparator parseComparator( String aText ) throws IkatsDaoInvalidValueException
    { 
        SingleValueComparator parsed =null;
        
        for (SingleValueComparator currentOper :  SingleValueComparator.values()) {
            if ( currentOper.getText().equals( aText ) )
            {
                parsed=currentOper;
            }
        }
        if ( parsed != null )
        {
            return parsed;   
        }
        else
        {
            throw new IkatsDaoInvalidValueException( "Unexpected text for Comparator: " + aText );
        }
    }
}