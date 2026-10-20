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

import static coffee.Size.*

class Pricing {
    static BigDecimal basePrice(Size size) {
        switch (size) {
            case SMALL -> 3.50
            case MEDIUM -> 4.20
            case LARGE -> 4.90
        }
    }

    static BigDecimal extraPrice(String extra) {
        val price = switch (extra) {
            case 'oat', 'soy' -> 0.60
            case 'shot' -> 0.90
            default -> 0.40
        }
        price
    }

    static BigDecimal price(Drink drink) {
        basePrice(drink.cup()) + drink.extras().sum(0.0) { extraPrice(it) }
    }
}
