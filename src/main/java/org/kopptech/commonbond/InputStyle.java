package org.kopptech.commonbond;

public enum InputStyle
{
    /**
     * ignore any input
     */
    NOINPUT,
    
    /**
     * this simply adds or removes from this list based on the IDs. 
     *   must have a tag named "id" bound to a field named "id"
     *   objects are looked up using a DAO
     *   but other fields will only be used for output
     *   (this is for use in combination with other REST services for adding, deleting, and modifying values)
     *   (all input rows must have id field)
     */
    SELECT_BY_ID,
    
    /**
     * process in order
     * if size mismatch throw exception
     */
    EDIT_BY_ORDER,
    
    /**
     * process in order
     * if input has extras, add them
     * if input has fewer than in-memory list, the list is shortened
     */
    HYBRID_BY_ORDER,
    
    /**
     * if size mismatch throw exception
     * if id not found throw exception
     * locate the match by ID in the list and modify contents
     */
    EDIT_BY_ID,
    
    /**
     * if id specified, find in list, load from DAO (if specified) and add to list, or create new and add to list
     * if id not specified, create new, populate, and add
     * leave untouched any existing items that aren't in the input
     */
    HYBRID_BY_ID,
    
    /**
     * DEFAULT!
     * if any in the input list contains IDs, then use HYBRID_BY_ID; else use HYBRID_BY_ORDER
     */
    HYBRID_AUTO
}
