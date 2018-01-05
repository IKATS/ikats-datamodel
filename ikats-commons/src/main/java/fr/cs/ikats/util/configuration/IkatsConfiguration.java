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
 */

package fr.cs.ikats.util.configuration;

/**
 * <p>
 * Default implementation for {@link AbstractIkatsConfiguration}<br>
 * Any new Ikats configuration property set (mainly provided with a file) could be provided with a new instance of that class<br>
 * </p>
 * <p>Example :<br>
 * <code>
 * private IkatsConfiguration<ApplicationConfiguration> config = new IkatsConfiguration<ApplicationConfiguration>(ApplicationConfiguration.class);
 * </code><br><br>
 * With <code>ApplicationConfiguration</code> as an implementation of {@link ConfigProperties}
 * </p>
 *
 * @param <T> as an {@link Enum} implementing {@link ConfigProperties}
 */
public class IkatsConfiguration<T extends Enum<T> & ConfigProperties> extends AbstractIkatsConfiguration<T> {

    public IkatsConfiguration(Class<T> configPropertiesClazz) {
        super.init(configPropertiesClazz);
    }

}
	

