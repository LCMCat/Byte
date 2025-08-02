// UuidCodec.kt
package tech.ccat.byte.storage

import org.bson.*
import org.bson.codecs.*
import org.bson.codecs.configuration.CodecRegistry
import java.util.*

class UuidCodec : Codec<UUID> {
    override fun encode(writer: BsonWriter, value: UUID, encoderContext: EncoderContext) {
        writer.writeBinaryData(BsonBinary(value))
    }

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): UUID {
        return reader.readBinaryData().asUuid()
    }

    override fun getEncoderClass(): Class<UUID> {
        return UUID::class.java
    }
}