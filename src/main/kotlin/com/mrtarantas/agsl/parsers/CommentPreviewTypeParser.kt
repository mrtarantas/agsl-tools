package com.mrtarantas.agsl.parsers

import com.mrtarantas.agsl.models.PreviewType

class CommentPreviewTypeParser(
	private val regex: Regex = Regex("""//\s*PreviewType:\s*(\w+)"""),
) {

	operator fun invoke(comment: String): PreviewType? {
		if (!regex.matches(comment)) return null

		val value = runCatching { regex.find(comment)?.groups?.get(1)?.value }.getOrNull()
		return when (value?.trim()) {
			"time" -> PreviewType.Time
			"size" -> PreviewType.Size
			"color" -> PreviewType.Color
			"number" -> PreviewType.Number
			else -> null
		}
	}
}