<!--
SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>
SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>

SPDX-License-Identifier: Apache-2.0
-->

# _OOPS!_ - OntOlogy Pitfall Scanner

[![License: Apache-2.0](
    https://img.shields.io/badge/License-Apache--2.0-blue.svg)](
    LICENSE.txt)
[![REUSE status](
    https://api.reuse.software/badge/github.com/oeg-upm/OOPS)](
    https://api.reuse.software/info/github.com/oeg-upm/OOPS)
[![Repo](
    https://img.shields.io/badge/Repo-GitHub-555555&logo=github.svg)](
    https://github.com/oeg-upm/OOPS)
[![Build Status](
    https://github.com/oeg-upm/OOPS/workflows/build/badge.svg)](
    https://github.com/oeg-upm/OOPS/actions)

[![In cooperation with Open Source Ecology Germany](
    https://raw.githubusercontent.com/osegermany/tiny-files/master/res/media/img/badge-oseg.svg)](
    https://opensourceecology.de)

_OOPS!_ is a [linter] for [RDF]/[OWL] [Ontologies][Ontology].  The software is accessible at <https://oops.linkeddata.es>.

## Building

```shell
# To create JAR/WAR
mvn package
```

## Testing

To run unit-, doc- and integration-tests:

```shell
# To run unit-tests
mvn test
```

## Deploying the webapp

Build the WAR as explained in Building.

Install Tomcat10 and give yourself access to the manager GUI by adding the
following line to `/etc/tomcat10/tomcat-users.xml`:

```xml
<user username="admin" password="<your-password>" roles="manager-gui"/>
```

Start Tomcat 10 for example with:

```shell
systemctl start tomcat10
```

Browse to <http://localhost:8080/manager>.

In "WAR file to deploy", select the WAR file that you've just built to upload
and choose Deploy.  In "Applications" you can click on
[/oops-2.0.0-SNAPSHOT](http://localhost:8080/oops-2.0.0-SNAPSHOT/) to see the
application running.

## Testing the CLI

How to setup OOPS on your local machine:

```shell
git clone https://github.com/oeg-upm/OOPS.git
cd OOPS
mvn package
```

How to run OOPS on the ValueFlows and OKH ontologies:

```shell
mvn exec:java -Dexec.args="--ontology-url=https://w3id.org/valueflows/ont/vf.ttl"
mvn exec:java -Dexec.args="--ontology-url=https://w3id.org/oseg/ont/okh.ttl"
```

## Testing the REST web service

Use the XML file below as `example-request.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<OOPSRequest>
  <OntologyUrl></OntologyUrl>
  <OntologyContent><![CDATA[
  <?xml version="1.0"?>
  <rdf:RDF
  xmlns="http://www.cc.uah.es/ie/ont/learning-resources#"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
  xmlns:owl="http://www.w3.org/2002/07/owl#"
  xml:base="http://www.cc.uah.es/ie/ont/learning-resources">
  <owl:Ontology rdf:about="">
  <rdfs:comment xml:lang="en">An upper ontology for models of</rdfs:comment>
  </owl:Ontology>
  <owl:Class rdf:ID="LearningObject">
  <rdfs:comment xml:lang="en">"A digital learning resource"</rdfs:comment>
  </owl:Class>
  <owl:Class rdf:ID="ExerciseLO">
  <rdfs:comment xml:lang="en">"A task, problem, or other effort performed
  to develop or maintain fitness 
  or increase skill:"</rdfs:comment>
  <rdfs:subClassOf rdf:resource="#LearningObject"/>
  </owl:Class>
  <owl:ObjectProperty rdf:ID="partOf">
  <owl:inverseOf>
  <owl:TransitiveProperty rdf:ID="hasPart"/>
  </owl:inverseOf>
  <rdfs:range rdf:resource="#LearningObject"/>
  <rdfs:domain rdf:resource="#LearningObject"/>
  </owl:ObjectProperty>
  <owl:TransitiveProperty rdf:about="#hasPart">
  <rdfs:range rdf:resource="#LearningObject"/>
  <rdfs:domain rdf:resource="#LearningObject"/>
  <rdfs:comment xml:lang="en">Specifies that a LO has as one of its constituent
  another LO.</rdfs:comment>
  <owl:inverseOf rdf:resource="#partOf"/>
  <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#ObjectProperty"/>
  </owl:TransitiveProperty>
  <LearningObject rdf:ID="aLearningObject">
  <hasPart>
  <ExerciseLO rdf:ID="anExerciseLearningObject">
  <partOf rdf:resource="#aLearningObject"/>
  </ExerciseLO>
  </hasPart>
  <partOf>
  <LearningObject rdf:ID="yetAnotherLearningObject">
  <hasPart rdf:resource="#aLearningObject"/>
  </LearningObject>
  </partOf>
  </LearningObject>
  <LearningObject rdf:ID="anotherLearningObject"/>
  </rdf:RDF>
  ]]></OntologyContent>
  <Pitfalls>10</Pitfalls>
  <OutputFormat>RDF/XML</OutputFormat>
</OOPSRequest>
```

Run this on the webserver with the following command:

```shell
curl -X POST http://localhost:8080/oops-2.0.0-SNAPSHOT/rest -H "Content-Type: application/xml" -d @example-request.xml
```

The answer should be:

```xml
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oops="http://oops.linkeddata.es/def#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#">
  <owl:Class rdf:about="http://oops.linkeddata.es/def#warning"/>
  <owl:Class rdf:about="http://oops.linkeddata.es/def#pitfall"/>
  <owl:Class rdf:about="http://oops.linkeddata.es/def#suggestion"/>
  <oops:response rdf:about="http://oops.linkeddata.es/data/576052e7-1475-47da-a959-0c2cb334a08a">
    <oops:hasPitfall>
      <oops:pitfall rdf:about="http://oops.linkeddata.es/data/67449ee5-b7fe-4ea1-9601-ce3c43c72218">
        <oops:hasNumberAffectedElements rdf:datatype="http://www.w3.org/2001/XMLSchema#integer"
        >1</oops:hasNumberAffectedElements>
        <oops:hasImportanceLevel>Important</oops:hasImportanceLevel>
        <oops:hasDescription>The ontology lacks disjoint axioms between classes or between properties that should be defined as disjoint. This pitfall is related with the guidelines provided in [6], [2] and [7].	</oops:hasDescription>
        <oops:hasName>Missing disjointness</oops:hasName>
        <oops:hasCode>P10</oops:hasCode>
      </oops:pitfall>
    </oops:hasPitfall>
  </oops:response>
</rdf:RDF>
```

## Similar projects

- [Eyeball]

[linter]: https://en.wikipedia.org/wiki/Lint_(software)
[Eyeball]: https://codeberg.org/elevont/eyeball
[RDF]: https://en.wikipedia.org/wiki/Resource_Description_Framework
[OWL]: https://en.wikipedia.org/wiki/Web_Ontology_Language
[Ontology]: https://en.wikipedia.org/wiki/Ontology_(information_science)
