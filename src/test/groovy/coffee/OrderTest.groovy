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

import org.junit.jupiter.api.Test

import static coffee.Size.*

class OrderTest {
    @Test
    void emptyOrderIsFree() {
        assert new Order().total() == 0.00
    }

    @Test
    void subtotalAddsDrinks() {
        def order = new Order() << Drink.of('Flat White', SMALL) << Drink.of('Mocha', LARGE)
        assert order.subtotal() == 8.40
        assert order.discount() == 0.0
    }

    @Test
    void threeOrMoreDrinksGetTenPercentOff() {
        def order = new Order()
        3.times { order << Drink.of('Espresso', SMALL) }
        assert order.total() == 9.45
    }

    @Test
    void tenStampsEarnTheCheapestDrinkFree() {
        def order = new Order(loyaltyStamps: 10) << Drink.of('Latte', LARGE, 'oat') << Drink.of('Espresso', SMALL)
        assert order.total() == 5.50
    }
}
