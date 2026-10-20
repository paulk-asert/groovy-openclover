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
package coffee

import spock.lang.Specification

import static coffee.Size.*

class ReceiptSpec extends Specification {
    def "a receipt lists each drink and the total"() {
        given:
        def order = new Order() << Drink.of('Cappuccino', MEDIUM, 'shot')

        expect:
        Receipt.render(order) == '''\
            M Cappuccino (shot)         5.10
            Total                       5.10'''.stripIndent()
    }

    def "a receipt shows any discount"() {
        given:
        def order = new Order()
        3.times { order << Drink.of('Espresso', SMALL) }

        when:
        def lines = Receipt.render(order).readLines()

        then:
        lines.size() == 5
        lines[3] == 'Discount                    -1.05'
        lines[4].endsWith('9.45')
    }
}
