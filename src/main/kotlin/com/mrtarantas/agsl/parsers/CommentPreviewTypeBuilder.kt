package com.mrtarantas.agsl.parsers

import com.mrtarantas.agsl.models.PreviewType

class CommentPreviewTypeBuilder {
	operator fun invoke(type: PreviewType): String {
		val value = when (type) {
			PreviewType.Number -> "number"
			PreviewType.Color -> "color"
			PreviewType.Size -> "size"
			PreviewType.Time -> "time"
		}
		return "// PreviewType: $value"
	}
}