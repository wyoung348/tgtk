package cafe.dragons.tgtk.scene

import java.io.File
import java.io.InputStreamReader

import cafe.dragons.tgtk.scene.tokens.*
import java.io.InputStream

class Scene private constructor() {
    internal val corpus = CompoundToken()

    class SceneLoader() {
        private var escaped = false
        private var builder = StringBuilder()
        private lateinit var reader: InputStreamReader
        private var scene = Scene()

        fun load(filename: String): Scene {
            File(filename).reader(Charsets.UTF_8).use {
                reader = it
                tokenize(this::static)
            }
            return scene
        }

        private fun tokenize(op: (Char) -> Boolean) {
            var builder = StringBuilder()
            var i = reader.read()
            while(i != -1) {
                if (escaped) {
                    escaped = false
                    builder.append(i.toChar())
                    continue
                }
                if (op(i.toChar())) break
                i = reader.read()
            }
        }

        // returns whether to stop or not
        private fun static(c: Char): Boolean {
            when (c) {
                '\\' -> escaped = true
                '$'  -> {
                    // finish our StaticToken
                    scene.corpus.add(StaticToken(builder.toString()))
                    builder = StringBuilder()
                    // figure out this new, exciting token
                    tokenize(this::dynamic)
                }
                else -> builder.append(c)
            }
            return false
        }

        private fun dynamic(c: Char): Boolean {
            when (c) {
                in arrayOf('\\', '$') -> return false // TODO throw exception
            }
            return false
        }
    }
}