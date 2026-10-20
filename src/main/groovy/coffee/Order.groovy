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

import java.math.RoundingMode

class Order {
    final List<Drink> drinks = []
    int loyaltyStamps

    Order leftShift(Drink drink) {
        drinks << drink
        this
    }

    BigDecimal subtotal() {
        drinks.sum(0.0) { Pricing.price(it) }
    }

    BigDecimal discount() {
        if (loyaltyStamps >= 10) {
            return cheapest()
        }
        drinks.size() >= 3 ? (subtotal() * 0.10).setScale(2, RoundingMode.HALF_UP) : 0.0
    }

    BigDecimal cheapest() {
        drinks ? drinks.collect(Pricing::price).min() : 0.0
    }

    BigDecimal total() {
        (subtotal() - discount()).setScale(2, RoundingMode.HALF_UP)
    }
}
