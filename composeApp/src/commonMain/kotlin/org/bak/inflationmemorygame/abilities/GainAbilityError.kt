package org.bak.inflationmemorygame.abilities

import inflationmemorygame.composeapp.generated.resources.Res
import inflationmemorygame.composeapp.generated.resources.log_totteoki_match_too_early
import org.bak.inflationmemorygame.logs.Logs
import org.jetbrains.compose.resources.stringResource

sealed class GainAbilityError(val log: Logs) : Exception() {
    class TotteokiMatchTooEarlyError : GainAbilityError(log = Logs(category = Logs.Category.Match) {
        stringResource(Res.string.log_totteoki_match_too_early)
    })
}