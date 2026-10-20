/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Answers "which tests should I run?" for the changes made since the
// test map was built. Each changed hunk is resolved as precisely as the
// map allows: statement -> enclosing method -> whole file -> everything.
// Run from the repository root.
// Usage: AffectedTests.groovy <test-map.json> [--json | --filters] [--lines path:from[-to]]...
//   --json     compact summary for agents (counts and classes, not every test name)
//   --filters  the full selection, read by the testAffected Gradle task

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

def opts = args.toList()
def map = new JsonSlurper().parse(new File(opts.pop()))
def asJson = opts.remove('--json')
def asFilters = opts.remove('--filters')
def git = { String... cmd -> (['git', *cmd]).execute().text }
def prefix = map.project ? "$map.project/" : ''
// changes outside these paths can't affect this project's tests
def watched = ['--', "${prefix}src", "${prefix}build.gradle", 'settings.gradle', 'gradle.properties', 'buildSrc', 'gradle']

record Hunk(String file, int from, int to, boolean insertion) {
    String toString() { from ? "$file:$from${to > from ? "-$to" : ''}${insertion ? ' (insert)' : ''}" : "$file (new file)" }
}

List<Hunk> hunks = []
if (opts.contains('--lines')) {
    opts.withIndex().findAll { o, i -> o == '--lines' }.each { o, i ->
        def (file, range) = opts[i + 1].split(':')
        def (from, to) = (range.split('-')*.toInteger() * 2)
        hunks << new Hunk(file, from, to, false)
    }
} else {
    // compare the working tree with the commit the coverage was recorded against
    String file = null
    git('diff', '-U0', '--no-color', map.baseline.commit, *watched).eachLine { line ->
        if (line.startsWith('--- ')) file = line == '--- /dev/null' ? null : line.substring(6)
        def m = line =~ /^@@ -(\d+)(?:,(\d+))? \+\d+(?:,\d+)? @@/
        if (file && m) {
            int start = m[0][1].toInteger(), count = (m[0][2] ?: '1').toInteger()
            hunks << (count ? new Hunk(file, start, start + count - 1, false)
                            : new Hunk(file, start, start + 1, true))
        }
    }
    (git('diff', '--name-only', '--diff-filter=A', map.baseline.commit, *watched).readLines() +
            git('ls-files', '--others', '--exclude-standard', *watched).readLines()).each {
        hunks << new Hunk(it, 0, 0, true)
    }
}

def encloses = { e, Hunk h -> e[0] <= h.from() && e[1] >= h.to() }

def resolve = { Hunk h ->
    def entry = map.files[h.file()]
    if (!entry) {
        def source = h.file() =~ /^${prefix}src\/(main|test)\/\w+\/(.*)\.(groovy|java)$/
        if (source && source[0][1] == 'test') return [level: 'new-test', filters: [source[0][2].replace('/', '.')]]
        if (source) return [level: 'new-code', tests: []]
        return [level: 'unmapped', all: true]
    }
    // for each changed line, the innermost statement(s) covering it
    def statements = h.insertion() ? [] : (h.from()..h.to()).collectMany { line ->
        def covering = entry.statements.findAll { it[0] <= line && it[1] >= line }
        def span = covering.collect { it[1] - it[0] }.min()
        covering.findAll { it[1] - it[0] == span }
    }
    def signatures = entry.methods.findAll { it[0] in h.from()..h.to() }
    if (statements || signatures) {
        return [level: 'line', tests: (statements*.last() + signatures*.last()).flatten().unique()]
    }
    def method = entry.methods.findAll { encloses(it, h) }.min { it[1] - it[0] }
    method ? [level: "method ${method[2]}", tests: method.last()] : [level: 'file', tests: entry.tests]
}

def results = hunks.collect { h -> [hunk: h, *: resolve(h)] }
def runAll = results.any { it.all }
def selected = results.collectMany { it.tests ?: [] }.unique().sort().collect { map.tests[it] }
def filters = runAll ? [] : (selected.collect { "${it.className}.${it.name}" } +
        results.collectMany { it.filters ?: [] }).unique()
def uncovered = results.findAll { it.tests == [] }*.hunk*.toString()
def run = "./gradlew -q ${map.project ? ":${map.project.replace('/', ':')}" : ''}:testAffected"
def classes = runAll ? [] : (selected*.className + results.collectMany { it.filters ?: [] }).unique()

if (asFilters) {
    println JsonOutput.toJson(runAll: runAll, filters: filters, uncovered: uncovered)
    return
}
if (asJson) {
    // an agent re-reads every tool result on every later turn, so keep this short
    println JsonOutput.prettyPrint(JsonOutput.toJson(
            baseline: map.baseline, runAll: runAll, testCount: filters.size(), testClasses: classes,
            uncovered: uncovered, run: runAll || filters ? run : null,
            hunks: results.collect { [location: it.hunk.toString(), level: it.level, tests: it.tests?.size()] }))
    return
}

if (map.baseline.dirty) println 'Warning: test map was built from uncommitted changes; line numbers may drift'
if (!hunks) { println "No changes since ${map.baseline.commit.take(10)}"; return }
results.each { r ->
    def count = r.all ? 'all tests' : r.filters ? r.filters.join(', ') : "${r.tests.size()} test(s)"
    println "${r.hunk.toString().padRight(52)} ${r.level.padRight(18)} $count"
}
uncovered.each { println "No test covers: $it" }
println()
if (!runAll && !filters) { println 'No tests need to run'; return }
filters.each { println "  $it" }
println "Run: $run"
