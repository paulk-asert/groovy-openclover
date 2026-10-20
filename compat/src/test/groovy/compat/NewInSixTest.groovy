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

class NewInSixTest {
    @Test void valAndRestBinding() { assert NewInSix.valAndRestBinding([1, 2, 3]) == 3 }
    @Test void mapDestructuring() { assert NewInSix.mapDestructuring([name: 'Al', age: 20]) == 'Al adult' }
    @Test void statementSwitch() { assert NewInSix.statementSwitch(3) == null }
    @Test void asyncAwait() { assert NewInSix.asyncAwait() == 42 }
}
