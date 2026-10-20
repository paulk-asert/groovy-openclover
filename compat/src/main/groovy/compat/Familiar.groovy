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
package compat

import groovy.transform.CompileStatic

// Constructs familiar from Groovy 4 and 5
class Familiar {
    static String classicSwitch(int n) {
        switch (n) {
            case 0: return 'zero'
            case 1: return 'one'
            default: return 'other'
        }
    }

    static String switchExpression(int n) {
        def r = switch (n) {
            case 0 -> 'zero'
            case 1 -> 'one'
            default -> 'other'
        }
        r
    }

    @CompileStatic
    static String staticSwitchExpression(int n) {
        String r = switch (n) {
            case 0 -> 'zero'
            default -> 'other'
        }
        r
    }

    static String ternary(int n) { n > 0 ? 'pos' : 'nonpos' }

    static String elvis(String s) { s ?: 'empty' }

    static int closures(List<Integer> xs) { xs.findAll { it % 2 == 0 }.sum(0) { it * 2 } }

    static int lambdas(List<Integer> xs) { xs.stream().filter(x -> x > 1).map(Integer::valueOf).reduce(0, Integer::sum) }
}

record Point(int x, int y) {
    int manhattan() { Math.abs(x) + Math.abs(y) }
}

trait Greeter {
    String greet(String name) { name ? "Hi $name" : 'Hi' }
}

class Host implements Greeter {}
