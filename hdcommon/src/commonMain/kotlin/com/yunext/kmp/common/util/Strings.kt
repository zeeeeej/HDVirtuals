package com.yunext.kmp.common.util

expect fun hdMD5(text: String, upperCase: Boolean = false): String?
expect fun hdRandomString(length:Int): String
