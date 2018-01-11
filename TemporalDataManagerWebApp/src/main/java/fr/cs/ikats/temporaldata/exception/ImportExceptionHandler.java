/**
 * LICENSE:
 * --------
 * Copyright 2017-2018 CS SYSTEMES D'INFORMATION
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
 * @author Maxime PERELMUTER <maxime.perelmuter@c-s.fr>
 *
 */

package fr.cs.ikats.temporaldata.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import fr.cs.ikats.datamanager.client.opentsdb.ApiResponse;
import fr.cs.ikats.datamanager.client.opentsdb.ImportResult;

/**
 * Handler converting ImportException object into HTTP Response object
 */
@Provider
public class ImportExceptionHandler implements ExceptionMapper<ImportException> {

    private static Logger logger = Logger.getLogger(ImportExceptionHandler.class);

    @Override
    public Response toResponse(ImportException exception) {
        logger.error("Error handled while importing data", exception);
        Status handledStatus = Status.BAD_REQUEST;
        if (ImportFileNotFoundException.class.isAssignableFrom(exception.getClass())) {
            handledStatus = Status.NOT_FOUND;
        }
        ApiResponse resultatTotal = exception.getImportResult();
        if (resultatTotal == null) resultatTotal = new ImportResult();
        resultatTotal.setSummary(exception.getMessage());

        return Response.status(handledStatus).entity(resultatTotal).build();
    }

    /**
     * constructor
     */
    public ImportExceptionHandler() {
        logger.info("INIT IMPORT EXCEPTION HANDLER");
    }
}
