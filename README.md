# Groovy 6 and OpenClover

Companion code for the Apache Groovy blog post
[Which tests should I run? Per-test coverage for Groovy 6 with OpenClover](https://groovy.apache.org/blog/groovy-openclover).

It shows:

* [OpenClover](https://openclover.org) 5.1.1 instrumenting a Groovy 6 code base
  (JUnit 6 tests and Spock 2.5 specifications), driven from Gradle 9
* per-test coverage: which tests executed which lines
* OpenClover's built-in *test optimization* (re-run tests touching changed files)
* a small line-level "which tests should I run?" tool, intended for AI coding agents

## Layout

| Path | What |
|---|---|
| `src/` | a small coffee-shop pricing domain with JUnit and Spock tests |
| `compat/` | a tour of Groovy 4/5 and Groovy 6 constructs, used to check OpenClover's Groovy 6 support |
| `buildSrc/src/main/groovy/openclover.gradle` | wires OpenClover's Ant tasks into Gradle |
| `tools/TestMap.groovy` | exports the OpenClover database to a portable `test-map.json` |
| `tools/AffectedTests.groovy` | maps `git diff` hunks to the tests that cover them |

## Running

Requires JDK 17+.

```
./gradlew test                 # normal, uninstrumented tests
./gradlew cloverReport         # instrumented run + HTML/XML reports in build/clover
./gradlew cloverTestMap        # instrumented run + build/clover/test-map.json
./gradlew cloverOptimizedTest  # OpenClover test optimization (file granularity)
./gradlew -q :affectedTests     # line-level selection for your uncommitted changes
./gradlew -q :affectedTests -Pjson
./gradlew -q :affectedTests -Plines=src/main/groovy/coffee/Pricing.groovy:32
```

Typical session:

```
$ ./gradlew -q :cloverTestMap
$ sed -i "s/case 'shot' -> 0.90/case 'shot' -> 1.00/" src/main/groovy/coffee/Pricing.groovy
$ ./gradlew -q :affectedTests
src/main/groovy/coffee/Pricing.groovy:32         line                     3 test(s)

Run: ./gradlew :test --tests 'coffee.ReceiptSpec.a receipt lists each drink and the total' --tests 'coffee.PricingTest.extraShot' --tests 'coffee.PricingTest.priceIncludesExtras'
```

See [AGENTS.md](AGENTS.md) for the instructions given to coding agents.
