# ![IKATS Logo](https://ikats.github.io/img/Logo-ikats-icon.png) IKATS Datamodel

![Docker Automated build](https://img.shields.io/docker/automated/ikats/datamodel.svg)
![Docker Build Status](https://img.shields.io/docker/build/ikats/datamodel.svg)
![MicroBadger Size](https://img.shields.io/microbadger/image-size/ikats/datamodel.svg)

**An overview of IKATS global architecture is available [here](https://github.com/IKATS/IKATS)**

IKATS datamodel provides two IKATS web applications :
* the **Ingestion** webapp, deployed in a Tommee server to ingest data in IKATS : cf.[ingestion documentation](https://github.com/IKATS/ikats-datamodel/tree/master/ikats-ingestion)
* the **TemporalDataManager** webapp, deployed in a Tomcat server, which is describer here.

The **TemporalDataManager** webapp provides access to following IKATS resources:

On PostgreSQL database:

* Metadata
* Dataset (set of time series)
* Table
* MacroOperator
* ProcessData
* Workflow

On OpenTSDB database:

* Time Series

Resources can be accessed through an HTTP API, including IKATS operators dealing with non temporal data.

List of java operators at the moment: (see [python operators](https://github.com/IKATS?q=op-) for other operators provided in IKATS)

## Dataset Preparation

### Import Export

* [Import TS](/doc/operators/importTs.html)
* [Import Metadata](/doc/operators/importMetadata.html)

### Dataset Management

* [Dataset Selection](/doc/operators/datasetSelection.html)
* [Manual Selection](/doc/operators/manualSelection.html)
* [TS Finder](/doc/operators/tsFinder.html)
* [Filter](/doc/operators/filter.html)
* [Merge TS lists](/doc/operators/mergeTsLists.html)
* [Save as a Dataset](/doc/operators/saveAsDataset.html)

## Pre-Processing on Ts

### Transforming

* [Ts2Feature](/doc/operators/ts2Feature.html)
* [Discretize](/doc/operators/discretize.html)

## Processing On Tables

* [Read Table](/doc/operators/readTable.html)
* [TrainTestSplit](/doc/operators/trainTestSplit.html)
* [Merge Tables](/doc/operators/mergeTables.html)
* [Population Selection](/doc/operators/populationSelection.html)
