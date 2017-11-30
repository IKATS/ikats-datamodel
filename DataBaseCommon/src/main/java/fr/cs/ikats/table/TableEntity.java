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
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * @author Fabien TORTORA <fabien.tortora@c-s.fr>
 * @author Fabien TORAL <fabien.toral@c-s.fr>
 * 
 */

package fr.cs.ikats.table;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.bytecode.javassist.FieldHandled;
import org.hibernate.bytecode.javassist.FieldHandler;


/**
 * The Table entity
 */
@Entity
@Table(name = "TableEntity")
public class TableEntity implements FieldHandled {

    /**
     * Object necessary for the lazy loading of the big fields content
     */
    @Transient
    private FieldHandler fieldHandler;

    /**
     * Unique identifier allowing to query any table
     */
    @Id
    @SequenceGenerator(name = "table_id_seq", sequenceName = "table_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "table_id_seq")
    @Column(name = "id", updatable = false)
    private int id;

    /**
     * Unique name used to identify the TableEntity by user
     */
    @Column(name = "name", unique = true)
    private String name;

    /**
     * Additional user description for the Table
     */
    @Column(name = "description")
    private String description;

    /**
     * Opaque data containing the 2D-array containing the values of the "cells"
     * including headers as a serialized object
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "rawValues")
    private byte[] rawValues;

    /**
     * Opaque data containing the 2D-array containing the links of the "cells"
     * including headers as a serialized object
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "rawDataLinks")
    private byte[] rawDataLinks;

    /**
     * Boolean indicating if the TableEntity contains columns headers
     */
    @Column(name = "colHeader")
    private boolean colHeader;

    /**
     * Boolean indicating if the TableEntity contains row headers
     */
    @Column(name = "rowHeader")
    private boolean rowHeader;

    /**
     * Creation date of the table
     */
    @Column(name = "created")
    private Date created;


    /**
     * Getter for the id
     *
     * @return the id to get
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for the id
     *
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the name
     *
     * @return the name to get
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the description
     *
     * @return the description to get
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Getter for the rawValues
     *
     * @return the rawValues to get
     */
    public byte[] getRawValues() {
        if (fieldHandler != null) {
            return (byte[]) fieldHandler.readObject(this, "rawValues", rawValues);
        }
        return rawValues;
    }

    /**
     * Setter for the rawValues
     *
     * @param rawValues the rawValues to set
     */
    public void setRawValues(byte[] rawValues) {
        if (fieldHandler != null) {
            fieldHandler.writeObject(this, "rawValues", this.rawValues, rawValues);
            return;
        }
        this.rawValues = rawValues;
    }

    /**
     * Getter for the rawDataLinks
     *
     * @return the rawDataLinks to get
     */
    public byte[] getRawDataLinks() {
        if (fieldHandler != null) {
            return (byte[]) fieldHandler.readObject(this, "rawDataLinks", rawDataLinks);
        }
        return rawDataLinks;
    }

    /**
     * Setter for the rawDataLinks
     *
     * @param rawDataLinks the rawDataLinks to set
     */
    public void setRawDataLinks(byte[] rawDataLinks) {
        if (fieldHandler != null) {
            fieldHandler.writeObject(this, "rawDataLinks", this.rawDataLinks, rawDataLinks);
            return;
        }
        this.rawDataLinks = rawDataLinks;
    }

    /**
     * Getter for the colHeader
     *
     * @return the colHeader to get
     */
    public boolean hasColHeader() {
        return colHeader;
    }

    /**
     * Setter for the colHeader
     *
     * @param colHeader the colHeader to set
     */
    public void setColHeader(boolean colHeader) {
        this.colHeader = colHeader;
    }

    /**
     * Getter for the rowHeader
     *
     * @return the rowHeader to get
     */
    public boolean hasRowHeader() {
        return rowHeader;
    }

    /**
     * Setter for the rowHeader
     *
     * @param rowHeader the rowHeader to set
     */
    public void setRowHeader(boolean rowHeader) {
        this.rowHeader = rowHeader;
    }

    /**
     * Getter for the created date
     *
     * @return the created date to get
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Setter for the creation date
     *
     * @param created the created date to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public void setFieldHandler(FieldHandler handler) {
        this.fieldHandler = handler;
    }

    @Override
    public FieldHandler getFieldHandler() {
        return this.fieldHandler;
    }
}
