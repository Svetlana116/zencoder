package com.zencoder.tst

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.launcher.Ide
import com.intellij.remoterobot.launcher.IdeDownloader
import com.intellij.remoterobot.launcher.IdeLauncher
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.stepsProcessing.StepLogger
import com.intellij.remoterobot.stepsProcessing.StepWorker
import com.intellij.remoterobot.utils.Locators
import com.intellij.remoterobot.utils.Locators.XpathProperty.SIMPLE_CLASS_NAME
import com.intellij.remoterobot.utils.hasSingleComponent
import com.intellij.remoterobot.utils.waitFor
import okhttp3.OkHttpClient
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestWatcher
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

@ExtendWith(LauncherTest.IdeTestWatcher::class)
@Timeout(value = 15, unit = TimeUnit.MINUTES)
class LauncherTest {
    companion object {
        private var ideaProcess: Process? = null
        private var tmpDir: Path = Files.createTempDirectory("launcher")
        private lateinit var remoteRobot: RemoteRobot

        @BeforeAll
        @JvmStatic
        fun startIdea() {
            StepWorker.registerProcessor(StepLogger())

            val client = OkHttpClient()
            remoteRobot = RemoteRobot("http://127.0.0.1:8580", client)
            val ideDownloader = IdeDownloader(client)
            ideaProcess = IdeLauncher.launchIde(
                ideDownloader.downloadAndExtractLatestEap(Ide.IDEA_ULTIMATE, tmpDir),
                mapOf("robot-server.port" to 8580),
                emptyList(),
                listOf(ideDownloader.downloadRobotPlugin(tmpDir)),
                tmpDir
            )
            waitFor(Duration.ofSeconds(120), Duration.ofSeconds(5)) {
                remoteRobot.callJs<Boolean>("true")
            }
        }

        @AfterAll
        @JvmStatic
        fun cleanUp() {
            ideaProcess?.destroy()
            tmpDir.toFile().deleteRecursively()
        }
    }

    @Test
    fun test() {
        remoteRobot.find<CommonContainerFixture>(
            Locators.byProperties(SIMPLE_CLASS_NAME to "FlatWelcomeFrame"),
            Duration.ofSeconds(20)
        ).run {
            val newProjectButtonLocator = byXpath("""//div[contains(@defaulticon, 'createNewProject') or (@accessiblename='New Project' and @class='JBOptionButton')]""")
            Assertions.assertTrue(hasSingleComponent(newProjectButtonLocator), "New Project button not found")
        }
    }

    class IdeTestWatcher : TestWatcher {
        override fun testFailed(context: ExtensionContext, cause: Throwable?) {
            ImageIO.write(remoteRobot.getScreenshot(), "png", File("build/reports", "${context.displayName}.png"))
        }
    }
}
