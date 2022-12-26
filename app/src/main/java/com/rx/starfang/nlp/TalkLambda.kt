package com.rx.starfang.nlp

import android.text.TextUtils
import android.util.Log
import com.rx.starfang.database.room.memo.MemoRepository
import org.mozilla.javascript.Context
import org.mozilla.javascript.ContextFactory
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.debug.DebugFrame
import org.mozilla.javascript.debug.DebuggableScript
import org.mozilla.javascript.debug.Debugger
import java.util.Timer
import java.util.TimerTask
import kotlin.math.max

/*
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.jsr223.KotlinStandardJsr223ScriptTemplate
import kotlin.script.experimental.jvm.util.KotlinJars
 */

class TalkLambda {

    enum class CmdNum {
        MEMO, CALC
    }

    companion object {

        private val TAG = TalkLambda::class.java.simpleName


        private val cmdKor2HashMap = HashMap<String, CmdNum>(
            mapOf(
                "메모" to CmdNum.MEMO,
                "계산" to CmdNum.CALC
            )
        )

        private val cmdEngHashMap = HashMap<String, CmdNum>(
            mapOf(
                "memo" to CmdNum.MEMO,
                "calc" to CmdNum.CALC
            )
        )

        suspend fun process(
            content: String,
            sender: String,
            memoRepo: MemoRepository,
            time: Long
        ): List<String>? {
            var req = content

            var cmdNum: CmdNum? = null
            val pivotIndex = max(0, req.length - 2)
            val key = req.substring(pivotIndex)
            cmdKor2HashMap[key]?.run {
                cmdNum = this
                req = req.substring(0, pivotIndex).trim()
            }

            when (cmdNum) {
                CmdNum.MEMO -> {
                    val lineRegex = System.getProperty("line.separator")
                    lineRegex?.run {
                        val lines: Array<String> = req.split(toRegex()).toTypedArray()
                        if (lines.isNotEmpty()) {
                            val memoName: String = lines[0].trim()
                            return List(1) {
                                if (lines.size > 1) {
                                    memoRepo.insertMemo(
                                        memoName,
                                        TextUtils.join(
                                            "\r\n",
                                            listOf(*lines).subList(1, lines.size)
                                        )
                                            .trim(), sender, time
                                    )

                                } else {
                                    memoRepo.deleteMemo(memoName)
                                }
                            }
                        }
                    }
                }
                CmdNum.CALC -> {
                    return try {
                        val debugger = ObservingDebugger()
                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                debugger.disconnect()
                            }
                        }, 2 * 1000)

                        val ctx = ContextFactory().enterContext().apply {
                            setDebugger(debugger, 0)
                            isGeneratingDebug = true
                            //instructionObserverThreshold = 6000
                            optimizationLevel = -1
                        }

                        val scope = ctx.initSafeStandardObjects()
                        val resultObj = ctx.evaluateString(scope, req, "<cmd>", 1, null)

                        //val manager = ScriptEngineManager()
                        //val engine = manager.getEngineByName("rhino")
                        //Log.d(TAG, "engine name: " + engine.factory.engineName)
                        listOf(resultObj.toString())
                        //engine.eval(req).toString())


                    } catch (e: Exception) {
                        listOf(e.localizedMessage ?: e.toString())
                    }


                }
                else -> {
                    return memoRepo.searchMemos(req)
                }
            }
            return null
        }
    }

    //https://stackoverflow.com/questions/57275093/how-to-kill-a-rhino-script
    class ObservingDebugger : Debugger {
        private var connected = true
        private var debugFrame: DebugFrame? = null

        fun disconnect() {
            connected = false
            if (debugFrame != null && debugFrame is ObservingDebugFrame)
                (debugFrame as ObservingDebugFrame).isDisconnected = !connected
        }

        override fun handleCompilationDone(
            cx: Context?,
            fnOrScript: DebuggableScript?,
            source: String?
        ) {
        }

        override fun getFrame(cx: Context?, fnOrScript: DebuggableScript?): DebugFrame {
            if (debugFrame == null) {
                debugFrame = ObservingDebugFrame().apply {
                    isDisconnected = !connected
                }
            }
            return debugFrame as DebugFrame
        }

        class ObservingDebugFrame : DebugFrame {
            var isDisconnected = false
            override fun onEnter(
                cx: Context?,
                activation: Scriptable?,
                thisObj: Scriptable?,
                args: Array<out Any>?
            ) {
            }

            override fun onLineChange(cx: Context?, lineNumber: Int) {
                //Log.d(TAG, "line$lineNumber executed")
                if (isDisconnected)
                    throw RuntimeException("Script Engine Disconnected: timeout")
            }

            override fun onExceptionThrown(cx: Context?, ex: Throwable?) {
            }

            override fun onExit(cx: Context?, byThrow: Boolean, resultOrException: Any?) {
                Log.d(TAG, "Exit Script")
            }

            override fun onDebuggerStatement(cx: Context?) {
            }

        }

    }

    class ScriptDynamicScopeFactory : ContextFactory() {
        @Override
        override fun makeContext(): Context {
            val ctx = Context.enter()
            ctx.instructionObserverThreshold = 10000
            return ctx
        }

        override fun observeInstructionCount(cx: Context?, instructionCount: Int) {
            if (instructionCount > 5000) {
                Log.d(TAG, "over 5000")
            }
            //val executionTime: Long = (currTime - ((ScriptContext)cx).startTime())
        }
    }

    /*
    class StarfangScriptEngineFactory: KotlinJsr223JvmScriptEngineFactoryBase(){
        override fun getScriptEngine(): ScriptEngine =
            KotlinJsr223JvmDaemonCompileScriptEngine(
                this,
                KotlinJars.compilerWithScriptingClasspath,
                scriptCompilationClasspathFromContext("kotlin-script-unit.jar", wholeClasspath = true),
                //listOf(KotlinJars.scriptRuntime, KotlinJars.stdlib, ), // !!! supply the script classpath here
                KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
                { ctx, types -> ScriptArgsWithTypes(arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)), types ?: emptyArray()) },
                arrayOf(Bindings::class)
            )

    }
    */


}