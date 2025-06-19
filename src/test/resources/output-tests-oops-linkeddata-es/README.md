<!--
SPDX-FileCopyrightText: 2025 Pieter Hijma <info@pieterhijma.net>

SPDX-License-Identifier: Apache-2.0
-->

# Output for regression tests

## Context

This implementation of OOPS! has been updated to the latest software packages
and has been released as open source.  As a result, the software has undergone
refactoring which always brings the risk of regressions compared to the old
version.  This directory contains the output for various test-cases of the old
version that is still accessible on <https://oops.linkeddata.es> as of June
2025.

## Contents

This directory contains the output of various tests-cases for the pitfalls.
The test-cases are defined in `src/test/resources/data/input` and are run on
<https://oops.linkeddata.es/> in the period of May and June 2025.

The output of these test-cases run on the old version is stored in this
directory.  It serves as a baseline for the output of the test-cases run in the
new version.  The two outputs have a different representation and are carefully
compared to be equivalent.

The output of the current version is stored in `src/test/resources/data/output`
and as such `src/test/resources/data/{input,output}` define the regression
tests.  The regression tests can be run with:

```sh
mvn test
```

