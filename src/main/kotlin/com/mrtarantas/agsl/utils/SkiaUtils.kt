package com.mrtarantas.agsl.utils

import org.jetbrains.skia.impl.Managed

fun Managed.tryClose() {
	try {
		if (!isClosed)
			close()
	} catch (t: Throwable) {
		t.printStackTrace()
	}
}