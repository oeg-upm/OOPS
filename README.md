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
# To create JAR/WAR and running all tests
mvn package

# To create JAR/WAR without running tests
mvn package -Dmaven.test.skip=true
```

## Testing

To run the tests:

```shell
mvn test
```

It is also possible to run a specific test on the command line:

```shell
./run/test-pitfall.sh P02
```

To see all possible tests:

```shell
./run/test-pitfall.sh --help
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

In "WAR file to deploy", select the WAR file that you've just built in
directory `target` to upload and choose Deploy.  In "Applications" you can
click on [/oops-2.0.0-SNAPSHOT](http://localhost:8080/oops-2.0.0-SNAPSHOT/) to
see the application running.

The WAR file may be too large which leads to a failing upload.

You can adjust the maximum value (default 50 MiB) in `/var/lib/tomcat10/webapps/manager/WEB-INF/web.xml`:

```xml
    <multipart-config>
      <!-- 50 MiB max -->
      <max-file-size>209715200</max-file-size>
      <max-request-size>209715200</max-request-size>
      <file-size-threshold>0</file-size-threshold>
    </multipart-config>
```

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
</OOPSRequest>
```

Run this on the webserver with the following command:

```shell
curl -X POST http://localhost:8080/oops-2.0.0-SNAPSHOT/rest -H "Content-Type: application/xml" -d @example-request.xml
```

The answer should be:

```ttl
PREFIX oops: <http://oops.linkeddata.es/def#>

<http://oops.linkeddata.es/data/5b610be9-7e7d-40a9-a04d-72b6a6cea1a9>
        a                               oops:pitfall;
        oops:hasCode                    "P10 - Missing disjoint-ness";
        oops:hasDescription             "The ontology lacks disjoint axioms between classes or between properties that should be defined as disjoint. This pitfall is related to the guidelines provided in [6], [2] and [7].";
        oops:hasImportanceLevel         "IMPORTANT";
        oops:hasName                    "Missing disjoint-ness";
        oops:hasNumberAffectedElements  1 .

<http://oops.linkeddata.es/data/9ed73d6b-e820-4cc5-b044-4a78a9ef7861>
        a                oops:response;
        oops:hasPitfall  <http://oops.linkeddata.es/data/5b610be9-7e7d-40a9-a04d-72b6a6cea1a9> .
```

For convenience, it is also possible to run:

```shell
./run/example-request.sh
```

It is also possible to execute the tests by means of the service:

```shell
./run/test-pitfall-service.sh P02
```

## Similar projects

- [Eyeball]

[linter]: https://en.wikipedia.org/wiki/Lint_(software)
[Eyeball]: https://codeberg.org/elevont/eyeball
[RDF]: https://en.wikipedia.org/wiki/Resource_Description_Framework
[OWL]: https://en.wikipedia.org/wiki/Web_Ontology_Language
[Ontology]: https://en.wikipedia.org/wiki/Ontology_(information_science)
