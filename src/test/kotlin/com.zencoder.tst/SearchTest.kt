package com.zencoder.tst

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.ContainerFixture
import com.intellij.remoterobot.fixtures.dataExtractor.txt
import com.intellij.remoterobot.search.locators.byXpath
import com.intellij.remoterobot.utils.keyboard
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.event.KeyEvent
import java.util.concurrent.TimeUnit

class SearchTest {
    private val robot = RemoteRobot("http://127.0.0.1:8580")
    private val ide = robot.find(ContainerFixture::class.java, byXpath("//div[@class='ide-frame']"))

    @BeforeEach
    fun setUp() {
        Thread.sleep(TimeUnit.SECONDS.toMillis(5))
    }

    @AfterEach
    fun tearDown() {
        ide.find(ComponentFixture::class.java, byXpath("//div[@class='exit']")).click()
    }

    @Test
    fun testProjectSearch() {
        val searchString = "Test"

        robot.keyboard {
            hotKey(KeyEvent.VK_META, KeyEvent.VK_SHIFT, KeyEvent.VK_F)
        }

        val searchField = ide.find(ComponentFixture::class.java, byXpath("//div[@class='SearchTextField']"))
        searchField.txt(searchString)

        robot.keyboard {
            key(KeyEvent.VK_ENTER)
        }

        Thread.sleep(TimeUnit.SECONDS.toMillis(5))

        val searchResults =
            ide.find(CommonContainerFixture::class.java, byXpath("//div[@class='search-results-panel']"))
        assert(
            searchResults.findAll<ComponentFixture>(byXpath("//div[@class='file-path']")).isNotEmpty()
        ) { "Search results are empty" }
    }
}