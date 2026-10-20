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

import org.junit.jupiter.api.Test

class FamiliarTest {
    @Test void classicSwitch() { assert Familiar.classicSwitch(1) == 'one' }
    @Test void switchExpression() { assert Familiar.switchExpression(1) == 'one' }
    @Test void staticSwitchExpression() { assert Familiar.staticSwitchExpression(0) == 'zero' }
    @Test void ternary() { assert Familiar.ternary(1) == 'pos' }
    @Test void elvis() { assert Familiar.elvis('') == 'empty' }
    @Test void closures() { assert Familiar.closures([1, 2, 3, 4]) == 12 }
    @Test void lambdas() { assert Familiar.lambdas([1, 2, 3]) == 5 }
    @Test void record() { assert new Point(1, -2).manhattan() == 3 }
    @Test void trait() { assert new Host().greet('Bob') == 'Hi Bob' }
}
