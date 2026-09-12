import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.gradle.api.Project

private val JSON = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
    allowComments = true
    allowTrailingComma = true
    prettyPrint = true
}

@Suppress("ObjectPropertyName")
private lateinit var _replacements: Map<String, Replacement>

val Project.replacements: Map<String, Replacement> get() {
    if(!::_replacements.isInitialized) {
        val file = project.rootProject.file("replacements.json5")
        _replacements = JSON.decodeFromString(file.readText())
    }
    return _replacements
}

@Serializable
data class Replacement(
    val `when`: String,
    val alwaysActive: Boolean = false,
    val strings: List<StringReplacement> = emptyList(),
    val regex: List<RegexReplacement> = emptyList(),
) {
    init {
        check(strings.isNotEmpty() || regex.isNotEmpty()) {
            "replacement must have at least either string or regex replacements"
        }
    }
}

@Serializable(with = StringReplacementSerializer::class)
data class StringReplacement(val old: String, val new: String)

@Serializable
data class RegexReplacement(val forward: RegexValue, val backward: RegexValue)

@Serializable
data class RegexValue(
    @Serializable(with = RegexSerializer::class)
    val pattern: Regex,
    val replacement: String,
)

private object RegexSerializer : KSerializer<Regex> {
    override val descriptor = PrimitiveSerialDescriptor("Regex", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Regex) {
        encoder.encodeString(value.pattern)
    }

    override fun deserialize(decoder: Decoder): Regex {
        return Regex(decoder.decodeString())
    }
}

internal object StringReplacementSerializer : KSerializer<StringReplacement> {
    private val backing = serializer<List<String>>()
    override val descriptor = SerialDescriptor("StringReplacement", backing.descriptor)

    override fun serialize(encoder: Encoder, value: StringReplacement) {
        encoder.encodeSerializableValue(backing, listOf(value.old, value.new))
    }

    override fun deserialize(decoder: Decoder): StringReplacement {
        val deserialized = decoder.decodeSerializableValue(backing)
        require(deserialized.size == 2) {
            "string replacement must have exactly 2 string values, got ${deserialized.size}"
        }
        return StringReplacement(deserialized[0], deserialized[1])
    }
}
