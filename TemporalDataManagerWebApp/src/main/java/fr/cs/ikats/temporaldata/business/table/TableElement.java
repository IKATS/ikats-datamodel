/**
 * LICENSE:
 * --------
 * Copyright 2017 CS SYSTEMES D'INFORMATION
 *
 * Licensed to CS SYSTEMES D'INFORMATION under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. CS licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * @author Fabien TORTORA <fabien.tortora@c-s.fr>
 * @author Mathieu BERAUD <mathieu.beraud@c-s.fr>
 */

package fr.cs.ikats.temporaldata.business.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import fr.cs.ikats.temporaldata.business.table.TableInfo.DataLink;
import fr.cs.ikats.temporaldata.exception.IkatsException;

/**
 * The TableElement is a view of the pair (data, link) stored as a cell in the table.
 * The TableElement associates one Object (data) and one DataLink (link).
 *
 * The TableElement is not used in json serialization, but is quite useful, when
 * links are managed. for the Table and TableManager API.
 */
public class TableElement {
    public Object data;
    public DataLink link;

    /**
     * @param data:
     *            any immutable Object (String, subclass of Number, Boolean,
     *            BigInteger, BigDecimal ...).
     * @param link:
     *            optional link. null accepted
     */
    public TableElement(Object data, DataLink link) {
        super();
        this.data = data;
        this.link = link;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String linkStr = link == null ? "null" : link.toString();
        String dataStr = data == null ? "null" : data.toString();
        return "TableElement [data=" + dataStr + ", link=" + linkStr + "]";
    }

    /**
     * Method used to encode the List<TableElement> for the API
     *
     * @param data
     * @param links
     * @return list converted
     * @throws IkatsException
     */
    static List<TableElement> encodeElements(List<Object> data, List<DataLink> links) throws IkatsException {

        if (data == null)
            throw new IkatsException("Failed Table.encodeElements(): at least data must be defined.");
        if (links != null && data.size() != links.size())
            throw new IkatsException("Failed Table.encodeElements(): expected when links not null: links size == data size");

        List<TableElement> resultList = new ArrayList<>();
        Iterator<DataLink> iterLinks = (links != null) ? links.iterator() : null;
        for (Object dataElt : data) {
            DataLink link = (iterLinks != null) ? iterLinks.next() : null;
            resultList.add(new TableElement(dataElt, link));
        }
        return resultList;

    }

    /**
     * Method used to encode the List<TableElement> for the API
     * @param items
     * @return
     * @throws IkatsException
     */
    public static List<TableElement> encodeElements(Object... items) throws IkatsException {

        return encodeElements(Arrays.asList(items));
    }

    /**
     * Method used to encode the List<TableElement> for the API
     *
     * @param list
     * @param links
     * @return list converted
     * @throws IkatsException
     */
    public static List<TableElement> encodeElements(List<Object> list) throws IkatsException {

        if (list == null)
            throw new IkatsException("Failed Table.encodeElements(): at least data must be defined.");

        List<TableElement> resultList = new ArrayList<>();

        for (Object value : list)
            resultList.add(encodeElement(value));

        return resultList;
    }

    /**
     * Method used to encode the List<TableElement> for the API
     * @param value can be immutable object, or TableElement or DataLink or null
     * @return TableElement deduced from the value.
     */
    public static TableElement encodeElement(Object value) {

        if (value == null)
            return new TableElement(null, null);

        if (value instanceof TableElement)
            return (TableElement) value;

        if (value instanceof DataLink)
            return new TableElement(null, (DataLink) value);

        return new TableElement(value, null);
    }
}

