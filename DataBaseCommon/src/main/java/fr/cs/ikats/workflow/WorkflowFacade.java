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
 * @author Fabien TORAL <fabien.toral@c-s.fr>
 * @author Fabien TORTORA <fabien.tortora@c-s.fr>
 * @author Mathieu BERAUD <mathieu.beraud@c-s.fr>
 */

package fr.cs.ikats.workflow;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.cs.ikats.common.dao.exception.IkatsDaoConflictException;
import fr.cs.ikats.common.dao.exception.IkatsDaoException;
import fr.cs.ikats.common.dao.exception.IkatsDaoMissingResource;

/**
 * The type Workflow facade.
 */
@Component("WorkflowFacade")
@Scope("singleton")
public class WorkflowFacade {

    /**
     * DAO object to access the Workflow storage
     */
    private WorkflowDAO dao;

    /**
     * Constructor
     */
    public WorkflowFacade() {

        init();
    }

    /**
     * Initialize the DAO
     */
    public void init() {
        dao = new WorkflowDAO();
        dao.init("/workflowHibernate.cfg.xml");
        // TODO analyser 161722 pourquoi pas
        dao.addAnotatedPackage("fr.cs.ikats.workflow");
        dao.addAnnotatedClass(Workflow.class);
        dao.completeConfiguration();
    }

    /**
     * Create Workflow in database
     *
     * @param wf workflow object
     * @return the ID of the inserted data
     * @throws IkatsDaoConflictException create error raised on conflict with another resource
     * @throws IkatsDaoException         another error from DAO
     */
    public Integer persist(Workflow wf) throws IkatsDaoConflictException, IkatsDaoException {
        return dao.persist(wf);
    }

    /**
     * List all workflows
     *
     * @return The list of all workflow
     * @throws IkatsDaoException if there is no workflow
     */
    public List<Workflow> listAllWorkflows() throws IkatsDaoException {
        return dao.listAll(false);
    }

    /**
     * List all Macro Operators
     *
     * @return The list of all Macro Operators
     * @throws IkatsDaoException if there is no Macro Operator
     */
    public List<Workflow> listAllMacroOp() throws IkatsDaoException {
        return dao.listAll(true);
    }

    /**
     * Get a workflow content by providing its id
     *
     * @param id id of the workflow
     * @return the workflow matching this id
     * @throws IkatsDaoMissingResource if there is no workflow matching the id
     * @throws IkatsDaoException        if any other exception occurs
     */
    public Workflow getById(Integer id) throws IkatsDaoMissingResource, IkatsDaoException {
        return dao.getById(id);
    }

    /**
     * Update a workflow with new values
     *
     * @param id          identifier of the workflow (before it changes)
     * @param name        new name to apply to workflow
     * @param description new description of the workflow
     * @param raw         new content of the workflow
     * @return true if the workflow update is successful
     * @throws IkatsDaoConflictException if the new name is already used (not unique)
     * @throws IkatsDaoException         if any other exception occurs
     */
    public boolean update(Integer id, String name, String description, String raw) throws IkatsDaoConflictException, IkatsDaoException {
        Workflow wf = dao.getById(id);
        wf.setName(name);
        wf.setDescription(description);
        wf.setRaw(raw);
        return dao.update(wf);
    }

    /**
     * Update a workflow with new values based on its id
     *
     * @param wf Updated Workflow object
     * @return true if the workflow update is successful
     * @throws IkatsDaoConflictException if the new name is already used (not unique)
     * @throws IkatsDaoException         if any other exception occurs
     */
    public boolean update(Workflow wf) throws IkatsDaoConflictException, IkatsDaoException {
        return dao.update(wf);
    }

    /**
     * Delete a workflow identified by its id
     *
     * @param id identifier of the workflow
     * @throws IkatsDaoException if the workflow couldn't be removed
     */
    public void removeById(Integer id) throws IkatsDaoException {
        dao.removeById(id);
    }

    /**
     * Delete all workflow
     *
     * @throws IkatsDaoException if the workflow couldn't be removed
     */
    public void removeAllWorkflows() throws IkatsDaoException {
        dao.removeAll(false);
    }

    /**
     * Delete all workflow
     *
     * @throws IkatsDaoException if the workflow couldn't be removed
     */
    public void removeAllMacroOp() throws IkatsDaoException {
        dao.removeAll(true);
    }

}

