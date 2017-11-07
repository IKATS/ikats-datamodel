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
 * 
 */

package fr.cs.ikats.common.dao.exception;

/**
 * Subclass of IkatsDaoException: exceptions raised when a ressource is missing. 
 */
public class IkatsDaoMissingRessource extends IkatsDaoException {
 
    /**
     * 
     */
    private static final long serialVersionUID = -6925941034613818210L;

    /**
     * Subclass of IkatsDaoException: error raised when a ressource was not found
     */
    public IkatsDaoMissingRessource() { 
    }

    /**
     * @param message
     */
    public IkatsDaoMissingRessource(String message) {
        super(message); 
    }

    /**
     * @param cause
     */
    public IkatsDaoMissingRessource(Throwable cause) {
        super(cause); 
    }

    /**
     * @param message
     * @param cause
     */
    public IkatsDaoMissingRessource(String message, Throwable cause) {
        super(message, cause); 
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public IkatsDaoMissingRessource(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}

