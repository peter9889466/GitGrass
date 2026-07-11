package com.gitgrass.global.security.crypto

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class TokenConverter : AttributeConverter<String, String> {

    override fun convertToDatabaseColumn(attribute: String?): String? {
        return attribute?.let { AesUtil.encrypt(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        return dbData?.let { AesUtil.decrypt(it) }
    }
}
