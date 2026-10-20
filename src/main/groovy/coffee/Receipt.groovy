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

class Receipt {
    static String render(Order order) {
        val lines = order.drinks.collect { drink ->
            def (name: n, cup: c, extras: xs) = drink
            val label = xs ? "$n (${xs.join(', ')})" : n
            "${c.name()[0]} $label".padRight(28) + Pricing.price(drink)
        }
        val discount = order.discount()
        if (discount) {
            lines << 'Discount'.padRight(28) + "-$discount"
        }
        lines << 'Total'.padRight(28) + order.total()
        lines.join('\n')
    }
}
