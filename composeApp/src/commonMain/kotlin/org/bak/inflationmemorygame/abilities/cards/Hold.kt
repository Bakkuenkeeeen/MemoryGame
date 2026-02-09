package org.bak.inflationmemorygame.abilities.cards

import org.bak.inflationmemorygame.abilities.Abilities
import org.bak.inflationmemorygame.abilities.AbilityCard
import org.bak.inflationmemorygame.abilities.EarnedAbility
import org.bak.inflationmemorygame.abilities.EffectHandlerResults
import org.bak.inflationmemorygame.abilities.buildEffectHandlerResults
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityEarnEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnAbilityLostEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnCardFlipEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnLevelUpEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnPairMatchEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandler
import org.bak.inflationmemorygame.abilities.handlers.OnTurnEndEffectHandlerParam
import org.bak.inflationmemorygame.abilities.handlers.OnTurnStartEffectHandler
import org.bak.inflationmemorygame.logs.Logs

class HoldCard : AbilityCard.NoFieldEffect(
    ability = Abilities.Hold,
    earnedAbilityFactory = ::HoldAbility
)

// TODO
//  ホールドの効果で、とっておきが選ばれないようにする
class HoldAbility : EarnedAbility(ability = Abilities.Hold) {

    private val holdCards = mutableListOf<Long>()

    override val descriptionParams: Array<Any> get() = arrayOf(level)

    override fun onEarn(): OnAbilityEarnEffectHandler? = null
    override fun onLevelUp(amount: Int) = OnLevelUpEffectHandler.NoAction
    override fun onTurnStart(): OnTurnStartEffectHandler? = null
    override fun onCardFlip(): OnCardFlipEffectHandler? = null
    override fun onPairMatch(): OnPairMatchEffectHandler? = null

    override fun onTurnEnd(): OnTurnEndEffectHandler {
        changeEffectState(to = EffectState.Active)
        return object : OnTurnEndEffectHandler {
            override val priority: Int = OnTurnEndEffectHandler.PRIORITY_HOLD
            override suspend fun dispatch(param: OnTurnEndEffectHandlerParam): List<EffectHandlerResults> {
                // 前のターンまでの結果は、ここでクリア
                // (ターン内に別な効果でレベルが下がったときの参照用に残しておく必要がある)
                holdCards.clear()
                holdCards.addAll(
                    param.stageState.cards
                        .filter { it.isFaceUp && !it.isMatched }
                        .shuffled()
                        .take(level)
                        .map { it.instanceId }
                )
                return buildEffectHandlerResults {
                    if (holdCards.isEmpty()) {
                        printLog(Logs.effectMistake { displayName })
                    } else {
                        printLog(Logs.gainStatusEffect { displayName })
                        holdCards.forEach {
                            keepFlipped(targetInstanceId = it)
                        }
                    }
                }
            }
        }
    }

    override fun onLevelDown(): OnAbilityLostEffectHandler {
        return object : OnAbilityLostEffectHandler {
            override val priority: Int = OnAbilityLostEffectHandler.PRIORITY_HOLD_LEVEL_DOWN
            override suspend fun dispatch(param: OnAbilityLostEffectHandlerParam): List<EffectHandlerResults> {
                return buildEffectHandlerResults {
                    printLog(Logs.levelDown { displayName })
                    reverseCard(targetInstanceId = holdCards.random().also {
                        holdCards.remove(it)
                    })
                }
            }
        }
    }
}