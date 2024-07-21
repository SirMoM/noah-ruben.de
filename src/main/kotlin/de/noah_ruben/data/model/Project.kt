package de.noah_ruben.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Project(
    val stars: Int,
    val topics: List<String>,
    val languages: List<String>,
    val releases: String,
    val name: String,
    val description: String,
    val githubLink: String,
    val link: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastModified: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val created: LocalDateTime,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDateTime::class)
class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.YYYY")

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(dateFormatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime = LocalDateTime.parse(decoder.decodeString(), dateFormatter)
}
