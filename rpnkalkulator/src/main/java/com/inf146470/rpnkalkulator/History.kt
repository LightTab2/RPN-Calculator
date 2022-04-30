package com.inf146470.rpnkalkulator

import java.math.BigDecimal
import ch.obermuhlner.math.big.kotlin.bigdecimal.valueOf

class History (var action: Action, var stack1: BigDecimal = valueOf(0),
                       var stack2: BigDecimal = valueOf(0))