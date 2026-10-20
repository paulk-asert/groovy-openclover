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

// Exports OpenClover's per-test coverage as a plain JSON "test map":
// for every source file, which tests executed each method, statement
// and branch. Run from the repository root.
// Usage: TestMap.groovy <clover.db> <project path, '' for root> <out.json>

import groovy.json.JsonOutput
import org.openclover.core.CloverDatabase
import org.openclover.core.CoverageDataSpec
import org.openclover.core.api.registry.*

def (dbPath, project, outFile) = args
def db = CloverDatabase.loadWithCoverage(dbPath, new CoverageDataSpec())
def root = new File('.').canonicalFile.toPath()
def git = { String... cmd -> (['git', *cmd]).execute().text.trim() }

def tests = [:]                       // TestCaseInfo -> id
def testId = { TestCaseInfo t -> tests.computeIfAbsent(t) { tests.size() } }
def hits = { CoverageDataRange r -> db.getTestHits(r).collect(testId).sort() }

def files = db.fullModel.getFiles(HasMetricsFilter.ACCEPT_ALL).collectEntries { FileInfo f ->
    def regions = f.gatherSourceRegions()
    def methods = regions.findAll { it instanceof MethodInfo }.collect { MethodInfo m ->
        [m.startLine, m.endLine, m.simpleName, hits(m)]
    }
    def statements = regions.findAll { it instanceof StatementInfo || it instanceof BranchInfo }.collect {
        [it.startLine, it.endLine, hits(it)]
    }
    def path = root.relativize(f.physicalFile.toPath()).toString()
    [path, [test      : f.testFile,
            tests     : (methods*.last() + statements*.last()).flatten().unique().sort(),
            methods   : methods.sort { it[0] },
            statements: statements.unique().sort { it[0] }]]
}

// Gradle re-runs the instrumented tests whenever their inputs change,
// so the database describes HEAD plus any uncommitted changes under src
def baseline = [commit: git('rev-parse', 'HEAD'),
                dirty : !git('status', '--porcelain', '--', project ? "$project/src" : 'src').isEmpty()]

new File(outFile).text = JsonOutput.prettyPrint(JsonOutput.toJson(
        project : project,
        baseline: baseline,
        tests   : tests.collect { t, id ->
            [id: id, className: t.runtimeTypeName, name: t.testName, passed: t.success]
        }.sort { it.id },
        files   : files))
println "Wrote ${tests.size()} tests across ${files.size()} files to $outFile"
