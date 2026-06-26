package com.solitrix.postureminder.shared

expect fun platform(): String
expect fun notify(message: String)