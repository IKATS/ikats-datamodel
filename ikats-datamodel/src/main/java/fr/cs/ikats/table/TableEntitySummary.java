/**
 * Copyright 2018-2019 CS Systèmes d'Information
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cs.ikats.table;


import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity that gets only summary properties from {@link AbstractTableEntity}.
 * The raw data properties are available through {@link TableEntity}
 */
@Entity
@Table(name = "TableEntity")
public class TableEntitySummary extends AbstractTableEntity {

}
