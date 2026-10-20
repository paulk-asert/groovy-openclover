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

class PricingTest {
    @Test
    void smallIsCheapest() {
        assert Pricing.basePrice(SMALL) < Pricing.basePrice(MEDIUM)
    }

    @Test
    void largeBasePrice() {
        assert Pricing.basePrice(LARGE) == 4.90
    }

    @Test
    void plantMilkCostsExtra() {
        assert Pricing.extraPrice('oat') == 0.60
    }

    @Test
    void extraShot() {
        assert Pricing.extraPrice('shot') == 0.90
    }

    @Test
    void priceIncludesExtras() {
        assert Pricing.price(Drink.of('Latte', MEDIUM, 'soy', 'shot')) == 5.70
    }
}
