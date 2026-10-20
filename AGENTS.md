# Instructions for coding agents

This project records per-test coverage with OpenClover, so you rarely
need to run the whole test suite after a change.

## After editing code

1. Run `./gradlew -q :affectedTests -Pjson` (or `:compat:affectedTests` for the `compat` project).
2. Read the JSON:
   * `command`: run it. It selects exactly the tests that executed the lines
     you changed (or, for lines with no recorded code such as a blank line or
     a brace, the tests of the enclosing method, then of the file).
   * `runAll: true`: a build file or other unmapped file changed, so `command`
     runs the project's full suite.
   * `uncovered`: changed lines that no test executes. Add or extend a test
     for them before declaring the task complete.
3. If `build/clover/test-map.json` is missing, run `./gradlew -q :cloverTestMap`
   first (it takes a little longer: it runs the full suite instrumented).

## Before finishing

Run `./gradlew test` once. Selection is a fast feedback loop, not a
replacement for the full suite: a change can affect a test that never
executed the old line (for example, a new branch, or a changed constant
read elsewhere).

## Refreshing the map

The map describes the commit recorded in `build/clover/baseline.properties`
and is compared against your working tree with `git diff`. After committing,
refresh it with `./gradlew -q :cloverTestMap`.
