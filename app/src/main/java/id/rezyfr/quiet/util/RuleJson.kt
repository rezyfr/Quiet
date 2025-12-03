package id.rezyfr.quiet.util

import id.rezyfr.quiet.domain.model.BatchAction
import id.rezyfr.quiet.domain.model.BluetoothCriteria
import id.rezyfr.quiet.domain.model.CallCriteria
import id.rezyfr.quiet.domain.model.CooldownAction
import id.rezyfr.quiet.domain.model.DismissAction
import id.rezyfr.quiet.domain.model.PostureCriteria
import id.rezyfr.quiet.domain.model.RuleAction
import id.rezyfr.quiet.domain.model.RuleCriteria
import id.rezyfr.quiet.domain.model.TimeCriteria
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object RuleJson {
    val ruleSerializersModule = SerializersModule {
        polymorphic(RuleCriteria::class) {
            subclass(TimeCriteria::class, TimeCriteria.serializer())
            subclass(CallCriteria::class, CallCriteria.serializer())
            subclass(BluetoothCriteria::class, BluetoothCriteria.serializer())
            subclass(PostureCriteria::class, PostureCriteria.serializer())
        }
        polymorphic(RuleAction::class) {
            subclass(CooldownAction::class, CooldownAction.serializer())
            subclass(DismissAction::class, DismissAction.serializer())
            subclass(BatchAction::class, BatchAction.serializer())
        }
    }

    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
        classDiscriminator = "type"
        serializersModule = ruleSerializersModule
    }

    fun encodeApps(apps: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), apps)

    fun decodeApps(raw: String): List<String> =
        if (raw.isBlank()) {
            emptyList()
        } else {
            json.decodeFromString(ListSerializer(String.serializer()), raw)
        }

    fun encodeKeywords(keywords: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), keywords)

    fun decodeKeywords(raw: String): List<String> =
        if (raw.isBlank()) {
            emptyList()
        } else {
            json.decodeFromString(ListSerializer(String.serializer()), raw)
        }

    private val criteriaListSerializer =
        ListSerializer(PolymorphicSerializer(RuleCriteria::class))

    fun encodeCriteria(criteria: List<RuleCriteria>): String =
        json.encodeToString(criteriaListSerializer, criteria)

    fun decodeCriteria(raw: String): List<RuleCriteria> =
        if (raw.isBlank()) {
            emptyList()
        } else {
            json.decodeFromString(criteriaListSerializer, raw)
        }

    private val actionSerializer = PolymorphicSerializer(RuleAction::class)

    fun encodeAction(action: RuleAction): String =
        json.encodeToString(actionSerializer, action)

    fun decodeAction(raw: String): RuleAction =
        json.decodeFromString(actionSerializer, raw)
}
