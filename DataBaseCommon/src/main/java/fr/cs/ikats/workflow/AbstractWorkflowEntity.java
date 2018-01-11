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
 * @author Fabien TORAL <fabien.toral@c-s.fr>
 * @author Fabien TORTORA <fabien.tortora@c-s.fr>
 * @author Mathieu BERAUD <mathieu.beraud@c-s.fr>
 * 
 */

package fr.cs.ikats.workflow;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The type Workflow.
 */
@MappedSuperclass
public abstract class AbstractWorkflowEntity  {

    @Id
    @SequenceGenerator(name = "workflow_id_seq", sequenceName = "workflow_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workflow_id_seq")
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "isMacroOp")
    private Boolean isMacroOp;

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets macroOp flag.
     *
     * @return the flag indicating if this is a macro operator (true) or not (false)
     */
    public Boolean getMacroOp() {
        return isMacroOp;
    }

    /**
     * Sets macroOp flag.
     *
     * @param macroOp the boolean indicating if the item is a macro operator (true) or not (false)
     */
    public void setMacroOp(Boolean macroOp) {
        isMacroOp = macroOp;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Using Hibernate: advised to implement hashcode: see §13.1.3
     * http://docs.jboss.org/hibernate/orm/3.6/reference/en-US/html_single/#transactions-demarcation
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
    	// - avoid to involve the database key this.id: refer to mentioned doc in javadoc
    	// - do not involve raw: too big ...
    	return (""+ name + isMacroOp + description +"Wkf").hashCode();
    }
}

