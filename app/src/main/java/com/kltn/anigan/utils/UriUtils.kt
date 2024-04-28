package com.kltn.anigan.utils;

import android.net.Uri;

class UriUtils {

    companion object {

        fun encodeUri(uriString: String): String {
            val uri = Uri.parse(uriString)
            val encodedUriString = Uri.encode(uri.lastPathSegment)
            return uriString.replace(uri.lastPathSegment ?: "", encodedUriString)
        }
    }
}
