package eu.timerertim.downlomatic.utils.logging

fun main() {
    Log.wtf("Test")
    Log.fileLogging = true
    test()
    Log.fileVerbosity = Level.ASSERT
    test()
    Log.fileLogging = false
    Log.consoleVerbosity = Level.ASSERT
    test()
    Log.i("Info message")
    Log.consoleVerbosity = Level.DEBUG
    Log.d("Debug message")
}

fun test() {
    Log.wtf("Another message", IllegalArgumentException("Shit happens"))
    Log.f("Some fatal error occurred", ArrayIndexOutOfBoundsException("Death"))
}
